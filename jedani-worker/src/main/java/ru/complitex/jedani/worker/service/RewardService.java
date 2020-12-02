package ru.complitex.jedani.worker.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.mapper.RewardMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov
 * 20.10.2019 11:02 AM
 */
public class RewardService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private WorkerNodeService workerNodeService;

    @Inject
    private SaleService saleService;

    @Inject
    private WorkerService workerService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private PeriodService periodService;

    @Inject
    private RewardMapper rewardMapper;

    @Inject
    private ExchangeRateService exchangeRateService;

    @Inject
    private PriceService priceService;

    public List<Reward> getRewardsBySaleId(Long saleId){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward().setSaleId(saleId)));
    }

    public BigDecimal getRewardsTotalBySaleId(Long saleId, Long rewardTypeId){
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public List<Reward> getRewards(Long workerId, Date month){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward()
                .setWorkerId(workerId)
                .setMonth(month)
                .setFilter(Reward.MONTH, Attribute.FILTER_SAME_MONTH)
        ));
    }

    public BigDecimal getRewardsTotal(Long workerId, Long rewardTypeId, Date month){
        return getRewards(workerId, month).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsTotal(List<Reward> rewards, Long rewardTypeId, Date month, boolean current){
        return rewards.stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId) && isCurrentSaleMonth(r, month) == current)
                .reduce(ZERO, ((t, r) -> t.add(r.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsTotalLocal(List<Reward> rewards, Long rewardTypeId, Date month, boolean current){
        return rewards.stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId) && isCurrentSaleMonth(r, month) == current)
                .reduce(ZERO, ((t, r) -> t.add(r.getLocal())), BigDecimal::add);
    }

    public boolean isCurrentSaleMonth(Reward reward, Date month){
        Sale sale = saleService.getSale(reward.getSaleId());

        return sale != null
                ? Dates.isSameMonth(saleService.getSale(reward.getSaleId()).getDate(), month)
                : Dates.isSameMonth(reward.getMonth(), month);
    }

    public WorkerRewardTree getWorkerRewardTree(Date month){
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        calcSaleVolumes(tree, month);

        calcPaymentVolume(tree, month);

        calcRegistrationCount(tree, month);
        calcFirstLevelCount(tree);

        return tree;
    }

    private Long getRank(BigDecimal saleVolume){
        if (saleVolume.compareTo(getParameter(20L)) >= 0){
            return RankType.PLATINUM_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(19L)) >= 0){
            return RankType.GOLD_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(18L)) >= 0){
            return RankType.SILVER_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(17L)) >= 0){
            return RankType.REGIONAL_MANAGER;
        }else if (saleVolume.compareTo(getParameter(16L)) >= 0){
            return RankType.AREA_MANAGER;
        }else if (saleVolume.compareTo(getParameter(15L)) >= 0){
            return RankType.DIVISION_MANAGER;
        }else if (saleVolume.compareTo(getParameter(14L)) >= 0){
            return RankType.SENIOR_MANAGER;
        }else if (saleVolume.compareTo(getParameter(13L)) >= 0){
            return RankType.SENIOR_ASSISTANT;
        }else if (saleVolume.compareTo(getParameter(12L)) >= 0){
            return RankType.TEAM_MANAGER;
        }else if (saleVolume.compareTo(getParameter(11L)) >= 0){
            return RankType.MANAGER_JUNIOR;
        }else if (saleVolume.compareTo(getParameter(47L)) >= 0){
            return RankType.MANAGER_ASSISTANT;
        }

        return 0L;
    }

    private void calcSaleVolumes(WorkerRewardTree tree, Date month){
        Set<Long> activeSaleWorkerIds = saleService.getActiveSaleWorkerIds();

        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                if (activeSaleWorkerIds.contains(r.getWorkerNode().getObjectId())) {
                    r.setSaleVolume(saleService.getSaleVolume(r.getWorkerNode().getObjectId(), month));
                }

                r.setGroupSaleVolume(r.getChildRewards().stream()
                        .filter(WorkerReward::isManager)
                        .reduce(r.getSaleVolume(), (v, c) -> v.add(c.getGroupSaleVolume()), BigDecimal::add));

                r.setRank(getRank(r.getGroupSaleVolume()));

                r.setStructureSaleVolume(r.getChildRewards().stream()
                        .reduce(r.getSaleVolume(), (v, c) -> v.add(c.getStructureSaleVolume()), BigDecimal::add));
            });
        });
    }

    private void calcPaymentVolume(WorkerRewardTree tree, Date month){
        Set<Long> activeSaleWorkerIds = saleService.getActiveSaleWorkerIds();

        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                if (activeSaleWorkerIds.contains(r.getWorkerNode().getObjectId())) {
                    r.setPaymentVolume(paymentService.getPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getObjectId(), month));
                }

                r.setGroupPaymentVolume(r.getChildRewards().stream()
                        .filter(WorkerReward::isManager)
                        .reduce(r.getPaymentVolume(), (v, c) -> v.add(c.getGroupPaymentVolume()), BigDecimal::add));

                r.setStructurePaymentVolume(r.getChildRewards().stream()
                        .reduce(r.getPaymentVolume(), (v, c) -> v.add(c.getStructurePaymentVolume()), BigDecimal::add));
            });
        });
    }

    private void calcRegistrationCount(WorkerRewardTree tree, Date month){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                r.setRegistrationCount(r.getChildRewards().stream()
                        .filter(c -> c.getWorkerNode().getRegistrationDate() != null)
                        .reduce(0L, (v, c) -> isNewWorker(c, month) ? 1L : 0L, Long::sum));

                r.setGroupRegistrationCount(r.getChildRewards().stream()
                        .filter(c -> !c.isManager() && c.getWorkerNode().getRegistrationDate() != null)
                        .reduce(0L, (v, c) -> c.getGroupRegistrationCount() + (isNewWorker(c, month) ? 1 : 0), Long::sum));

                r.setStructureManagerCount(r.getChildRewards().stream()
                        .filter(WorkerReward::isManager)
                        .reduce(0L, (v, c) -> c.getStructureManagerCount() + 1, Long::sum));
            });
        });
    }

    private boolean isNewWorker(WorkerReward workerReward, Date month){
        return Dates.isSameMonth(workerReward.getWorkerNode().getRegistrationDate(), month);
    }

    private void calcFirstLevelCount(WorkerRewardTree tree){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelCount((long) r.getChildRewards().size())));
    }

    public BigDecimal getPersonalRewardPoint(Sale sale, List<SaleItem> saleItems){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            boolean mkPremium = saleService.isMkPremiumSaleItems(saleItems);
            boolean mkTouch = saleService.isMkTouchSaleItems(saleItems);

            if (sale.isSasRequest()){
                point = getParameter(1L);
            }else if (w.getMkStatus() == MkStatus.STATUS_PROMO){
                if (mkPremium){
                    point = getParameter(2L);
                }else if (mkTouch){
                    point = getParameter(3L);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_JUST){
                if (mkPremium){
                    point = getParameter(4L);
                }else if (mkTouch){
                    point = getParameter(5L);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_VIP){
                if (sale.getMkManagerBonusWorkerId() != null){
                    if (mkPremium){
                        point = getParameter(6L).subtract(getParameter(9L));
                    }else if (mkTouch){
                        point = getParameter(7L).subtract(getParameter(10L));
                    }
                }else if (mkPremium){
                    point = getParameter(6L);
                }else if (mkTouch){
                    point = getParameter(7L);
                }
            }
        }else if (sale.getType() == SaleType.BASE_ASSORTMENT && sale.getTotal() != null){
            point = sale.getTotal().multiply(getParameter(8L));
        }

        return point;
    }

    public BigDecimal getMkManagerBonusRewardPoint(Sale sale, List<SaleItem> saleItems){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            if (saleService.isMkPremiumSaleItems(saleItems)){
                point = getParameter(9L);
            }else if (saleService.isMkTouchSaleItems(saleItems)){
                point = getParameter(10L);
            }
        }

        return point;
    }

    public BigDecimal getCulinaryWorkshopRewardPoint(Sale sale, List<SaleItem> saleItems){
        if (saleService.isMkSaleItems(saleItems)){
            if (!sale.isSasRequest()) {
                return getParameter(41L);
            } else {
                return getParameter(42L);
            }
        }

        return ZERO;
    }

    private void updateLocal(Sale sale, Reward reward){
        Date date = Dates.currentDate();

        List<SaleItem> saleItems = saleService.getSaleItems(sale.getObjectId());

        if (!saleItems.isEmpty()){
            SaleItem saleItem = saleItems.get(0);

            if (saleItem.getSaleDecisionId() != null){
                reward.setRate(priceService.getRate(sale, saleItem, date));
            }

            reward.setDiscount(saleItem.getPrice().divide(saleItem.getBasePrice(), 2, HALF_EVEN));
        }

        if (reward.getRate() == null){
            reward.setRate(priceService.getRate(sale.getStorageId(), date));
        }

        reward.setLocal(reward.getPoint().multiply(reward.getRate()).setScale(2, HALF_EVEN));

        if (reward.getDiscount() != null){
            reward.setLocal(reward.getLocal().multiply(reward.getDiscount()).setScale(2, HALF_EVEN));
        }
    }

    private void updateLocal(Long countyId, Reward reward){
        Date date = Dates.currentDate();

        reward.setRate(exchangeRateService.getExchangeRateValue(countyId, date));

        reward.setLocal(reward.getPoint().multiply(reward.getRate()).setScale(2, HALF_EVEN));
    }

    private BigDecimal calcManagerPremiumPoint(Sale sale, Long rank){
        switch (rank.intValue()){
            case (int) RankType.MANAGER_ASSISTANT:
                return calcManagerPremiumPoint(sale, 48L, 49L);
            case (int) RankType.MANAGER_JUNIOR:
                return calcManagerPremiumPoint(sale, 21L, 31L);
            case (int) RankType.TEAM_MANAGER:
                return calcManagerPremiumPoint(sale, 22L, 32L);
            case (int) RankType.SENIOR_ASSISTANT:
                return calcManagerPremiumPoint(sale, 23L, 33L);
            case (int) RankType.SENIOR_MANAGER:
                return calcManagerPremiumPoint(sale, 24L, 34L);
            case (int) RankType.DIVISION_MANAGER:
                return calcManagerPremiumPoint(sale, 25L, 35L);
            case (int) RankType.AREA_MANAGER:
                return calcManagerPremiumPoint(sale, 26L, 36L);
            case (int) RankType.REGIONAL_MANAGER:
                return calcManagerPremiumPoint(sale, 27L, 37L);
            case (int) RankType.SILVER_DIRECTOR:
                return calcManagerPremiumPoint(sale, 28L, 38L);
            case (int) RankType.GOLD_DIRECTOR:
                return calcManagerPremiumPoint(sale, 29L, 39L);
            case (int) RankType.PLATINUM_DIRECTOR:
                return calcManagerPremiumPoint(sale, 30L, 40L);
        }

        return ZERO;
    }

    private BigDecimal calcManagerPremiumPoint(Sale sale, Long mkParameterId, Long baParameterId){
        if (sale.getType() == SaleType.MYCOOK){
            return getParameter(mkParameterId);
        }else if (sale.getType() == SaleType.BASE_ASSORTMENT && sale.getTotal() != null){
            return sale.getTotal().multiply(getParameter(baParameterId));
        }

        return ZERO;
    }

    private BigDecimal getGroupVolumePercent(Long rank){
        switch (rank.intValue()){
            case (int) RankType.MANAGER_ASSISTANT:
                return getParameter(50L);
            case (int) RankType.MANAGER_JUNIOR:
                return getParameter(51L);
            case (int) RankType.TEAM_MANAGER:
                return getParameter(52L);
            case (int) RankType.SENIOR_ASSISTANT:
                return getParameter(53L);
            case (int) RankType.SENIOR_MANAGER:
                return getParameter(54L);
            case (int) RankType.DIVISION_MANAGER:
                return getParameter(55L);
            case (int) RankType.AREA_MANAGER:
                return getParameter(56L);
            case (int) RankType.REGIONAL_MANAGER:
                return getParameter(57L);
            case (int) RankType.SILVER_DIRECTOR:
                return getParameter(58L);
            case (int) RankType.GOLD_DIRECTOR:
                return getParameter(59L);
            case (int) RankType.PLATINUM_DIRECTOR:
                return getParameter(60L);
        }

        return ZERO;
    }

    private BigDecimal calcRewardPoint(Sale sale, Long rewardType, Date month, BigDecimal rewardPoint){
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0 && sale.getTotal() != null) {
            BigDecimal paidInterest = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId(), month)
                    .divide(sale.getTotal(), 5, BigDecimal.ROUND_HALF_EVEN);

            if (paidInterest.compareTo(new BigDecimal("0.2")) >= 0){
                point = point.add(rewardPoint).multiply(new BigDecimal("0.25"));
            }

            if (paidInterest.compareTo(new BigDecimal("0.7")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.35")));
            }

            if (paidInterest.compareTo(new BigDecimal("1")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.40")));
            }

            point = point.subtract(getRewardsTotalBySaleId(sale.getObjectId(), rewardType));

        }

        return point;
    }

    private BigDecimal calcRewardPoint(Long workerId, Long rewardType, Date month, BigDecimal saleVolume,
                                       BigDecimal paymentVolume, BigDecimal rewardPoint){
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0) {
            BigDecimal paidInterest = paymentVolume.divide(saleVolume, 5, BigDecimal.ROUND_HALF_EVEN);

            if (paidInterest.compareTo(new BigDecimal("0.2")) >= 0){
                point = point.add(rewardPoint).multiply(new BigDecimal("0.25"));
            }

            if (paidInterest.compareTo(new BigDecimal("0.7")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.35")));
            }

            if (paidInterest.compareTo(new BigDecimal("1")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.40")));
            }

            point = point.subtract(getRewardsTotal(workerId, rewardType, month));
        }

        return point;
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards() throws RewardException {
        try {
            Period period = periodService.getActualPeriod();

            Date month = period.getOperatingMonth();

            rewardMapper.deleteRewards(month);

            WorkerRewardTree tree = getWorkerRewardTree(month);

            saleService.getActiveSales().forEach(s -> {
                if (s.getPersonalRewardPoint() != null){
                    BigDecimal point = calcRewardPoint(s, RewardType.TYPE_MYCOOK_SALE, month, s.getPersonalRewardPoint()
                    );

                    if (point.compareTo(ZERO) > 0){
                        Reward reward = new Reward();

                        reward.setSaleId(s.getObjectId());
                        reward.setWorkerId(s.getSellerWorkerId());
                        reward.setType(RewardType.TYPE_MYCOOK_SALE);
                        reward.setPoint(point);
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);

                        updateLocal(s, reward);

                        domainService.save(reward);
                    }

                    WorkerReward workerReward = tree.getWorkerReward(s.getSellerWorkerId());

                    if (workerReward.getRank() > 0){
                        BigDecimal premium = calcRewardPoint(s, RewardType.TYPE_MANAGER_PREMIUM, month,
                                calcManagerPremiumPoint(s, workerReward.getRank()));

                        if (premium.compareTo(ZERO) > 0){
                            Reward reward = new Reward();

                            reward.setSaleId(s.getObjectId());
                            reward.setWorkerId(s.getSellerWorkerId());
                            reward.setType(RewardType.TYPE_MANAGER_PREMIUM);
                            reward.setPoint(premium);
                            reward.setDate(Dates.currentDate());
                            reward.setMonth(month);
                            reward.setGroupSaleVolume(workerReward.getGroupSaleVolume());
                            reward.setRankId(workerReward.getRank());

                            updateLocal(s, reward);

                            domainService.save(reward);
                        }
                    }
                }

                if (s.getMkManagerBonusRewardPoint() != null){
                    BigDecimal point = calcRewardPoint(s, RewardType.TYPE_MK_MANAGER_BONUS, month,
                            s.getMkManagerBonusRewardPoint());

                    if (point.compareTo(ZERO) > 0){
                        Reward reward = new Reward();

                        reward.setSaleId(s.getObjectId());
                        reward.setWorkerId(s.getMkManagerBonusWorkerId());
                        reward.setType(RewardType.TYPE_MK_MANAGER_BONUS);
                        reward.setPoint(point);
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);

                        updateLocal(s, reward);

                        domainService.save(reward);
                    }
                }

                if (s.getCulinaryRewardPoint() != null && s.getTotal() != null &&
                        paymentService.getPaymentsVolumeBySaleId(s.getObjectId(), month).compareTo(s.getTotal()) >= 0 &&
                        getRewardsTotalBySaleId(s.getObjectId(), RewardType.TYPE_CULINARY_WORKSHOP).compareTo(ZERO) == 0){
                    Reward reward = new Reward();

                    reward.setSaleId(s.getObjectId());
                    reward.setType(RewardType.TYPE_CULINARY_WORKSHOP);
                    reward.setWorkerId(s.getCulinaryWorkerId() != null ? s.getCulinaryWorkerId() : s.getSellerWorkerId());
                    reward.setPoint(s.getCulinaryRewardPoint());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(month);

                    updateLocal(s, reward);

                    domainService.save(reward);
                }
            });

            tree.forEachLevel((l, rl) -> {
                rl.forEach(r -> {
                    if (r.getPaymentVolume().compareTo(getParameter(43L)) >= 0){
                        Reward reward = new Reward();

                        reward.setType(RewardType.TYPE_PERSONAL_VOLUME);
                        reward.setWorkerId(r.getWorkerNode().getObjectId());
                        reward.setPersonalSaleVolume(r.getSaleVolume());
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);

                        if (getRewardsTotal(reward.getWorkerId(), RewardType.TYPE_PERSONAL_VOLUME,
                                month).compareTo(ZERO) == 0) {
                            if (r.getPaymentVolume().compareTo(getParameter(44L)) < 0){
                                reward.setPoint(getParameter(45L));
                            }else if (r.getPaymentVolume().compareTo(getParameter(44L)) >= 0){
                                reward.setPoint(getParameter(46L));
                            }
                        }

                        updateLocal(2L, reward);

                        if (reward.getPoint().compareTo(ZERO) > 0) {
                            domainService.save(reward);
                        }
                    }

                    if (r.getRank() > 0){
                        BigDecimal point = getGroupVolumePercent(r.getRank()).multiply(r.getGroupSaleVolume())
                                .divide(new BigDecimal("100"), 5, ROUND_HALF_EVEN);

                        point = calcRewardPoint(r.getWorkerId(), RewardType.TYPE_GROUP_VOLUME, month,
                                r.getGroupSaleVolume(), r.getGroupPaymentVolume(), point);

                        if (point.compareTo(ZERO) > 0) {
                            Reward reward = new Reward();

                            reward.setWorkerId(r.getWorkerId());
                            reward.setType(RewardType.TYPE_GROUP_VOLUME);
                            reward.setPoint(point);
                            reward.setGroupSaleVolume(r.getGroupSaleVolume());
                            reward.setGroupPaymentVolume(r.getGroupPaymentVolume());
                            reward.setRankId(r.getRank());
                            reward.setDate(Dates.currentDate());
                            reward.setMonth(month);

                            updateLocal(2L, reward);

                            domainService.save(reward);
                        }
                    }
                });
            });
        } catch (Exception e) {
            throw new RewardException(e.getMessage());
        }
    }

    private transient LoadingCache<Long, BigDecimal> parameterCache;

    public LoadingCache<Long, BigDecimal> getParameterCache(){
        if (parameterCache == null){
            parameterCache = CacheBuilder.newBuilder()
                    .expireAfterAccess(1, TimeUnit.MINUTES)
                    .build(CacheLoader.from(rewardParameterId -> domainService.getDomain(RewardParameter.class, rewardParameterId)
                            .getDecimal(RewardParameter.VALUE)));

        }

        return parameterCache;
    }

    public BigDecimal getParameter(Long rewardParameterId){
        try {
            return getParameterCache().get(rewardParameterId);
        } catch (Exception e) {
            return null;
        }
    }

    public WorkerReward getWorkerReward(Worker worker){
        Date month = periodService.getActualPeriod().getOperatingMonth();

        return getWorkerRewardTree(month).getWorkerReward(worker.getObjectId());
    }
}
