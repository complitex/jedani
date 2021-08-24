package ru.complitex.jedani.worker.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.RewardMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov
 * 20.10.2019 11:02 AM
 */
@ApplicationScoped
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
    private PeriodMapper periodMapper;

    @Inject
    private RewardMapper rewardMapper;

    @Inject
    private PriceService priceService;

    @Inject
    private ExchangeRateService exchangeRateService;

    public List<Reward> getRewardsBySaleId(Long saleId) {
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward().setSaleId(saleId)));
    }

    public List<Reward> getRewardsBySaleId(Long saleId, Long rewardTypeId) {
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .collect(Collectors.toList());
    }

    public BigDecimal getRewardsPointSum(Long saleId, Long rewardTypeId, Long rewardStatusId) {
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsPointSumBySale(Long saleId, Long rewardTypeId, Long rewardStatusId, Long managerId) {
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .filter(r -> Objects.equals(r.getManagerId(), managerId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsPointSumByWorker(Long periodId, Long workerId, Long rewardTypeId, Long rewardStatusId) {
        return getRewards(periodId, workerId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public List<Reward> getRewards(Long periodId, Long workerId){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward()
                .setWorkerId(workerId)
                .setPeriodId(periodId)));
    }

    public BigDecimal getRewardsLocal(Long periodId, Long workerId, Long rewardStatusId) {
        return getRewards(periodId, workerId).stream()
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .map(r -> r.getAmount() != null ? r.getAmount() : ZERO)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getRewardsLocalByCurrency(Long periodId, Long rewardStatusId, Long currencyId) {
        return getRewards(periodId, null).stream()
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .filter(r -> Objects.equals(workerService.getCurrencyId(r.getWorkerId()), currencyId))
                .map(r -> r.getAmount() != null ? r.getAmount() : ZERO)
                .reduce(ZERO, BigDecimal::add);
    }

    public WorkerRewardTree getWorkerRewardTree(Period period) {
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        calcPaymentVolume(tree, period);
        calcFirstLevelCount(tree, period);
        calcRegistrationCount(tree, period);
        calcSaleVolumes(tree, period);
        calcStructureManagerCount(tree, period);

        return tree;
    }

    private transient LoadingCache<Long, WorkerRewardTree> workerRewardTreeCache;

    private LoadingCache<Long, WorkerRewardTree> getWorkerRewardTreeCache(){
        if (workerRewardTreeCache == null){
            workerRewardTreeCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(periodId -> getWorkerRewardTree(periodMapper.getPeriod(periodId))));
        }

        return workerRewardTreeCache;
    }

    public WorkerRewardTree getWorkerRewardTreeCache(Long periodId) {
        try {
            return getWorkerRewardTreeCache().get(periodId);
        } catch (ExecutionException e) {
            return null;
        }
    }

    private Long getRank(BigDecimal saleVolume) {
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

    private void calcSaleVolumes(WorkerRewardTree tree, Period period){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> {
            r.setSales(saleService.getSales(r.getWorkerNode().getObjectId(), period));

            r.setSaleVolume(saleService.getSaleVolume(r.getSales()));

            r.getGroup().forEach(c -> r.getGroupSales().addAll(c.getSales()));

            r.setGroupSaleVolume(r.getGroup().stream()
                    .reduce(ZERO, (v, c) -> v.add(c.getSaleVolume()), BigDecimal::add));

            r.getStructureSales().addAll(r.getSales());

            r.getWorkerRewards().forEach(c -> r.getStructureSales().addAll(c.getStructureSales()));

            r.setStructureSaleVolume(r.getWorkerRewards().stream()
                    .reduce(r.getSaleVolume(), (v, c) -> v.add(c.getStructureSaleVolume()), BigDecimal::add));

            if (r.getFirstLevelPersonalCount() >= 4 && r.getGroupRegistrationCount() >= 2 && r.getPaymentVolume().compareTo(BigDecimal.valueOf(200)) >= 0) {
                r.setRank(getRank(r.getStructureSaleVolume()));
            }
        }));
    }

    private void calcPaymentVolume(WorkerRewardTree tree, Period period){
        Set<Long> activeSaleWorkerIds = saleService.getActiveSaleWorkerIds();

        tree.forEachLevel((l, rl) -> rl.forEach(r -> {
            if (activeSaleWorkerIds.contains(r.getWorkerNode().getObjectId())) {
                r.setPaymentVolume(paymentService.getPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getObjectId(), period));
            }

            r.setYearPaymentVolume(paymentService.getYearPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getObjectId()));

            Date registrationDate = domainService.getDate(Worker.ENTITY_NAME, r.getWorkerId(), Worker.REGISTRATION_DATE);

            r.setPk(Dates.isLessYear(period.getOperatingMonth(), registrationDate) || r.getYearPaymentVolume().compareTo(BigDecimal.valueOf(200)) >= 0);

            r.setGroupPaymentVolume(r.getGroup().stream()
                    .reduce(ZERO, (v, c) -> v.add(c.getPaymentVolume()), BigDecimal::add));

            r.setStructurePaymentVolume(r.getWorkerRewards().stream()
                    .reduce(r.getPaymentVolume(), (v, c) -> v.add(c.getStructurePaymentVolume()), BigDecimal::add));
        }));
    }

    private void calcRegistrationCount(WorkerRewardTree tree, Period period){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> {
            r.setRegistrationCount(r.getWorkerRewards().stream()
                    .reduce(0L, (v, c) -> v + (isNewWorker(c.getWorkerId(), period) ? 1L : 0L), Long::sum));

            r.setGroupRegistrationCount(r.getGroup().stream()
                    .reduce(0L, (v, c) -> v + (isNewWorker(c.getWorkerId(), period) ? 1 : 0), Long::sum));
        }));
    }

    private void calcStructureManagerCount(WorkerRewardTree tree, Period period){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setStructureManagerCount(r.getWorkerRewards().stream()
                .reduce(0L, (v, c) -> v + c.getStructureManagerCount() + (c.isManager() ? 1 : 0), Long::sum))));
    }

    private boolean isNewWorker(Long workerId, Period period){
        Date registrationDate = domainService.getDate(Worker.ENTITY_NAME, workerId, Worker.REGISTRATION_DATE);

        Long status =  domainService.getNumber(Worker.ENTITY_NAME, workerId, Worker.STATUS);

        return registrationDate != null && Dates.isSameMonth(registrationDate, period.getOperatingMonth()) &&
                !Objects.equals(status, WorkerStatus.MANAGER_CHANGED);
    }

    private void calcFirstLevelCount(WorkerRewardTree tree, Period period){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelCount(r.getWorkerRewards().stream()
                .filter(c -> {
                    Date registrationDate =  domainService.getDate(Worker.ENTITY_NAME, c.getWorkerId(), Worker.REGISTRATION_DATE);

                    return registrationDate != null && registrationDate.before(Dates.nextMonth(period.getOperatingMonth())) && c.isPk();
                })
                .count())));

        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelPersonalCount(r.getWorkerRewards().stream()
                .filter(c -> {
                    Date registrationDate =  domainService.getDate(Worker.ENTITY_NAME, c.getWorkerId(), Worker.REGISTRATION_DATE);

                    Long status =  domainService.getNumber(Worker.ENTITY_NAME, c.getWorkerId(), Worker.STATUS);

                    return registrationDate != null && registrationDate.before(Dates.nextMonth(period.getOperatingMonth())) &&
                            !Objects.equals(status, WorkerStatus.MANAGER_CHANGED) && c.isPk();
                })
                .count())));
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

    public BigDecimal getCulinaryRewardPoint(Sale sale, List<SaleItem> saleItems){
        if (saleService.isMkSaleItems(saleItems)){
            if (!sale.isSasRequest()) {
                return getParameter(41L);
            } else {
                return getParameter(42L);
            }
        }

        return ZERO;
    }

    public void updateLocal(Sale sale, Reward reward, Period period){
        if (reward.getPoint().compareTo(ZERO) >= 0) {
            Date date = Dates.currentDate();

            List<SaleItem> saleItems = saleService.getSaleItems(sale.getObjectId());

            if (!saleItems.isEmpty()){
                SaleItem saleItem = saleItems.get(0);

                reward.setRate(saleItem.getRate());

                if (reward.getRate() == null && saleItem.getSaleDecisionId() != null){
                    reward.setRate(priceService.getRate(sale, saleItem, date));
                }

                reward.setBasePrice(saleItem.getBasePrice());

                if (sale.isFeeWithdraw()) {
                    reward.setPrice(saleItem.getPrice().add(reward.getTotal()));
                }else{
                    reward.setPrice(saleItem.getPrice());
                }

                BigDecimal ratio = ZERO;

                List<Ratio> ratios = domainService.getDomains(Ratio.class, FilterWrapper.of((Ratio) new Ratio()
                        .setCountryId(workerService.getCountryId(reward.getWorkerId()))
                        .setBegin(reward.getMonth())
                        .setEnd(reward.getMonth())
                        .setFilter(Ratio.BEGIN, Attribute.FILTER_BEFORE_OR_EQUAL_DATE)
                        .setFilter(Ratio.END, Attribute.FILTER_AFTER_OR_EQUAL_OR_NULL_DATE)));

                if (!ratios.isEmpty()) {
                    ratio = ratios.get(0).getValue();
                }

                reward.setDiscount(reward.getPrice().multiply(BigDecimal.valueOf(100).subtract(ratio))
                        .divide(reward.getBasePrice().multiply(BigDecimal.valueOf(100)), 7, HALF_EVEN));

                if (reward.getRate() == null){
                    reward.setRate(priceService.getRate(sale.getStorageId(), date));
                }

                Long workerCountryId = workerService.getCountryId(sale.getSellerWorkerId());
                Long saleCountryId = saleService.getCountryId(sale);

                if (!workerCountryId.equals(saleCountryId)) {
                    Date crossDate = Dates.lastDayOfMonth(period.getOperatingMonth());

                    reward.setCrossRate(exchangeRateService.getExchangeRate(workerCountryId, crossDate)
                            .divide(exchangeRateService.getExchangeRate(saleCountryId, crossDate), 7, HALF_EVEN));
                } else {
                    reward.setCrossRate(ONE);
                }

                reward.setAmount(reward.getPoint().multiply(reward.getRate()).multiply(reward.getCrossRate())
                        .setScale(5, HALF_EVEN));

                if (reward.getDiscount() != null){
                    reward.setAmount(reward.getAmount().multiply(reward.getDiscount()).setScale(5, HALF_EVEN));
                }
            }
        } else {
            throw new RuntimeException("negative reward point error " + reward);
        }
    }

    private void updateLocal(Reward reward, Period period){
        if (reward.getPoint().compareTo(ZERO) >= 0) {
            reward.setRate(getPaymentsRate(reward.getWorkerId(), period));

            reward.setAmount(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));
        } else {
            throw new RuntimeException("negative reward point error " + reward);
        }
    }

    private BigDecimal calcManagerPoint(Sale sale, Long rank){
        switch (rank.intValue()){
            case (int) RankType.MANAGER_ASSISTANT:
                return calcManagerPoint(sale, 48L, 49L);
            case (int) RankType.MANAGER_JUNIOR:
                return calcManagerPoint(sale, 21L, 31L);
            case (int) RankType.TEAM_MANAGER:
                return calcManagerPoint(sale, 22L, 32L);
            case (int) RankType.SENIOR_ASSISTANT:
                return calcManagerPoint(sale, 23L, 33L);
            case (int) RankType.SENIOR_MANAGER:
                return calcManagerPoint(sale, 24L, 34L);
            case (int) RankType.DIVISION_MANAGER:
                return calcManagerPoint(sale, 25L, 35L);
            case (int) RankType.AREA_MANAGER:
                return calcManagerPoint(sale, 26L, 36L);
            case (int) RankType.REGIONAL_MANAGER:
                return calcManagerPoint(sale, 27L, 37L);
            case (int) RankType.SILVER_DIRECTOR:
                return calcManagerPoint(sale, 28L, 38L);
            case (int) RankType.GOLD_DIRECTOR:
                return calcManagerPoint(sale, 29L, 39L);
            case (int) RankType.PLATINUM_DIRECTOR:
                return calcManagerPoint(sale, 30L, 40L);
        }

        return ZERO;
    }

    private BigDecimal calcManagerPoint(Sale sale, Long mkParameterId, Long baParameterId){
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

    public BigDecimal calcRewardPoint(Sale sale, Long rewardType, Date month, BigDecimal rewardPoint, Long managerId) {
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

            point = point.subtract(getRewardsPointSumBySale(sale.getObjectId(), rewardType, RewardStatus.CHARGED, managerId));

        }

        return point;
    }

    public BigDecimal calcRewardPoint(Sale sale, Long rewardType, Date month, BigDecimal rewardPoint) {
        return calcRewardPoint(sale, rewardType, month, rewardPoint, null);
    }

    public void calculateSaleReward(Sale sale, List<SaleItem> saleItems, long rewardStatus) {
        Long rewardType = saleService.isMkSaleItems(saleItems) ? RewardType.MYCOOK_SALE : RewardType.BASE_ASSORTMENT_SALE;

        if (getRewardsPointSum(sale.getObjectId(), rewardType, rewardStatus).compareTo(ZERO) > 0) {
            return;
        }

        Reward reward = new Reward();

        Period period = periodMapper.getActualPeriod();

        Date month = period.getOperatingMonth();

        BigDecimal total = sale.getPersonalRewardPoint();

        if (total == null){
            return;
        }

        reward.setSaleId(sale.getObjectId());
        reward.setWorkerId(sale.getSellerWorkerId());
        reward.setType(rewardType);
        reward.setTotal(total);
        reward.setDate(Dates.currentDate());
        reward.setMonth(month);
        reward.setRewardStatus(rewardStatus);
        reward.setPeriodId(period.getObjectId());

        if (rewardStatus == RewardStatus.ESTIMATED) {
            reward.setPoint(total);
        } else if (rewardStatus == RewardStatus.CHARGED) {
            reward.setPoint(calcRewardPoint(sale, rewardType, period.getOperatingMonth(), total));
        } else if (rewardStatus == RewardStatus.WITHDRAWN) {
            reward.setPoint(total);
        }

        updateLocal(sale, reward, period);

        if (reward.getPoint().compareTo(ZERO) != 0) {
            domainService.save(reward);
        }
    }

    private void calculateBonusReward(Sale sale, Period period, long rewardStatus){
        if (sale.getMkManagerBonusRewardPoint() != null && sale.getMkManagerBonusWorkerId() != null) {
            if (getRewardsPointSum(sale.getObjectId(), RewardType.MANAGER_MK_BONUS, rewardStatus).compareTo(ZERO) > 0) {
                return;
            }

            BigDecimal total = sale.getMkManagerBonusRewardPoint();

            if (total.compareTo(ZERO) > 0) {
                Reward reward = new Reward();

                reward.setSaleId(sale.getObjectId());
                reward.setWorkerId(sale.getMkManagerBonusWorkerId());
                reward.setType(RewardType.MANAGER_MK_BONUS);
                reward.setDate(Dates.currentDate());
                reward.setMonth(period.getOperatingMonth());
                reward.setPeriodId(period.getObjectId());
                reward.setRewardStatus(rewardStatus);

                reward.setTotal(total);

                reward.setPoint(ZERO);

                if (rewardStatus == RewardStatus.ESTIMATED) {
                    reward.setPoint(total);
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calcRewardPoint(sale, RewardType.MANAGER_MK_BONUS, period.getOperatingMonth(), total));
                }

                updateLocal(sale, reward, period);

                if (reward.getPoint().compareTo(ZERO) != 0) {
                    domainService.save(reward);
                }
            }
        }
    }

    private void calculateCulinaryReward(Sale sale, Period period, Long rewardStatus) {
        if (sale.getCulinaryRewardPoint() == null) {
            return;
        }

        if (sale.getCulinaryWorkerId() == null || sale.getSaleStatus() == null || sale.getSaleStatus() < SaleStatus.PAID) {
            return;
        }

        if (getRewardsPointSum(sale.getObjectId(), RewardType.CULINARY_WORKSHOP, rewardStatus).compareTo(ZERO) != 0) {
            return;
        }

        Reward reward = new Reward();

        reward.setSaleId(sale.getObjectId());
        reward.setType(RewardType.CULINARY_WORKSHOP);
        reward.setWorkerId(sale.getCulinaryWorkerId());
        reward.setDate(Dates.currentDate());
        reward.setMonth(period.getOperatingMonth());
        reward.setPeriodId(period.getObjectId());
        reward.setRewardStatus(rewardStatus);

        reward.setTotal(sale.getCulinaryRewardPoint());
        reward.setPoint(sale.getCulinaryRewardPoint());

        updateLocal(sale, reward, period);

        domainService.save(reward);
    }

    public void calculateCulinaryReward(Sale sale) {
        Period period = periodMapper.getActualPeriod();

        getRewardsBySaleId(sale.getObjectId(), RewardType.CULINARY_WORKSHOP).stream()
                .filter(r -> Objects.equals(r.getPeriodId(), period.getObjectId()))
                .forEach(r -> domainService.delete(r));

        calculateCulinaryReward(sale, period, RewardStatus.ESTIMATED);
        calculateCulinaryReward(sale, period, RewardStatus.CHARGED);
    }

    private void calculateManagerReward(Sale sale, WorkerReward workerReward, Period period, Long rewardStatus) {
        if (getRewardsPointSum(sale.getObjectId(), RewardType.MANAGER_PREMIUM, rewardStatus).compareTo(ZERO) > 0) {
            return;
        }

        BigDecimal total = calcManagerPoint(sale, workerReward.getRank());

        if (total.compareTo(ZERO) > 0){
            Reward reward = new Reward();

            reward.setSaleId(sale.getObjectId());
            reward.setWorkerId(sale.getSellerWorkerId());
            reward.setType(RewardType.MANAGER_PREMIUM);
            reward.setDate(Dates.currentDate());
            reward.setMonth(period.getOperatingMonth());
            reward.setStructureSaleVolume(workerReward.getStructureSaleVolume());
            reward.setRankId(workerReward.getRank());
            reward.setPeriodId(period.getObjectId());
            reward.setRewardStatus(rewardStatus);

            reward.setTotal(total);

            reward.setPoint(ZERO);

            if (rewardStatus == RewardStatus.ESTIMATED) {
                reward.setPoint(total);
            } else if (rewardStatus == RewardStatus.CHARGED) {
                reward.setPoint(calcRewardPoint(sale, RewardType.MANAGER_PREMIUM, period.getOperatingMonth(), total));
            }

            updateLocal(sale, reward, period);

            if (reward.getPoint().compareTo(ZERO) != 0) {
                domainService.save(reward);
            }
        }
    }

    private void calculatePersonalReward(WorkerReward workerReward, Period period, Long rewardStatus) {
        if (workerReward.getSaleVolume().compareTo(getParameter(43L)) >= 0) {
            Reward reward = new Reward();

            reward.setType(RewardType.PERSONAL_VOLUME);
            reward.setWorkerId(workerReward.getWorkerNode().getObjectId());
            reward.setSaleVolume(workerReward.getSaleVolume());
            reward.setPaymentVolume(workerReward.getPaymentVolume());
            reward.setDate(Dates.currentDate());
            reward.setMonth(period.getOperatingMonth());
            reward.setPeriodId(period.getObjectId());
            reward.setRewardStatus(rewardStatus);

            BigDecimal avgPoint = getParameter(44L);
            BigDecimal lowerPoint = getParameter(45L);
            BigDecimal greaterPoint = getParameter(46L);

            if (workerReward.getSaleVolume().compareTo(avgPoint) < 0) {
                reward.setTotal(lowerPoint);
            }else {
                reward.setTotal(greaterPoint);
            }

            reward.setPoint(ZERO);

            if (rewardStatus == RewardStatus.ESTIMATED ||  getRewardsPointSumByWorker(period.getObjectId(), reward.getWorkerId(),
                    RewardType.PERSONAL_VOLUME, RewardStatus.CHARGED).compareTo(ZERO) == 0) {
                if (workerReward.getPaymentVolume().compareTo(avgPoint) < 0) {
                    reward.setPoint(lowerPoint);
                } else {
                    reward.setPoint(greaterPoint);
                }
            }

            updateLocal(reward, period);

            if (reward.getPoint().compareTo(ZERO) != 0) {
                domainService.save(reward);
            }
        }
    }

    private void calculateGroupReward(WorkerReward workerReward, Period period, Long rewardStatus) {
        BigDecimal sum = getGroupVolumePercent(workerReward.getRank())
                .multiply(workerReward.getGroupSaleVolume())
                .divide(new BigDecimal("100"), 5, HALF_EVEN);

        List<Sale> groupSales = workerReward.getGroupSales();

        for (int i = 0; i < groupSales.size(); i++) {
            Sale sale = groupSales.get(i);
            BigDecimal total = getGroupVolumePercent(workerReward.getRank())
                    .multiply(sale.getTotal())
                    .divide(new BigDecimal("100"), 5, HALF_EVEN);

            sum = sum.subtract(total);

            if (i == groupSales.size() - 1) {
                if (sum.compareTo(ZERO) != 0) {
                    log.info("calculateGroupReward sum " + sale + " " + sum);
                }

                total = total.add(sum);
            }

            if (total.compareTo(ZERO) > 0) {
                Reward reward = new Reward();

                reward.setSaleId(sale.getObjectId());
                reward.setWorkerId(workerReward.getWorkerId());
                reward.setType(RewardType.GROUP_VOLUME);
                reward.setGroupSaleVolume(workerReward.getGroupSaleVolume());
                reward.setGroupPaymentVolume(workerReward.getGroupPaymentVolume());
                reward.setRankId(workerReward.getRank());
                reward.setDate(Dates.currentDate());
                reward.setMonth(period.getOperatingMonth());
                reward.setRewardStatus(rewardStatus);
                reward.setPeriodId(period.getObjectId());

                reward.setTotal(total);

                reward.setPoint(ZERO);

                if (rewardStatus == RewardStatus.ESTIMATED) {
                    reward.setPoint(total);
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calcRewardPoint(sale, RewardType.GROUP_VOLUME, period.getOperatingMonth(), total));
                }

                updateLocal(reward, period);

                if (reward.getPoint().compareTo(ZERO) != 0) {
                    domainService.save(reward);
                }
            }
        }
    }

    private void calculateStructureReward(WorkerReward workerReward, Period period, Long rewardStatus) {
        BigDecimal sum = ZERO;

        for (WorkerReward m : workerReward.getFirstStructureManagers()) {
            BigDecimal total = getGroupVolumePercent(workerReward.getRank())
                    .subtract(getGroupVolumePercent(m.getRank()))
                    .multiply(m.getStructureSaleVolume())
                    .divide(new BigDecimal("100"), 5, HALF_EVEN);

            sum = sum.add(total);
        }

        List<WorkerReward> firstStructureManagers = workerReward.getFirstStructureManagers();

        for (int i = 0; i < firstStructureManagers.size(); i++) {
            WorkerReward m = firstStructureManagers.get(i);

            List<Sale> structureSales = m.getStructureSales();

            for (int j = 0; j < structureSales.size(); j++) {
                Sale sale = structureSales.get(j);

                BigDecimal total = getGroupVolumePercent(workerReward.getRank())
                        .subtract(getGroupVolumePercent(m.getRank()))
                        .multiply(sale.getTotal())
                        .divide(new BigDecimal("100"), 5, HALF_EVEN);

                sum = sum.subtract(total);

                if (i == firstStructureManagers.size() - 1 && j == structureSales.size() - 1) {
                    if (sum.compareTo(ZERO) != 0) {
                        log.info("calculateStructureReward sum " + m + " " + sale + " " + sum);
                    }

                    total = total.add(sum);
                }

                if (total.compareTo(ZERO) > 0) {
                    Reward reward = new Reward();

                    reward.setSaleId(sale.getObjectId());
                    reward.setWorkerId(workerReward.getWorkerId());
                    reward.setType(RewardType.STRUCTURE_VOLUME);
                    reward.setRankId(workerReward.getRank());
                    reward.setManagerId(m.getWorkerId());
                    reward.setManagerRankId(m.getRank());
                    reward.setStructureSaleVolume(workerReward.getStructureSaleVolume());
                    reward.setStructurePaymentVolume(workerReward.getStructurePaymentVolume());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(period.getOperatingMonth());
                    reward.setPeriodId(period.getObjectId());
                    reward.setRewardStatus(rewardStatus);

                    reward.setTotal(total);

                    reward.setPoint(ZERO);

                    if (rewardStatus == RewardStatus.ESTIMATED) {
                        reward.setPoint(total);
                    } else if (rewardStatus == RewardStatus.CHARGED) {
                        reward.setPoint(calcRewardPoint(sale, RewardType.STRUCTURE_VOLUME, period.getOperatingMonth(), total, reward.getManagerId()));
                    }

                    updateLocal(reward, period);

                    if (reward.getPoint().compareTo(ZERO) != 0) {
                        domainService.save(reward);
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards() throws RewardException {
        try {
            Period period = periodMapper.getActualPeriod();

            rewardMapper.deleteRewards(period.getObjectId());

            WorkerRewardTree tree = getWorkerRewardTree(period);

            saleService.getActiveSales().forEach(sale -> {
                calculateSaleReward(sale, saleService.getSaleItems(sale.getObjectId()), RewardStatus.ESTIMATED);
                calculateSaleReward(sale, saleService.getSaleItems(sale.getObjectId()), RewardStatus.CHARGED);

                if (sale.isFeeWithdraw()) {
                    calculateSaleReward(sale, saleService.getSaleItems(sale.getObjectId()), RewardStatus.WITHDRAWN);
                }

                calculateBonusReward(sale, period, RewardStatus.ESTIMATED);
                calculateBonusReward(sale, period, RewardStatus.CHARGED);

                calculateCulinaryReward(sale, period, RewardStatus.ESTIMATED);
                calculateCulinaryReward(sale, period, RewardStatus.CHARGED);

                WorkerReward workerReward = tree.getWorkerReward(sale.getSellerWorkerId());

                calculateManagerReward(sale, workerReward, period, RewardStatus.ESTIMATED);
                calculateManagerReward(sale, workerReward, period, RewardStatus.CHARGED);
            });

            tree.forEachLevel((l, rl) -> rl.forEach(workerReward -> {
                if (workerReward.getRank() > 0) {
                    Reward reward = new Reward();

                    reward.setWorkerId(workerReward.getWorkerId());
                    reward.setType(RewardType.RANK);
                    reward.setSaleVolume(workerReward.getSaleVolume());
                    reward.setPaymentVolume(workerReward.getPaymentVolume());
                    reward.setGroupSaleVolume(workerReward.getGroupSaleVolume());
                    reward.setGroupPaymentVolume(workerReward.getGroupPaymentVolume());
                    reward.setStructureSaleVolume(workerReward.getStructureSaleVolume());
                    reward.setStructurePaymentVolume(workerReward.getStructurePaymentVolume());
                    reward.setRankId(workerReward.getRank());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(period.getOperatingMonth());
                    reward.setPeriodId(period.getObjectId());
                    reward.setRewardStatus(RewardStatus.ESTIMATED);

                    domainService.save(reward);
                }
            }));

            tree.forEachLevel((l, rl) -> rl.forEach(workerReward -> {
                calculatePersonalReward(workerReward, period, RewardStatus.ESTIMATED);
                calculatePersonalReward(workerReward, period, RewardStatus.CHARGED);

                calculateGroupReward(workerReward, period, RewardStatus.ESTIMATED);
                calculateGroupReward(workerReward, period, RewardStatus.CHARGED);

                calculateStructureReward(workerReward, period, RewardStatus.ESTIMATED);
                calculateStructureReward(workerReward, period, RewardStatus.CHARGED);
            }));

            if (workerRewardTreeCache != null) {
                workerRewardTreeCache.refresh(period.getObjectId());
            }
        } catch (Exception e) {
            log.error("error calculate rewards", e);

            throw new RewardException(e.getMessage());
        }
    }

    private transient LoadingCache<Long, BigDecimal> parameterCache;

    private LoadingCache<Long, BigDecimal> getParameterCache(){
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
        return getWorkerRewardTreeCache(periodMapper.getActualPeriod().getObjectId()).getWorkerReward(worker.getObjectId());
    }

    public BigDecimal getPaymentsRate(Long workerId, Period period) {
        List<Payment> payments = paymentService.getPayments(FilterWrapper.of(new Payment().setPeriodId(period.getObjectId())));

        Map<Long, BigDecimal> pointSumMap = new HashMap<>();
        Map<Long, BigDecimal> amountSumMap = new HashMap<>();

        payments.stream()
                .filter(p -> saleService.getSale(p.getSaleId()).getSellerWorkerId().equals(workerId))
                .forEach(p -> {
                    Long countryId = saleService.getCountryId(p.getSaleId());

                    pointSumMap.put(countryId, pointSumMap.getOrDefault(countryId, ZERO).add(p.getPoint()));
                    amountSumMap.put(countryId, amountSumMap.getOrDefault(countryId, ZERO).add(p.getAmount()));
                });

        Long workerCountryId = workerService.getCountryId(workerId);

        List<BigDecimal> rates = new ArrayList<>();

        Date date = Dates.lastDayOfMonth(period.getOperatingMonth());

        amountSumMap.forEach((c, a) -> {
            if (a.compareTo(ZERO) != 0) {
                BigDecimal rate = a.divide(pointSumMap.get(c), 5, HALF_EVEN);

                if (!c.equals(workerCountryId)) {
                    rate = rate
                            .multiply(exchangeRateService.getExchangeRate(workerCountryId, date)
                            .divide(exchangeRateService.getExchangeRate(c, date), 5, HALF_EVEN));
                }

                rates.add(rate);
            }
        });

        return rates.size() > 0 ? rates.stream().reduce(ZERO, BigDecimal::add).divide(new BigDecimal(rates.size()), 5, HALF_EVEN) : ONE;
    }
}
