package ru.complitex.jedani.worker.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.tuple.Pair;
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
import ru.complitex.jedani.worker.mapper.RewardParameterMapper;

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
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class RewardService2 implements Serializable {
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
    private RewardParameterMapper rewardParameterMapper;

    @Inject
    private PriceService priceService;

    @Inject
    private ExchangeRateService exchangeRateService;

    private boolean test = false;

    private List<Reward> rewards = new ArrayList<>();

    private transient LoadingCache<Long, List<Reward>> rewardsBySaleIdCache;

    private LoadingCache<Long, List<Reward>> getRewardsBySaleIdCache() {
        if (rewardsBySaleIdCache == null){
            rewardsBySaleIdCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(saleId ->
                            domainService.getDomains(Reward.class, FilterWrapper.of(new Reward().setSaleId(saleId)))));
        }

        return rewardsBySaleIdCache;
    }

    public List<Reward> getRewardsBySaleId(Long saleId) {
        try {
            return getRewardsBySaleIdCache().get(saleId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Reward> getRewardsBySaleId(Long saleId, Long rewardTypeId) {
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .collect(Collectors.toList());
    }

    public BigDecimal getRewardsPointSumBefore(Long saleId, Long rewardTypeId, Long rewardStatusId, Period period) {
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .filter(r -> {
                    Period p = periodMapper.getPeriod(r.getPeriodId());

                    return p != null && p.getOperatingMonth().before(period.getOperatingMonth());
                })
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsPointSumBySaleBefore(Long saleId, Long rewardTypeId, Long rewardStatusId, Long managerId, Period period) {
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .filter(r -> Objects.equals(r.getManagerId(), managerId))
                .filter(r -> {
                    Period p = periodMapper.getPeriod(r.getPeriodId());

                    return p != null && p.getOperatingMonth().before(period.getOperatingMonth());
                })
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    private static class WorkerPeriodId {
        private final Long workerId;
        private final Long periodId;

        public WorkerPeriodId(Long workerId, Long periodId) {
            this.workerId = workerId;
            this.periodId = periodId;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof WorkerPeriodId) {
                return Objects.equals(workerId, ((WorkerPeriodId) o).workerId) &&
                        Objects.equals(periodId, ((WorkerPeriodId) o).periodId);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(workerId, periodId);
        }
    }

    private transient LoadingCache<WorkerPeriodId, List<Reward>> rewardsByPeriodAndWorkerCache;

    private LoadingCache<WorkerPeriodId, List<Reward>> getRewardsByPeriodWorkerIdCache() {
        if (rewardsByPeriodAndWorkerCache == null){
            rewardsByPeriodAndWorkerCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(workerPeriodId ->
                            domainService.getDomains(Reward.class, FilterWrapper.of(new Reward()
                                    .setWorkerId(workerPeriodId.workerId)
                                    .setPeriodId(workerPeriodId.periodId)))));
        }

        return rewardsByPeriodAndWorkerCache;
    }

    public List<Reward> getRewardsFromCache(Long periodId, Long workerId){
        try {
            return getRewardsByPeriodWorkerIdCache().get(new WorkerPeriodId(workerId, periodId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public BigDecimal getRewardsPointSumByWorker(Long periodId, Long workerId, Long rewardTypeId, Long rewardStatusId) {
        return getRewardsFromCache(periodId, workerId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public BigDecimal getRewardsLocal(Long periodId, Long workerId, Long rewardStatusId) {
        return getRewardsFromCache(periodId, workerId).stream()
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .map(r -> r.getAmount() != null ? r.getAmount() : ZERO)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal getRewardsLocalByCurrency(Long periodId, Long rewardStatusId, Long currencyId) {
        return getRewardsFromCache(periodId, null).stream()
                .filter(r -> Objects.equals(r.getRewardStatus(), rewardStatusId))
                .filter(r -> Objects.equals(workerService.getCurrencyId(r.getWorkerId()), currencyId))
                .map(r -> r.getAmount() != null ? r.getAmount() : ZERO)
                .reduce(ZERO, BigDecimal::add);
    }

    public RewardTree getRewardTree(Period period) {
        RewardTree tree = new RewardTree(workerNodeService.getWorkerNodeMap());

        calculateRewardTree(tree, period);

        return tree;
    }

    private transient LoadingCache<Long, RewardTree> rewardTreeCache;

    private LoadingCache<Long, RewardTree> getRewardTreeCache(){
        if (rewardTreeCache == null){
            rewardTreeCache = CacheBuilder.newBuilder().build(CacheLoader.from(periodId ->
                    getRewardTree(periodMapper.getPeriod(periodId))));
        }

        return rewardTreeCache;
    }

    public RewardTree getRewardTreeCache(Long periodId) {
        try {
            return getRewardTreeCache().get(periodId);
        } catch (ExecutionException e) {
            throw  new RuntimeException(e);
        }
    }

    private Long getRank(BigDecimal saleVolume, Period period) {
        if (saleVolume.compareTo(getParameter(20L, period)) >= 0){
            return RankType.PLATINUM_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(19L, period)) >= 0){
            return RankType.GOLD_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(18L, period)) >= 0){
            return RankType.SILVER_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(17L, period)) >= 0){
            return RankType.REGIONAL_MANAGER;
        }else if (saleVolume.compareTo(getParameter(16L, period)) >= 0){
            return RankType.AREA_MANAGER;
        }else if (saleVolume.compareTo(getParameter(15L, period)) >= 0){
            return RankType.DIVISION_MANAGER;
        }else if (saleVolume.compareTo(getParameter(14L, period)) >= 0){
            return RankType.SENIOR_MANAGER;
        }else if (saleVolume.compareTo(getParameter(13L, period)) >= 0){
            return RankType.SENIOR_ASSISTANT;
        }else if (saleVolume.compareTo(getParameter(12L, period)) >= 0){
            return RankType.TEAM_MANAGER;
        }else if (saleVolume.compareTo(getParameter(11L, period)) >= 0){
            return RankType.MANAGER_JUNIOR;
        }else if (saleVolume.compareTo(getParameter(47L, period)) >= 0){
            return RankType.MANAGER_ASSISTANT;
        }

        return 0L;
    }

    private void calculateRewardTree(RewardTree rewardTree, Period period) {
        Set<Long> activeSaleWorkerIds = saleService.getSaleWorkerIds();

        rewardTree.forEachLevel((l, rl) -> rl.forEach(r -> {
            r.setFirstLevelCount(r.getRewardNodes().stream()
                    .filter(c -> {
                        Date registrationDate =  domainService.getDate(Worker.ENTITY_NAME, c.getWorkerId(), Worker.REGISTRATION_DATE);

                        return registrationDate != null && registrationDate.before(Dates.nextMonth(period.getOperatingMonth())) && c.isPk();
                    })
                    .count());

            r.setFirstLevelPersonalCount(r.getRewardNodes().stream()
                    .filter(c -> {
                        Date registrationDate =  domainService.getDate(Worker.ENTITY_NAME, c.getWorkerId(), Worker.REGISTRATION_DATE);

                        Long status =  domainService.getNumber(Worker.ENTITY_NAME, c.getWorkerId(), Worker.STATUS);

                        return registrationDate != null && registrationDate.before(Dates.nextMonth(period.getOperatingMonth())) &&
                                !Objects.equals(status, WorkerStatus.MANAGER_CHANGED) && c.isPk();
                    })
                    .count());

            if (activeSaleWorkerIds.contains(r.getWorkerNode().getWorkerId())) {
                r.setPaymentVolume(paymentService.getPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getWorkerId(), period));
            }

            r.setYearPaymentVolume(paymentService.getYearPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getWorkerId()));

            Date registrationDate = domainService.getDate(Worker.ENTITY_NAME, r.getWorkerId(), Worker.REGISTRATION_DATE);

            r.setPk(Dates.isLessYear(period.getOperatingMonth(), registrationDate) || r.getYearPaymentVolume().compareTo(BigDecimal.valueOf(200)) >= 0);

            r.setGroupPaymentVolume(r.getGroup().stream()
                    .reduce(ZERO, (v, c) -> v.add(c.getPaymentVolume()), BigDecimal::add));

            r.setStructurePaymentVolume(r.getRewardNodes().stream()
                    .reduce(r.getPaymentVolume(), (v, c) -> v.add(c.getStructurePaymentVolume()), BigDecimal::add));

            r.setSales(saleService.getSales(r.getWorkerNode().getWorkerId(), period));

            r.setSaleVolume(r.getSales().stream()
                    .map(s -> s.getTotal() != null ? s.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            r.getGroup().forEach(c -> r.getGroupSales().addAll(c.getSales()));

            r.setGroupSaleVolume(r.getGroup().stream()
                    .reduce(ZERO, (v, c) -> v.add(c.getSaleVolume()), BigDecimal::add));

            r.getStructureSales().addAll(r.getSales());

            r.getRewardNodes().forEach(c -> r.getStructureSales().addAll(c.getStructureSales()));

            r.setStructureSaleVolume(r.getRewardNodes().stream()
                    .reduce(r.getSaleVolume(), (v, c) -> v.add(c.getStructureSaleVolume()), BigDecimal::add));

            r.setRegistrationCount(r.getRewardNodes().stream()
                    .filter(c -> isNewWorker(c.getWorkerId(), period))
                    .count());

            r.setGroupRegistrationCount(r.getGroup().stream()
                    .filter(c -> isNewWorker(c.getWorkerId(), period))
                    .count());

            if (r.getFirstLevelPersonalCount() >= 4 && r.getGroupRegistrationCount() >= 2 && r.getPaymentVolume().compareTo(BigDecimal.valueOf(200)) >= 0) {
                r.setRank(getRank(r.getStructureSaleVolume(), period));
            }

            r.setStructureManagerCount(r.getRewardNodes().stream()
                    .reduce(0L, (v, c) -> v + c.getStructureManagerCount() + (c.isManager() ? 1 : 0), Long::sum));
        }));
    }

    private boolean isNewWorker(Long workerId, Period period){
        Date registrationDate = domainService.getDate(Worker.ENTITY_NAME, workerId, Worker.REGISTRATION_DATE);

        Long status =  domainService.getNumber(Worker.ENTITY_NAME, workerId, Worker.STATUS);

        return registrationDate != null && Dates.isSameMonth(registrationDate, period.getOperatingMonth()) &&
                !Objects.equals(status, WorkerStatus.MANAGER_CHANGED);
    }

    public BigDecimal getPersonalRewardPoint(Sale sale, List<SaleItem> saleItems, Period period){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            boolean mkPremium = saleService.isMycookPremiumSaleItems(saleItems);
            boolean mkTouch = saleService.isMycookTouchSaleItems(saleItems);

            if (sale.isSasRequest()){
                point = getParameter(1L, period);
            }else if (w.getMkStatus() == MkStatus.STATUS_PROMO){
                if (mkPremium){
                    point = getParameter(2L, period);
                }else if (mkTouch){
                    point = getParameter(3L, period);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_JUST){
                if (mkPremium){
                    point = getParameter(4L, period);
                }else if (mkTouch){
                    point = getParameter(5L, period);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_VIP){
                if (sale.getManagerBonusWorkerId() != null){
                    if (mkPremium){
                        point = getParameter(6L, period).subtract(getParameter(9L, period));
                    }else if (mkTouch){
                        point = getParameter(7L, period).subtract(getParameter(10L, period));
                    }
                }else if (mkPremium){
                    point = getParameter(6L, period);
                }else if (mkTouch){
                    point = getParameter(7L, period);
                }
            }
        }else if (sale.getType() == SaleType.RANGE && sale.getTotal() != null){
            point = sale.getTotal().multiply(getParameter(8L, period));
        }

        return point;
    }

    public BigDecimal getManagerBonusRewardPoint(Sale sale, List<SaleItem> saleItems, Period period){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            if (saleService.isMycookPremiumSaleItems(saleItems)){
                point = getParameter(9L, period);
            }else if (saleService.isMycookTouchSaleItems(saleItems)){
                point = getParameter(10L, period);
            }
        }

        return point;
    }

    public BigDecimal getCulinaryRewardPoint(Sale sale, List<SaleItem> saleItems, Period period){
        if (saleService.isMycookSaleItems(saleItems)){
            if (!sale.isSasRequest()) {
                return getParameter(41L, period);
            } else {
                return getParameter(42L, period);
            }
        }

        return ZERO;
    }

    public void updateLocal(Sale sale, Reward reward, Period period){
        try {
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

                    Date month = periodMapper.getPeriod(sale.getPeriodId()).getOperatingMonth();

                    List<Ratio> ratios = domainService.getDomains(Ratio.class, FilterWrapper.of((Ratio) new Ratio()
                            .setCountryId(saleService.getCountryId(sale))
                            .setBegin(month)
                            .setEnd(month)
                            .setFilter(Ratio.BEGIN, Attribute.FILTER_BEFORE_OR_EQUAL_DATE)
                            .setFilter(Ratio.END, Attribute.FILTER_AFTER_DATE)));

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
                if (!test) {
                    throw new RuntimeException("negative reward point error " + reward);
                }
            }
        } catch (Exception e) {
            log.error("error updateLocal {}, {}", sale, reward);

            throw new RuntimeException(e);
        }
    }

    private void updateLocalByWorker(Reward reward, Period period) {
        if (reward.getPoint().compareTo(ZERO) >= 0) {
            reward.setRate(getPaymentsRateByWorkerId(reward.getWorkerId(), period.getObjectId()));

            reward.setAmount(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));
        } else {
            if (!test) {
                throw new RuntimeException("negative reward point error " + reward);
            }
        }
    }

    private void updateLocalBySale(Reward reward, Period period) {
        if (reward.getPoint().compareTo(ZERO) >= 0) {
            reward.setRate(getPaymentsRateBySaleId(reward.getSaleId(), period.getObjectId()));

            reward.setAmount(reward.getPoint().multiply(reward.getRate()).setScale(5, HALF_EVEN));
        } else {
            if (!test) {
                throw new RuntimeException("negative reward point error " + reward);
            }
        }
    }

    private BigDecimal calcManagerPoint(Sale sale, Long rank, Period period){
        switch (rank.intValue()){
            case (int) RankType.MANAGER_ASSISTANT:
                return calcManagerPoint(sale, 48L, 49L, period);
            case (int) RankType.MANAGER_JUNIOR:
                return calcManagerPoint(sale, 21L, 31L, period);
            case (int) RankType.TEAM_MANAGER:
                return calcManagerPoint(sale, 22L, 32L, period);
            case (int) RankType.SENIOR_ASSISTANT:
                return calcManagerPoint(sale, 23L, 33L, period);
            case (int) RankType.SENIOR_MANAGER:
                return calcManagerPoint(sale, 24L, 34L, period);
            case (int) RankType.DIVISION_MANAGER:
                return calcManagerPoint(sale, 25L, 35L, period);
            case (int) RankType.AREA_MANAGER:
                return calcManagerPoint(sale, 26L, 36L, period);
            case (int) RankType.REGIONAL_MANAGER:
                return calcManagerPoint(sale, 27L, 37L, period);
            case (int) RankType.SILVER_DIRECTOR:
                return calcManagerPoint(sale, 28L, 38L, period);
            case (int) RankType.GOLD_DIRECTOR:
                return calcManagerPoint(sale, 29L, 39L, period);
            case (int) RankType.PLATINUM_DIRECTOR:
                return calcManagerPoint(sale, 30L, 40L, period);
        }

        return ZERO;
    }

    private BigDecimal calcManagerPoint(Sale sale, Long mkParameterId, Long baParameterId, Period period){
        if (sale.getType() == SaleType.MYCOOK){
            return getParameter(mkParameterId, period);
        }else if (sale.getType() == SaleType.RANGE && sale.getTotal() != null){
            return sale.getTotal().multiply(getParameter(baParameterId, period));
        }

        return ZERO;
    }

    private BigDecimal getGroupVolumePercent(Long rank, Period period){
        switch (rank.intValue()){
            case (int) RankType.MANAGER_ASSISTANT:
                return getParameter(50L, period);
            case (int) RankType.MANAGER_JUNIOR:
                return getParameter(51L, period);
            case (int) RankType.TEAM_MANAGER:
                return getParameter(52L, period);
            case (int) RankType.SENIOR_ASSISTANT:
                return getParameter(53L, period);
            case (int) RankType.SENIOR_MANAGER:
                return getParameter(54L, period);
            case (int) RankType.DIVISION_MANAGER:
                return getParameter(55L, period);
            case (int) RankType.AREA_MANAGER:
                return getParameter(56L, period);
            case (int) RankType.REGIONAL_MANAGER:
                return getParameter(57L, period);
            case (int) RankType.SILVER_DIRECTOR:
                return getParameter(58L, period);
            case (int) RankType.GOLD_DIRECTOR:
                return getParameter(59L, period);
            case (int) RankType.PLATINUM_DIRECTOR:
                return getParameter(60L, period);
        }

        return ZERO;
    }

    public BigDecimal calculateRewardPoint(Sale sale, Long rewardType, BigDecimal rewardPoint, Long managerId, Period period) {
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0 && sale.getTotal() != null) {
            BigDecimal paid = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId())
                    .divide(sale.getTotal(), 5, RoundingMode.HALF_EVEN);

            if (paid.compareTo(new BigDecimal("0.2")) >= 0){
                point = point.add(rewardPoint).multiply(new BigDecimal("0.25"));
            }

            if (paid.compareTo(new BigDecimal("0.7")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.35")));
            }

            if (paid.compareTo(new BigDecimal("1")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.40")));
            }

            point = point.subtract(getRewardsPointSumBySaleBefore(sale.getObjectId(), rewardType, RewardStatus.CHARGED, managerId, period));

        }

        return point;
    }

    public BigDecimal calculateRewardPoint(Sale sale, Long rewardType, BigDecimal rewardPoint, Period period) {
        return calculateRewardPoint(sale, rewardType, rewardPoint, null, period);
    }

    public void calculateSaleReward(Sale sale, Period period, long rewardStatus) {
        Long rewardType = sale.getType().equals(SaleType.MYCOOK) ? RewardType.PERSONAL_MYCOOK : RewardType.PERSONAL_RANGE;

        {
            Reward reward = new Reward();

            BigDecimal total = sale.getPersonalRewardPoint();

            if (total == null){
                return;
            }

            reward.setSaleId(sale.getObjectId());
            reward.setWorkerId(sale.getSellerWorkerId());
            reward.setType(rewardType);
            reward.setTotal(total);
            reward.setDate(Dates.currentDate());
            reward.setMonth(period.getOperatingMonth());
            reward.setRewardStatus(rewardStatus);
            reward.setPeriodId(period.getObjectId());

            if (rewardStatus == RewardStatus.ESTIMATED) {
                reward.setPoint(total);
            } else if (rewardStatus == RewardStatus.CHARGED) {
                reward.setPoint(calculateRewardPoint(sale, rewardType, total, period));
            } else if (rewardStatus == RewardStatus.WITHDRAWN) {
                reward.setPoint(total);
            }

            updateLocal(sale, reward, period);

            if (reward.getPoint().compareTo(ZERO) != 0 && (rewardStatus == RewardStatus.CHARGED ||
                    getRewardsPointSumBefore(sale.getObjectId(), rewardType, rewardStatus, period).compareTo(ZERO) == 0)) {
                if (test) {
                    rewards.add(reward);
                } else {
                    domainService.save(reward);
                }
            }
        }
    }

    private void calculateBonusReward(Sale sale, Period period, long rewardStatus){
        if (sale.getManagerBonusRewardPoint() != null && sale.getManagerBonusWorkerId() != null) {
            BigDecimal total = sale.getManagerBonusRewardPoint();

            if (total.compareTo(ZERO) > 0) {
                Reward reward = new Reward();

                reward.setSaleId(sale.getObjectId());
                reward.setWorkerId(sale.getManagerBonusWorkerId());
                reward.setType(RewardType.MANAGER_BONUS);
                reward.setDate(Dates.currentDate());
                reward.setMonth(period.getOperatingMonth());
                reward.setPeriodId(period.getObjectId());
                reward.setRewardStatus(rewardStatus);

                reward.setTotal(total);

                reward.setPoint(ZERO);

                if (rewardStatus == RewardStatus.ESTIMATED) {
                    reward.setPoint(total);
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calculateRewardPoint(sale, RewardType.MANAGER_BONUS, total, period));
                }

                updateLocal(sale, reward, period);

                reward.setEstimatedId(reward.getObjectId());

                if (reward.getPoint().compareTo(ZERO) != 0 && (rewardStatus == RewardStatus.CHARGED ||
                        getRewardsPointSumBefore(sale.getObjectId(), RewardType.MANAGER_BONUS, rewardStatus, period).compareTo(ZERO) == 0)) {
                    if (test) {
                        rewards.add(reward);
                    } else {
                        domainService.save(reward);
                    }
                }
            }
        }
    }

    private void calculateCulinaryReward(Sale sale, Period period, Long rewardStatus) {
        if (sale.getCulinaryRewardPoint() == null || sale.getCulinaryWorkerId() == null || sale.getSaleStatus() == null || sale.getSaleStatus() < SaleStatus.PAID) {
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

        if (reward.getPoint().compareTo(ZERO) != 0 &&
                getRewardsPointSumBefore(sale.getObjectId(), RewardType.CULINARY_WORKSHOP, rewardStatus, period).compareTo(ZERO) == 0) {
            if (test) {
                rewards.add(reward);
            } else {
                domainService.save(reward);
            }
        }
    }

    public void calculateCulinaryReward(Sale sale) {
        Period period = periodMapper.getActualPeriod();

        getRewardsBySaleId(sale.getObjectId(), RewardType.CULINARY_WORKSHOP).stream()
                .filter(r -> Objects.equals(r.getPeriodId(), period.getObjectId()))
                .forEach(r -> domainService.delete(r));

        calculateCulinaryReward(sale, period, RewardStatus.ESTIMATED);
        calculateCulinaryReward(sale, period, RewardStatus.CHARGED);
    }

    private void calculateManagerReward(Sale sale, RewardNode rewardNode, Period period, Long rewardStatus) {
        if (Objects.equals(sale.getPeriodId(), period.getObjectId())) {
            BigDecimal total = calcManagerPoint(sale, rewardNode.getRank(), period);

            if (total.compareTo(ZERO) > 0){
                Reward reward = new Reward();

                reward.setSaleId(sale.getObjectId());
                reward.setWorkerId(sale.getSellerWorkerId());
                reward.setType(RewardType.MANAGER_MYCOOK);
                reward.setDate(Dates.currentDate());
                reward.setMonth(period.getOperatingMonth());
                reward.setStructureSaleVolume(rewardNode.getStructureSaleVolume());
                reward.setRank(rewardNode.getRank());
                reward.setPeriodId(period.getObjectId());
                reward.setRewardStatus(rewardStatus);

                reward.setTotal(total);

                reward.setPoint(ZERO);

                if (rewardStatus == RewardStatus.ESTIMATED) {
                    reward.setPoint(total);
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calculateRewardPoint(sale, RewardType.MANAGER_MYCOOK, total, period));
                }

                updateLocal(sale, reward, period);

                if (reward.getPoint().compareTo(ZERO) != 0 && (rewardStatus == RewardStatus.CHARGED ||
                        getRewardsPointSumBefore(sale.getObjectId(), RewardType.MANAGER_MYCOOK, rewardStatus, period).compareTo(ZERO) == 0)) {
                    if (test) {
                        rewards.add(reward);
                    } else {
                        domainService.save(reward);
                    }
                }
            }
        } else if (rewardStatus == RewardStatus.CHARGED) {
            Reward r = getRewardsBySaleId(sale.getObjectId()).stream()
                    .filter(r1 -> r1.getRewardStatus().equals(RewardStatus.ESTIMATED))
                    .filter(r1 -> r1.getType().equals(RewardType.MANAGER_MYCOOK))
                    .findFirst().orElse(null);

            if (r != null) {
                BigDecimal point = calculateRewardPoint(sale, RewardType.MANAGER_MYCOOK, r.getTotal(), period);

                if (point.compareTo(ZERO) != 0) {
                    Reward reward = new Reward(r, period);

                    reward.setType(RewardType.MANAGER_MYCOOK);
                    reward.setRewardStatus(RewardStatus.CHARGED);
                    reward.setPoint(point);

                    updateLocal(sale, reward, period);

                    reward.setEstimatedId(reward.getObjectId());

                    if (test) {
                        rewards.add(reward);
                    } else {
                        domainService.save(reward);
                    }
                }
            }
        }
    }

    private void calculatePersonalReward(RewardNode rewardNode, Period period, Long rewardStatus) {
        if (rewardNode.getPaymentVolume().compareTo(getParameter(43L, period)) >= 0) {
            Reward reward = new Reward();

            reward.setType(RewardType.PERSONAL_VOLUME);
            reward.setWorkerId(rewardNode.getWorkerNode().getWorkerId());
            reward.setSaleVolume(rewardNode.getSaleVolume());
            reward.setPaymentVolume(rewardNode.getPaymentVolume());
            reward.setDate(Dates.currentDate());
            reward.setMonth(period.getOperatingMonth());
            reward.setPeriodId(period.getObjectId());
            reward.setRewardStatus(rewardStatus);

            BigDecimal avgPoint = getParameter(44L, period);
            BigDecimal lowerPoint = getParameter(45L, period);
            BigDecimal greaterPoint = getParameter(46L, period);

            if (rewardNode.getPaymentVolume().compareTo(avgPoint) < 0) {
                reward.setTotal(lowerPoint);
            }else {
                reward.setTotal(greaterPoint);
            }

            reward.setPoint(ZERO);

            if (rewardNode.getPaymentVolume().compareTo(avgPoint) < 0) {
                reward.setPoint(lowerPoint);
            } else {
                reward.setPoint(greaterPoint);
            }

            updateLocalByWorker(reward, period);

            if (reward.getPoint().compareTo(ZERO) != 0) {
                if (test) {
                    rewards.add(reward);
                } else {
                    domainService.save(reward);
                }
            }
        }
    }

    private void calculateGroupReward(RewardNode rewardNode, Period period, Long rewardStatus) {
        BigDecimal sum = getGroupVolumePercent(rewardNode.getRank(), period)
                .multiply(rewardNode.getGroupSaleVolume())
                .divide(new BigDecimal("100"), 5, HALF_EVEN);

        List<Sale> groupSales = rewardNode.getGroupSales();

        for (int i = 0; i < groupSales.size(); i++) {
            Sale sale = groupSales.get(i);

            BigDecimal total = getGroupVolumePercent(rewardNode.getRank(), period)
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
                reward.setWorkerId(rewardNode.getWorkerId());
                reward.setType(RewardType.GROUP_VOLUME);
                reward.setGroupSaleVolume(rewardNode.getGroupSaleVolume());
                reward.setGroupPaymentVolume(rewardNode.getGroupPaymentVolume());
                reward.setRank(rewardNode.getRank());
                reward.setDate(Dates.currentDate());
                reward.setMonth(period.getOperatingMonth());
                reward.setRewardStatus(rewardStatus);
                reward.setPeriodId(period.getObjectId());

                reward.setTotal(total);

                reward.setPoint(ZERO);

                if (rewardStatus == RewardStatus.ESTIMATED) {
                    reward.setPoint(total);
                } else if (rewardStatus == RewardStatus.CHARGED) {
                    reward.setPoint(calculateRewardPoint(sale, RewardType.GROUP_VOLUME, total, period));
                }

                updateLocalBySale(reward, period);

                if (reward.getPoint().compareTo(ZERO) != 0) {
                    if (test) {
                        rewards.add(reward);
                    } else {
                        domainService.save(reward);
                    }
                }
            }
        }

        if (rewardStatus == RewardStatus.CHARGED) {
            saleService.getSalesBeforeOrEqual(rewardNode.getWorkerId(), period.getObjectId())
                    .forEach(sale -> getRewardsBySaleId(sale.getObjectId()).stream()
                            .filter(r -> r.getType() == RewardType.GROUP_VOLUME)
                            .filter(r -> r.getRewardStatus() == RewardStatus.ESTIMATED)
                            .filter(r -> !Objects.equals(r.getPeriodId(), period.getObjectId()))
                            .forEach(r -> {
                                BigDecimal point = calculateRewardPoint(sale, RewardType.GROUP_VOLUME, r.getTotal(), period);

                                if (point.compareTo(ZERO) != 0) {
                                    Reward reward = new Reward(r, period);

                                    reward.setType(RewardType.GROUP_VOLUME);
                                    reward.setRewardStatus(RewardStatus.CHARGED);
                                    reward.setPoint(point);

                                    updateLocalBySale(reward, period);

                                    reward.setEstimatedId(reward.getObjectId());

                                    if (test) {
                                        rewards.add(reward);
                                    } else {
                                        domainService.save(reward);
                                    }
                                }
                            }));
        }
    }

    private void calculateStructureReward(RewardNode rewardNode, Period period, Long rewardStatus) {
        BigDecimal sum = ZERO;

        for (RewardNode m : rewardNode.getFirstStructureManagers()) {
            BigDecimal total = getGroupVolumePercent(rewardNode.getRank(), period)
                    .subtract(getGroupVolumePercent(m.getRank(), period))
                    .multiply(m.getStructureSaleVolume())
                    .divide(new BigDecimal("100"), 5, HALF_EVEN);

            sum = sum.add(total);
        }

        List<RewardNode> firstStructureManagers = rewardNode.getFirstStructureManagers();

        for (int i = 0; i < firstStructureManagers.size(); i++) {
            RewardNode m = firstStructureManagers.get(i);

            List<Sale> structureSales = m.getStructureSales();

            for (int j = 0; j < structureSales.size(); j++) {
                Sale sale = structureSales.get(j);

                BigDecimal total = getGroupVolumePercent(rewardNode.getRank(), period)
                        .subtract(getGroupVolumePercent(m.getRank(), period))
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
                    reward.setWorkerId(rewardNode.getWorkerId());
                    reward.setType(RewardType.STRUCTURE_VOLUME);
                    reward.setRank(rewardNode.getRank());
                    reward.setManagerId(m.getWorkerId());
                    reward.setManagerRank(m.getRank());
                    reward.setStructureSaleVolume(rewardNode.getStructureSaleVolume());
                    reward.setStructurePaymentVolume(rewardNode.getStructurePaymentVolume());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(period.getOperatingMonth());
                    reward.setPeriodId(period.getObjectId());
                    reward.setRewardStatus(rewardStatus);

                    reward.setTotal(total);

                    reward.setPoint(ZERO);

                    if (rewardStatus == RewardStatus.ESTIMATED) {
                        reward.setPoint(total);
                    } else if (rewardStatus == RewardStatus.CHARGED) {
                        reward.setPoint(calculateRewardPoint(sale, RewardType.STRUCTURE_VOLUME, total, reward.getManagerId(), period));
                    }

                    updateLocalBySale(reward, period);

                    if (reward.getPoint().compareTo(ZERO) != 0) {
                        if (test) {
                            rewards.add(reward);
                        } else {
                            domainService.save(reward);
                        }
                    }
                }
            }
        }

        if (rewardStatus == RewardStatus.CHARGED) {
            saleService.getSalesBeforeOrEqual(rewardNode.getWorkerId(), period.getObjectId())
                    .forEach(sale -> getRewardsBySaleId(sale.getObjectId()).stream()
                            .filter(r -> r.getType() == RewardType.STRUCTURE_VOLUME)
                            .filter(r -> r.getRewardStatus() == RewardStatus.ESTIMATED)
                            .filter(r -> !Objects.equals(r.getPeriodId(), period.getObjectId()))
                            .forEach(r -> {
                                BigDecimal point = calculateRewardPoint(sale, RewardType.STRUCTURE_VOLUME, r.getTotal(), r.getManagerId(), period);

                                if (point.compareTo(ZERO) != 0) {
                                    Reward reward = new Reward(r, period);

                                    reward.setType(RewardType.STRUCTURE_VOLUME);
                                    reward.setRewardStatus(RewardStatus.CHARGED);
                                    reward.setPoint(point);

                                    updateLocalBySale(reward, period);

                                    reward.setEstimatedId(reward.getObjectId());

                                    if (test) {
                                        rewards.add(reward);
                                    } else {
                                        domainService.save(reward);
                                    }
                                }
                            }));
        }
    }

    private transient LoadingCache<Pair<Long, Date>, BigDecimal> parameterCache;

    private LoadingCache<Pair<Long, Date>, BigDecimal> getParameterCache() {
        if (parameterCache == null) {
            parameterCache = CacheBuilder.newBuilder()
                    .build(CacheLoader.from(pair ->
                            rewardParameterMapper.getRewardParameterValue(pair.getLeft(), pair.getRight())));
        }

        return parameterCache;
    }

    public BigDecimal getParameter(Long rewardParameterId, Period period){
        try {
            return getParameterCache().get(Pair.of(rewardParameterId, period.getOperatingMonth()));
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public RewardNode getWorkerReward(Worker worker){
        return getRewardTreeCache(periodMapper.getActualPeriod().getObjectId()).getRewardNode(worker.getObjectId());
    }

    private BigDecimal getPaymentsRate(Long countryId, Long periodId) {
        Period period = periodMapper.getPeriod(periodId);

        Map<Long, BigDecimal> pointSumMap = new HashMap<>();
        Map<Long, BigDecimal> amountSumMap = new HashMap<>();

        paymentService.getPayments(FilterWrapper.of(new Payment().setPeriodId(period.getObjectId())))
                .forEach(p -> {
                    Long saleCountryId = saleService.getCountryId(p.getSaleId());

                    if (countryId.equals(saleCountryId)) {
                        pointSumMap.put(countryId, pointSumMap.getOrDefault(countryId, ZERO).add(p.getPoint()));
                        amountSumMap.put(countryId, amountSumMap.getOrDefault(countryId, ZERO).add(p.getAmount()));
                    }
                });

        List<BigDecimal> rates = new ArrayList<>();

        Date date = Dates.lastDayOfMonth(period.getOperatingMonth());

        amountSumMap.forEach((c, a) -> {
            if (a.compareTo(ZERO) != 0) {
                BigDecimal rate = a.divide(pointSumMap.get(c), 5, HALF_EVEN);

                if (!c.equals(countryId)) {
                    rate = rate
                            .multiply(exchangeRateService.getExchangeRate(countryId, date)
                                    .divide(exchangeRateService.getExchangeRate(c, date), 5, HALF_EVEN));
                }

                rates.add(rate);
            }
        });

        return rates.size() > 0 ? rates.stream().reduce(ZERO, BigDecimal::add).divide(new BigDecimal(rates.size()), 5, HALF_EVEN) : ONE;
    }


    public static class CountryPeriodId {
        public final Long countryId;
        public final Long periodId;

        public CountryPeriodId(Long countryId, Long periodId) {
            this.countryId = countryId;
            this.periodId = periodId;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof RewardService.CountryPeriodId) {
                return Objects.equals(countryId, ((RewardService.CountryPeriodId) o).countryId) &&
                        Objects.equals(periodId, ((RewardService.CountryPeriodId) o).periodId);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(countryId, periodId);
        }
    }

    private transient LoadingCache<RewardService.CountryPeriodId, BigDecimal> paymentRateCache;

    private LoadingCache<RewardService.CountryPeriodId, BigDecimal> getPaymentRateCache() {
        if (paymentRateCache == null ){
            paymentRateCache = CacheBuilder.newBuilder()
                    .expireAfterAccess(5, TimeUnit.MINUTES)
                    .build(CacheLoader.from(countryPeriodId ->
                            getPaymentsRate(countryPeriodId.countryId, countryPeriodId.periodId)));
        }

        return paymentRateCache;
    }

    private BigDecimal getPaymentsRateByWorkerId(Long workerId, Long periodId) {
        try {
            return getPaymentRateCache().get(new RewardService.CountryPeriodId(workerService.getCountryId(workerId), periodId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal getPaymentsRateBySaleId(Long saleId, Long periodId) {
        try {
            return getPaymentRateCache().get(new RewardService.CountryPeriodId(saleService.getCountryId(saleId), periodId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards(Period period) throws RewardException {
        try {
            if (!test) {
                rewardMapper.deleteRewards(period.getObjectId());
            }

            rewards.clear();

            getRewardsBySaleIdCache().invalidateAll();

            getRewardsByPeriodWorkerIdCache().invalidateAll();

            getPaymentRateCache().invalidateAll();

            getParameterCache().invalidateAll();

            RewardTree tree = getRewardTree(period);

            saleService.getSales().stream()
                    .filter(sale -> sale.getPeriodId() <= period.getObjectId())
                    .forEach(sale -> {
                        calculateSaleReward(sale, period, RewardStatus.ESTIMATED);
                        calculateSaleReward(sale, period, RewardStatus.CHARGED);

                        if (sale.isFeeWithdraw()) {
                            calculateSaleReward(sale, period, RewardStatus.WITHDRAWN);
                        }

                        calculateBonusReward(sale, period, RewardStatus.ESTIMATED);
                        calculateBonusReward(sale, period, RewardStatus.CHARGED);

                        calculateCulinaryReward(sale, period, RewardStatus.ESTIMATED);
                        calculateCulinaryReward(sale, period, RewardStatus.CHARGED);

                        RewardNode rewardNode = tree.getRewardNode(sale.getSellerWorkerId());

                        if (!sale.isSasRequest()) {
                            calculateManagerReward(sale, rewardNode, period, RewardStatus.ESTIMATED);
                            calculateManagerReward(sale, rewardNode, period, RewardStatus.CHARGED);
                        }
                    });

            tree.forEachLevel((l, rl) -> rl.forEach(rewardNode -> {
                if (rewardNode.getRank() > 0) {
                    Reward reward = new Reward();

                    reward.setWorkerId(rewardNode.getWorkerId());
                    reward.setType(RewardType.RANK);
                    reward.setSaleVolume(rewardNode.getSaleVolume());
                    reward.setPaymentVolume(rewardNode.getPaymentVolume());
                    reward.setGroupSaleVolume(rewardNode.getGroupSaleVolume());
                    reward.setGroupPaymentVolume(rewardNode.getGroupPaymentVolume());
                    reward.setStructureSaleVolume(rewardNode.getStructureSaleVolume());
                    reward.setStructurePaymentVolume(rewardNode.getStructurePaymentVolume());
                    reward.setRank(rewardNode.getRank());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(period.getOperatingMonth());
                    reward.setPeriodId(period.getObjectId());
                    reward.setRewardStatus(RewardStatus.ESTIMATED);

                    if (test) {
                        rewards.add(reward);
                    } else {
                        domainService.save(reward);
                    }
                }
            }));

            tree.forEachLevel((l, rl) -> rl.forEach(rewardNode -> {
                calculatePersonalReward(rewardNode, period, RewardStatus.ESTIMATED);
                calculatePersonalReward(rewardNode, period, RewardStatus.CHARGED);

                calculateGroupReward(rewardNode, period, RewardStatus.ESTIMATED);
                calculateGroupReward(rewardNode, period, RewardStatus.CHARGED);

                calculateStructureReward(rewardNode, period, RewardStatus.ESTIMATED);
                calculateStructureReward(rewardNode, period, RewardStatus.CHARGED);
            }));

            if (rewardTreeCache != null) {
                rewardTreeCache.refresh(period.getObjectId());
            }
        } catch (Exception e) {
            log.error("error calculate rewards", e);

            throw new RewardException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards() throws RewardException {
        calculateRewards(periodMapper.getActualPeriod());
    }

    public void testRewards(Period period) throws RewardException {
        test = true;

        calculateRewards(period);

        test = false;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }
}

