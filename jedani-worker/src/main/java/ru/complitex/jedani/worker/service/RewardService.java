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

    public List<Reward> getRewards(Long periodId, Long workerId){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward()
                .setWorkerId(workerId)
                .setPeriodId(periodId)));
    }

    public BigDecimal getRewardsTotal(Long periodId, Long workerId, Long rewardTypeId, Long rewardStatusId) {
        return getRewards(periodId, workerId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .reduce(ZERO, ((t, p) -> t.add(p.getTotal())), BigDecimal::add);
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

    public List<WorkerReward> getPrivateGroup(WorkerReward r) {
        List<WorkerReward> group = new ArrayList<>();

        for (WorkerReward c : r.getChildRewards()){
            if (!c.isManager()){
                group.add(c);

                group.addAll(getPrivateGroup(c));
            }
        }

        return group;
    }

    private void calcSaleVolumes(WorkerRewardTree tree, Period period){
        Set<Long> activeSaleWorkerIds = saleService.getActiveSaleWorkerIds();

        tree.forEachLevel((l, rl) -> rl.forEach(r -> {
            if (activeSaleWorkerIds.contains(r.getWorkerNode().getObjectId())) {
                r.setSaleVolume(saleService.getSaleVolume(r.getWorkerNode().getObjectId(), period));
            }

            r.setGroupSaleVolume(getPrivateGroup(r).stream()
                    .reduce(ZERO, (v, c) -> v.add(c.getSaleVolume()), BigDecimal::add));

            r.setStructureSaleVolume(r.getChildRewards().stream()
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

            r.setGroupPaymentVolume(getPrivateGroup(r).stream()
                    .reduce(ZERO, (v, c) -> v.add(c.getPaymentVolume()), BigDecimal::add));

            r.setStructurePaymentVolume(r.getChildRewards().stream()
                    .reduce(r.getPaymentVolume(), (v, c) -> v.add(c.getStructurePaymentVolume()), BigDecimal::add));
        }));
    }

    private void calcRegistrationCount(WorkerRewardTree tree, Period period){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> {
            r.setRegistrationCount(r.getChildRewards().stream()
                    .reduce(0L, (v, c) -> v + (isNewWorker(c.getWorkerId(), period) ? 1L : 0L), Long::sum));

            r.setGroupRegistrationCount(getPrivateGroup(r).stream()
                    .reduce(0L, (v, c) -> v + (isNewWorker(c.getWorkerId(), period) ? 1 : 0), Long::sum));

            r.setStructureManagerCount(r.getChildRewards().stream()
                    .filter(WorkerReward::isManager)
                    .reduce(0L, (v, c) -> v + c.getStructureManagerCount() + 1, Long::sum));
        }));
    }

    private boolean isNewWorker(Long workerId, Period period){
        Date registrationDate = domainService.getDate(Worker.ENTITY_NAME, workerId, Worker.REGISTRATION_DATE);

        Long status =  domainService.getNumber(Worker.ENTITY_NAME, workerId, Worker.STATUS);

        return registrationDate != null && Dates.isSameMonth(registrationDate, period.getOperatingMonth()) &&
                !Objects.equals(status, WorkerStatus.MANAGER_CHANGED);
    }

    private void calcFirstLevelCount(WorkerRewardTree tree, Period period){
       tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelCount(r.getChildRewards().stream()
                .filter(c -> {
                    Date registrationDate =  domainService.getDate(Worker.ENTITY_NAME, c.getWorkerId(), Worker.REGISTRATION_DATE);

                    return registrationDate != null && registrationDate.before(Dates.nextMonth(period.getOperatingMonth())) && c.isPk();
                })
                .count())));

        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelPersonalCount(r.getChildRewards().stream()
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
            }
        }

        if (reward.getRate() == null){
            reward.setRate(priceService.getRate(sale.getStorageId(), date));
        }

        if (reward.getPoint().compareTo(ZERO) != 0) {
            reward.setAmount(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));

            if (reward.getDiscount() != null){
                reward.setAmount(reward.getAmount().multiply(reward.getDiscount()).setScale(5, HALF_EVEN));
            }
        }else {
            reward.setAmount(ZERO);
        }
    }

    private void updateLocal(Reward reward, Period period){
        Long currencyId = workerService.getCurrencyId(reward.getWorkerId());

        reward.setRate(paymentService.getPaymentsRate(currencyId, period.getObjectId()));

        if (reward.getPoint().compareTo(ZERO) > 0) {
            reward.setAmount(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));
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

            point = point.subtract(getRewardsPointSum(sale.getObjectId(), rewardType, RewardStatus.CHARGED));

        }

        return point;
    }

    private BigDecimal calcRewardPoint(Long workerId, Long managerId, Long rewardType, Long periodId, BigDecimal saleVolume,
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

            point = point.subtract(getRewardsTotal(periodId, workerId, managerId, rewardType));
        }

        return point;
    }

    public List<WorkerReward> getStructureManagers(WorkerReward r){
        List<WorkerReward> managers = new ArrayList<>();

        for (WorkerReward c : r.getChildRewards()){
            if (c.isManager()){
                managers.add(c);
            }

            managers.addAll(getStructureManagers(c));
        }

        return managers;
    }

    public List<WorkerReward> getFirstStructureManagers(WorkerReward r){
        List<WorkerReward> managers = new ArrayList<>();

        for (WorkerReward c : r.getChildRewards()){
            if (c.isManager()){
                managers.add(c);
            } else {
                managers.addAll(getFirstStructureManagers(c));
            }
        }

        return managers;
    }

    public void calculateSaleReward(Sale sale, List<SaleItem> saleItems, long rewardStatus){
        Reward reward = new Reward();

        Period period = periodMapper.getActualPeriod();

        Date month = period.getOperatingMonth();

        Long rewardType = saleService.isMkSaleItems(saleItems) ? RewardType.MYCOOK_SALE : RewardType.BASE_ASSORTMENT_SALE;

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
            reward.setPoint(total.subtract(getRewardsPointSum(sale.getObjectId(), rewardType, RewardStatus.ESTIMATED)));
        } else if (rewardStatus == RewardStatus.CHARGED) {
            reward.setPoint(calcRewardPoint(sale, rewardType, period.getOperatingMonth(), total));
        } else if (rewardStatus == RewardStatus.WITHDRAWN) {
            reward.setPoint(total.subtract(getRewardsPointSum(sale.getObjectId(), rewardType, RewardStatus.WITHDRAWN)));
        }

        updateLocal(sale, reward);

        if (reward.getPoint().compareTo(ZERO) != 0) {
            domainService.save(reward);
        }
    }

    private void calculateMkBonusReward(Sale sale, Period period, long rewardStatus){
        if (sale.getMkManagerBonusRewardPoint() != null && sale.getMkManagerBonusWorkerId() != null) {
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
                    reward.setPoint(total.subtract(getRewardsPointSum(sale.getObjectId(), RewardType.MANAGER_MK_BONUS, RewardStatus.ESTIMATED)));
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calcRewardPoint(sale, RewardType.MANAGER_MK_BONUS, period.getOperatingMonth(), total));
                }

                updateLocal(sale, reward);

                if (reward.getPoint().compareTo(ZERO) != 0) {
                    domainService.save(reward);
                }
            }
        }
    }

    private void calculateCulinaryReward(Sale sale, Period period, Long rewardStatus) {
        if (sale.getCulinaryWorkerId() == null || sale.getCulinaryRewardPoint() == null) {
            return;
        }

        if (sale.getSaleStatus() == null || sale.getSaleStatus() < SaleStatus.PAID) {
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

        updateLocal(sale, reward);

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

    private void calculateManagerPremiumReward(Sale sale, WorkerReward workerReward, Period period, Long rewardStatus) {
        BigDecimal total = calcManagerPremiumPoint(sale, workerReward.getRank());

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
                reward.setPoint(total.subtract(getRewardsPointSum(sale.getObjectId(), RewardType.MANAGER_PREMIUM, RewardStatus.ESTIMATED)));
            } else if (rewardStatus == RewardStatus.CHARGED) {
                reward.setPoint(calcRewardPoint(sale, RewardType.MANAGER_PREMIUM, period.getOperatingMonth(), total));
            }

            updateLocal(sale, reward);

            if (reward.getPoint().compareTo(ZERO) != 0) {
                domainService.save(reward);
            }
        }
    }

    private void calculatePersonalVolumeReward(WorkerReward workerReward, Period period, Long rewardStatus) {
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

            if (rewardStatus == RewardStatus.ESTIMATED || getRewardsTotal(period.getObjectId(), reward.getWorkerId(), RewardType.PERSONAL_VOLUME, RewardStatus.CHARGED
            ).compareTo(ZERO) == 0) {
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

    private void calculateGroupVolumeReward(WorkerReward workerReward, Period period, Long rewardStatus){
        BigDecimal total = getGroupVolumePercent(workerReward.getRank()).multiply(workerReward.getGroupSaleVolume()).divide(new BigDecimal("100"), 5, HALF_EVEN);

        if (total.compareTo(ZERO) > 0) {
            Reward reward = new Reward();

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
                reward.setPoint(calcRewardPoint(workerReward.getWorkerId(), null, RewardType.GROUP_VOLUME, period.getObjectId(),
                        workerReward.getGroupSaleVolume(), workerReward.getGroupPaymentVolume(), total));
            }

            updateLocal(reward, period);

            if (reward.getPoint().compareTo(ZERO) != 0) {
                domainService.save(reward);
            }
        }
    }

    private void calculateStructureVolume(WorkerReward workerReward, Period period, Long rewardStatus) {
        getFirstStructureManagers(workerReward).forEach(m -> {
            BigDecimal total = getGroupVolumePercent(workerReward.getRank())
                    .subtract(getGroupVolumePercent(m.getRank()))
                    .multiply(m.getStructureSaleVolume())
                    .divide(new BigDecimal("100"), 5, HALF_EVEN);

            if (total.compareTo(ZERO) > 0) {
                Reward reward = new Reward();

                reward.setWorkerId(workerReward.getWorkerId());
                reward.setType(RewardType.STRUCTURE_VOLUME);
                reward.setRankId(workerReward.getRank());
                reward.setManagerId(m.getWorkerId());
                reward.setManagerRankId(m.getRank());
                reward.setStructureSaleVolume(m.getStructureSaleVolume());
                reward.setStructurePaymentVolume(m.getStructurePaymentVolume());
                reward.setDate(Dates.currentDate());
                reward.setMonth(period.getOperatingMonth());
                reward.setPeriodId(period.getObjectId());
                reward.setRewardStatus(rewardStatus);

                reward.setTotal(total);

                reward.setPoint(ZERO);

                if (rewardStatus == RewardStatus.ESTIMATED) {
                    reward.setPoint(total);
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calcRewardPoint(workerReward.getWorkerId(), m.getWorkerId(), RewardType.STRUCTURE_VOLUME, period.getObjectId(),
                            m.getStructureSaleVolume(), m.getStructurePaymentVolume(), total));
                }

                updateLocal(reward, period);

                if (reward.getPoint().compareTo(ZERO) != 0) {
                    domainService.save(reward);
                }
            }
        });
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards() throws RewardException {
        try {
            Period period = periodMapper.getActualPeriod();

            rewardMapper.deleteRewards(period.getObjectId());

            WorkerRewardTree tree = getWorkerRewardTree(period);

            saleService.getActiveSales().forEach(sale -> {
                if (sale.isFeeWithdraw()) {
                    calculateSaleReward(sale, saleService.getSaleItems(sale.getObjectId()), RewardStatus.WITHDRAWN);
                } else {
                    calculateSaleReward(sale, saleService.getSaleItems(sale.getObjectId()), RewardStatus.ESTIMATED);
                    calculateSaleReward(sale, saleService.getSaleItems(sale.getObjectId()), RewardStatus.CHARGED);
                }

                calculateMkBonusReward(sale, period, RewardStatus.ESTIMATED);
                calculateMkBonusReward(sale, period, RewardStatus.CHARGED);

                calculateCulinaryReward(sale, period, RewardStatus.ESTIMATED);
                calculateCulinaryReward(sale, period, RewardStatus.CHARGED);
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

            saleService.getActiveSales().forEach(sale -> {
                WorkerReward workerReward = tree.getWorkerReward(sale.getSellerWorkerId());

                calculateManagerPremiumReward(sale, workerReward, period, RewardStatus.ESTIMATED);
                calculateManagerPremiumReward(sale, workerReward, period, RewardStatus.CHARGED);
            });

            tree.forEachLevel((l, rl) -> rl.forEach(workerReward -> {
                calculatePersonalVolumeReward(workerReward, period, RewardStatus.ESTIMATED);
                calculatePersonalVolumeReward(workerReward, period, RewardStatus.CHARGED);

                calculateGroupVolumeReward(workerReward, period, RewardStatus.ESTIMATED);
                calculateGroupVolumeReward(workerReward, period, RewardStatus.CHARGED);

                calculateStructureVolume(workerReward, period, RewardStatus.ESTIMATED);
                calculateStructureVolume(workerReward, period, RewardStatus.CHARGED);
            }));

            if (workerRewardTreeCache != null) {
                workerRewardTreeCache.invalidateAll();
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
        return getWorkerRewardTree(periodMapper.getActualPeriod()).getWorkerReward(worker.getObjectId());
    }
}
