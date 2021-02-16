package ru.complitex.jedani.worker.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.Region;
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
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov
 * 20.10.2019 11:02 AM
 */
public class RewardService implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(RewardService.class);

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

    public List<Reward> getRewards(Long workerId, Long managerId, Date month){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward()
                .setWorkerId(workerId)
                .setManagerId(managerId)
                .setMonth(month)
                .setFilter(Reward.MONTH, Attribute.FILTER_SAME_MONTH)
        ));
    }

    public BigDecimal getRewardsTotal(Long workerId, Long rewardTypeId, Date month){
        return getRewards(workerId, month).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .reduce(ZERO, ((t, p) -> t.add(p.getTotal())), BigDecimal::add);
    }

    public BigDecimal getRewardsTotal(Long workerId, Long managerId, Long rewardTypeId, Date month){
        return getRewards(workerId, managerId, month).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .reduce(ZERO, ((t, p) -> t.add(p.getTotal())), BigDecimal::add);
    }

    public BigDecimal getRewardsTotal(List<Reward> rewards, Long rewardTypeId, Date month, boolean current){
        return rewards.stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId) && isCurrentSaleMonth(r, month) == current)
                .reduce(ZERO, ((t, r) -> t.add(r.getTotal())), BigDecimal::add);
    }

    public BigDecimal getRewardsPoint(List<Reward> rewards, Long rewardTypeId, Date month, boolean current){
        return rewards.stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId) && isCurrentSaleMonth(r, month) == current)
                .reduce(ZERO, ((t, r) -> t.add(r.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsTotalLocal(List<Reward> rewards, Long rewardTypeId, Date month, boolean current){
        return rewards.stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId) && isCurrentSaleMonth(r, month) == current)
                .filter(r -> r.getTotal() != null && r.getRate() != null)
                .reduce(ZERO, ((t, r) -> t.add(r.getTotal().multiply(r.getRate()))), BigDecimal::add);
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

    public List<WorkerReward> getPrivateGroup(WorkerReward r){
        List<WorkerReward> group = new ArrayList<>();

        for (WorkerReward c : r.getChildRewards()){
            if (!c.isManager()){
                group.add(c);

                group.addAll(getPrivateGroup(c));
            }
        }

        return group;
    }

    private void calcSaleVolumes(WorkerRewardTree tree, Date month){
        Set<Long> activeSaleWorkerIds = saleService.getActiveSaleWorkerIds();

        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                if (activeSaleWorkerIds.contains(r.getWorkerNode().getObjectId())) {
                    r.setSaleVolume(saleService.getSaleVolume(r.getWorkerNode().getObjectId(), month));
                }

                r.setGroupSaleVolume(getPrivateGroup(r).stream()
                        .reduce(ZERO, (v, c) -> v.add(c.getSaleVolume()), BigDecimal::add));

                r.setStructureSaleVolume(r.getChildRewards().stream()
                        .reduce(r.getSaleVolume(), (v, c) -> v.add(c.getStructureSaleVolume()), BigDecimal::add));

                r.setRank(getRank(r.getStructureSaleVolume()));
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

                r.setGroupPaymentVolume(getPrivateGroup(r).stream()
                        .reduce(ZERO, (v, c) -> v.add(c.getPaymentVolume()), BigDecimal::add));

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
                        .reduce(0L, (v, c) -> v + (isNewWorker(c, month) ? 1L : 0L), Long::sum));

                r.setGroupRegistrationCount(getPrivateGroup(r).stream()
                        .filter(c -> c.getWorkerNode().getRegistrationDate() != null)
                        .reduce(0L, (v, c) -> v + (isNewWorker(c, month) ? 1 : 0), Long::sum));

                r.setStructureManagerCount(r.getChildRewards().stream()
                        .filter(WorkerReward::isManager)
                        .reduce(0L, (v, c) -> v + c.getStructureManagerCount() + 1, Long::sum));
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

    public void updateLocal(Sale sale, Reward reward){
        Date date = Dates.currentDate();

        List<SaleItem> saleItems = saleService.getSaleItems(sale.getObjectId());

        if (!saleItems.isEmpty()){
            SaleItem saleItem = saleItems.get(0);

            reward.setRate(saleItem.getRate());

            if (reward.getRate() == null && saleItem.getSaleDecisionId() != null){
                reward.setRate(priceService.getRate(sale, saleItem, date));
            }

            if (reward.getPoint().compareTo(ZERO) > 0) {
                reward.setBasePrice(saleItem.getBasePrice());

                if (sale.isFeeWithdraw()) {
                    reward.setPrice(saleItem.getPrice().add(reward.getTotal()));
                }else{
                    reward.setPrice(saleItem.getPrice());
                }

                reward.setDiscount(reward.getPrice().divide(reward.getBasePrice(), 7, HALF_EVEN));
            }
        }

        if (reward.getRate() == null){
            reward.setRate(priceService.getRate(sale.getStorageId(), date));
        }

        if (reward.getPoint().compareTo(ZERO) != 0) {
            reward.setLocal(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));

            if (reward.getDiscount() != null){
                reward.setLocal(reward.getLocal().multiply(reward.getDiscount()).setScale(5, HALF_EVEN));
            }
        }else {
            reward.setLocal(ZERO);
        }
    }

    private void updateLocal(Reward reward){
        Worker worker = workerService.getWorker(reward.getWorkerId());

        if (!worker.getRegionIds().isEmpty()) {;
            Region region = domainService.getDomain(Region.class, worker.getRegionIds().get(0));

            reward.setRate(exchangeRateService.getExchangeRateValue(region.getParentId(), Dates.currentDate()));

            if (reward.getPoint().compareTo(ZERO) > 0) {
                reward.setLocal(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));
            }
        }
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

    public BigDecimal calcRewardPoint(Sale sale, Long rewardType, Date month, BigDecimal rewardPoint){
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0 && sale.getTotal() != null) {
            BigDecimal paidInterest = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId(), month)
                    .divide(sale.getTotal(), 5, RoundingMode.HALF_EVEN);

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

    private BigDecimal calcRewardPoint(Long workerId, Long managerId, Long rewardType, Date month, BigDecimal saleVolume,
                                       BigDecimal paymentVolume, BigDecimal rewardPoint){
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0) {
            BigDecimal paidInterest = paymentVolume.divide(saleVolume, 5, HALF_EVEN);

            if (paidInterest.compareTo(new BigDecimal("0.2")) >= 0){
                point = point.add(rewardPoint).multiply(new BigDecimal("0.25"));
            }

            if (paidInterest.compareTo(new BigDecimal("0.7")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.35")));
            }

            if (paidInterest.compareTo(new BigDecimal("1")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.40")));
            }

            point = point.subtract(getRewardsTotal(workerId, managerId, rewardType, month));
        }

        return point;
    }

    public List<WorkerReward> getStructureManagers(WorkerReward r){
        List<WorkerReward> managers = new ArrayList<>();

        for (WorkerReward c : r.getChildRewards()){
            if (c.isManager()){
                managers.add(c);

                managers.addAll(getStructureManagers(c));
            }
        }

        return managers;
    }

    public void calculateSaleReward(Sale sale, List<SaleItem> saleItems){
        Reward reward = new Reward();

        Period period = periodService.getActualPeriod();

        Date month = period.getOperatingMonth();

        Long rewardType = saleService.isMkSaleItems(saleItems) ? RewardType.MYCOOK_SALE : RewardType.BASE_ASSORTMENT_SALE;

        BigDecimal total = sale.getPersonalRewardPoint();

        if (total == null){
            log.warn("null sale reward total {}", sale);

            return;
        }

        reward.setSaleId(sale.getObjectId());
        reward.setWorkerId(sale.getSellerWorkerId());
        reward.setType(rewardType);
        reward.setTotal(total);
        reward.setPoint(total.subtract(getRewardsTotalBySaleId(sale.getObjectId(), rewardType)));
        reward.setDate(Dates.currentDate());
        reward.setMonth(month);
        reward.setRewardStatus(RewardStatus.WITHDRAWN);
        reward.setPeriodId(period.getObjectId());

        updateLocal(sale, reward);

        if (reward.getPoint().compareTo(ZERO) != 0) {
            domainService.save(reward);
        }
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards(boolean accrue) throws RewardException {
        try {
            Period period = periodService.getActualPeriod();

            Date month = period.getOperatingMonth();

            rewardMapper.deleteRewards(month);

            WorkerRewardTree tree = getWorkerRewardTree(month);

            saleService.getActiveSales().forEach(s -> {
                calculateSaleReward(s, saleService.getSaleItems(s.getObjectId()));

                if (s.getMkManagerBonusRewardPoint() != null && s.getMkManagerBonusWorkerId() != null){
                    BigDecimal total = s.getMkManagerBonusRewardPoint();

                    if (total.compareTo(ZERO) > 0){
                        Reward reward = new Reward();

                        reward.setSaleId(s.getObjectId());
                        reward.setWorkerId(s.getMkManagerBonusWorkerId());
                        reward.setType(RewardType.MK_MANAGER_BONUS);
                        reward.setPoint(calcRewardPoint(s, RewardType.MK_MANAGER_BONUS, month, total));
                        reward.setTotal(total);
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);
                        reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                        reward.setPeriodId(period.getObjectId());

                        updateLocal(s, reward);

                        domainService.save(reward);
                    }
                }

                if (s.getCulinaryRewardPoint() != null && s.getTotal() != null &&
                        paymentService.getPaymentsVolumeBySaleId(s.getObjectId(), month).compareTo(s.getTotal()) >= 0 &&
                        getRewardsTotalBySaleId(s.getObjectId(), RewardType.CULINARY_WORKSHOP).compareTo(ZERO) == 0){
                    Reward reward = new Reward();

                    reward.setSaleId(s.getObjectId());
                    reward.setType(RewardType.CULINARY_WORKSHOP);
                    reward.setWorkerId(s.getCulinaryWorkerId() != null ? s.getCulinaryWorkerId() : s.getSellerWorkerId());
                    reward.setPoint(s.getCulinaryRewardPoint());
                    reward.setTotal(s.getCulinaryRewardPoint());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(month);
                    reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                    reward.setPeriodId(period.getObjectId());

                    updateLocal(s, reward);

                    domainService.save(reward);
                }
            });

            tree.forEachLevel((l, rl) -> {
                rl.forEach(r -> {
                    if (r.getRank() > 0){
                        Reward reward = new Reward();

                        reward.setWorkerId(r.getWorkerId());
                        reward.setType(RewardType.RANK);
                        reward.setSaleVolume(r.getSaleVolume());
                        reward.setPaymentVolume(r.getPaymentVolume());
                        reward.setGroupSaleVolume(r.getGroupSaleVolume());
                        reward.setGroupPaymentVolume(r.getGroupPaymentVolume());
                        reward.setStructureSaleVolume(r.getStructureSaleVolume());
                        reward.setStructurePaymentVolume(r.getStructurePaymentVolume());
                        reward.setRankId(r.getRank());
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);
                        reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                        reward.setPeriodId(period.getObjectId());

                        domainService.save(reward);
                    }
                });
            });

            saleService.getActiveSales().forEach(s -> {
                WorkerReward workerReward = tree.getWorkerReward(s.getSellerWorkerId());

                if (workerReward.getRank() > 0){
                    BigDecimal total = calcManagerPremiumPoint(s, workerReward.getRank());

                    if (total.compareTo(ZERO) > 0){
                        Reward reward = new Reward();

                        reward.setSaleId(s.getObjectId());
                        reward.setWorkerId(s.getSellerWorkerId());
                        reward.setType(RewardType.MANAGER_PREMIUM);
                        reward.setPoint(calcRewardPoint(s, RewardType.MANAGER_PREMIUM, month, total));
                        reward.setTotal(total);
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);
                        reward.setStructureSaleVolume(workerReward.getStructureSaleVolume());
                        reward.setRankId(workerReward.getRank());
                        reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                        reward.setPeriodId(period.getObjectId());

                        updateLocal(s, reward);

                        domainService.save(reward);
                    }
                }
            });

            tree.forEachLevel((l, rl) -> {
                rl.forEach(r -> {
                    if (r.getSaleVolume().compareTo(getParameter(43L)) >= 0){
                        Reward reward = new Reward();

                        reward.setType(RewardType.PERSONAL_VOLUME);
                        reward.setWorkerId(r.getWorkerNode().getObjectId());
                        reward.setSaleVolume(r.getSaleVolume());
                        reward.setPaymentVolume(r.getPaymentVolume());
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);
                        reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                        reward.setPeriodId(period.getObjectId());

                        if (getRewardsTotal(reward.getWorkerId(), RewardType.PERSONAL_VOLUME,
                                month).compareTo(ZERO) == 0) {
                            if (r.getPaymentVolume().compareTo(getParameter(44L)) < 0){
                                reward.setPoint(getParameter(45L));
                            }else if (r.getPaymentVolume().compareTo(getParameter(44L)) >= 0){
                                reward.setPoint(getParameter(46L));
                            }
                        }

                        if (r.getSaleVolume().compareTo(getParameter(44L)) < 0){
                            reward.setTotal(getParameter(45L));
                        }else if (r.getSaleVolume().compareTo(getParameter(44L)) >= 0){
                            reward.setTotal(getParameter(46L));
                        }

                        updateLocal(reward);

                        if (reward.getTotal().compareTo(ZERO) > 0) {
                            domainService.save(reward);
                        }
                    }

                    if (r.getRank() > 0){
                        BigDecimal total = getGroupVolumePercent(r.getRank()).multiply(r.getGroupSaleVolume())
                                .divide(new BigDecimal("100"), 5, HALF_EVEN);

                        if (total.compareTo(ZERO) > 0) {
                            Reward reward = new Reward();

                            reward.setWorkerId(r.getWorkerId());
                            reward.setType(RewardType.GROUP_VOLUME);
                            reward.setPoint(calcRewardPoint(r.getWorkerId(), null, RewardType.GROUP_VOLUME, month,
                                    r.getGroupSaleVolume(), r.getGroupPaymentVolume(), total));
                            reward.setTotal(total);
                            reward.setGroupSaleVolume(r.getGroupSaleVolume());
                            reward.setGroupPaymentVolume(r.getGroupPaymentVolume());
                            reward.setRankId(r.getRank());
                            reward.setDate(Dates.currentDate());
                            reward.setMonth(month);
                            reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                            reward.setPeriodId(period.getObjectId());

                            updateLocal(reward);

                            domainService.save(reward);
                        }

                        getStructureManagers(r).forEach(m -> {
                            BigDecimal t = getGroupVolumePercent(r.getRank())
                                    .subtract(getGroupVolumePercent(m.getRank()))
                                    .multiply(m.getStructureSaleVolume())
                                    .divide(new BigDecimal("100"), 5, HALF_EVEN);

                            if (t.compareTo(ZERO) > 0) {
                                Reward reward = new Reward();

                                reward.setWorkerId(r.getWorkerId());
                                reward.setType(RewardType.STRUCTURE_VOLUME);
                                reward.setPoint(calcRewardPoint(r.getWorkerId(), m.getWorkerId(),
                                        RewardType.STRUCTURE_VOLUME, month,
                                        m.getStructureSaleVolume(), m.getStructurePaymentVolume(), t));
                                reward.setTotal(t);
                                reward.setRankId(r.getRank());
                                reward.setManagerId(m.getWorkerId());
                                reward.setManagerRankId(m.getRank());
                                reward.setStructureSaleVolume(m.getStructureSaleVolume());
                                reward.setStructurePaymentVolume(m.getStructurePaymentVolume());
                                reward.setDate(Dates.currentDate());
                                reward.setMonth(month);
                                reward.setRewardStatus(accrue ? RewardStatus.ACCRUED : RewardStatus.CALCULATED);
                                reward.setPeriodId(period.getObjectId());

                                updateLocal(reward);

                                domainService.save(reward);
                            }
                        });
                    }
                });
            });
        } catch (Exception e) {
            log.error("error calculate rewards", e);

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
