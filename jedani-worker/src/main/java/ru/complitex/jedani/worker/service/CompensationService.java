package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.*;
import ru.complitex.jedani.worker.service.cache.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static ru.complitex.jedani.worker.entity.RewardStatus.*;
import static ru.complitex.jedani.worker.entity.RewardType.*;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class CompensationService {
    private final static Logger log = LoggerFactory.getLogger(CompensationService.class);

    private final static BigDecimal BD_100 = new BigDecimal(100);

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private PaymentMapper paymentMapper;

    @Inject
    private WorkerNodeMapper workerNodeMapper;

    @Inject
    private RewardMapper rewardMapper;

    @Inject
    private RewardNodeMapper rewardNodeMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    @Inject
    private SaleService saleService;

    @Inject
    private PriceService priceService;

    @Inject
    private ExchangeRateService exchangeRateService;

    @Inject
    private WorkerNodeService workerNodeService;

    @Inject
    private RewardTreeService rewardTreeService;

    @Inject
    private SaleCacheService saleCacheService;

    @Inject
    private PaymentCacheService paymentCacheService;

    @Inject
    private ParameterCacheService parameterCacheService;

    @Inject
    private RewardTreeCacheService rewardTreeCacheService;

    @Inject
    private RewardCacheService rewardCacheService;

    private boolean test = false;

    private List<Reward> rewards = new ArrayList<>();

    private BigDecimal getRate(Sale sale, SaleItem saleItem) {
        if (saleItem.getSaleDecisionId() != null) {
            return priceService.getRate(sale, saleItem, Dates.currentDate());
        }

        if (saleItem.getRate() != null) {
            return saleItem.getRate();
        }

        return priceService.getRate(sale.getStorageId(), Dates.currentDate());
    }

    private BigDecimal getPrice(Sale sale, SaleItem saleItem) {
        return sale.isFeeWithdraw()
                ? saleItem.getPrice().add(sale.getPersonalRewardPoint())
                : saleItem.getPrice();
    }

    private BigDecimal getRatio(Sale sale) {
        Date month =  periodMapper.getOperationMonth(sale.getPeriodId());

        List<Ratio> ratios = domainService.getDomains(Ratio.class, FilterWrapper.of((Ratio) new Ratio()
                .setCountryId(saleService.getCountryId(sale))
                .setBegin(month)
                .setEnd(month)
                .setFilter(Ratio.BEGIN, Attribute.FILTER_BEFORE_OR_EQUAL_DATE)
                .setFilter(Ratio.END, Attribute.FILTER_AFTER_DATE)));

        return !ratios.isEmpty() ? ratios.get(0).getValue() : ZERO;
    }

    private BigDecimal getDiscount(Sale sale, SaleItem saleItem) {
        return getPrice(sale, saleItem)
                .multiply(BD_100.subtract(getRatio(sale)))
                .divide(saleItem.getBasePrice().multiply(BD_100), 7, HALF_EVEN);
    }

    private BigDecimal getCrossRate(Sale sale) {
        Long workerCountryId = workerService.getCountryId(sale.getSellerWorkerId());
        Long saleCountryId = saleService.getCountryId(sale);

        if (!workerCountryId.equals(saleCountryId)) {
            Date crossDate = Dates.lastDayOfMonth(periodMapper.getOperationMonth(sale.getPeriodId()));

            return exchangeRateService.getExchangeRate(workerCountryId, crossDate)
                    .divide(exchangeRateService.getExchangeRate(saleCountryId, crossDate), 7, HALF_EVEN);
        }

        return ONE;
    }

    private void updateAmount(Reward reward) {
        if (reward.getPoint().compareTo(ZERO) >= 0) {
            reward.setAmount(reward.getPoint().multiply(reward.getRate())
                    .multiply(reward.getCrossRate() != null ? reward.getCrossRate() : ONE)
                    .multiply(reward.getDiscount() != null ? reward.getDiscount() : ONE)
                    .setScale(5, HALF_EVEN));
        } else {
            throw new RuntimeException("negative reward point error " + reward);
        }
    }

    private BigDecimal getPaymentRate(Long countryId, Period period) {
        return paymentCacheService.getPaymentRateByCountryId(countryId, period.getObjectId());
    }

    private Reward getReward(Long typeId, Long workerId, BigDecimal point, Sale sale, SaleItem saleItem, Period period, Long countryId) {
        Reward reward = new Reward();

        reward.setType(typeId);
        reward.setWorkerId(workerId);

        if (sale != null) {
            reward.setSaleId(sale.getObjectId());
            reward.setSaleTotal(sale.getTotal());

            if (saleItem != null) {
                reward.setBasePrice(saleItem.getBasePrice());
                reward.setPrice(getPrice(sale, saleItem));
                reward.setRate(getRate(sale, saleItem));
                reward.setCrossRate(getCrossRate(sale));
                reward.setDiscount(getDiscount(sale, saleItem));
            }
        }

        if (countryId != null) {
            reward.setRate(getPaymentRate(countryId, period));
        }

        reward.setPoint(point);
        reward.setTotal(point);

        updateAmount(reward);

        reward.setDate(Dates.currentDate());
        reward.setMonth(period.getOperatingMonth());
        reward.setPeriodId(period.getObjectId());

        return reward;
    }

    private Reward getReward(Long typeId, Long workerId, BigDecimal point, Sale sale, SaleItem saleItem, Period period) {
        return getReward(typeId, workerId, point, sale, saleItem, period, null);
    }

    public BigDecimal getPersonalRewardPoint(Sale sale, List<SaleItem> saleItems){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            boolean mkPremium = saleService.isMycookPremiumSaleItems(saleItems);
            boolean mkTouch = saleService.isMycookTouchSaleItems(saleItems);

            if (sale.isSasRequest()) {
                point = getParameter(1L);
            }else if (w.getMkStatus() == MkStatus.STATUS_PROMO) {
                if (mkPremium) {
                    point = getParameter(2L);
                }else if (mkTouch) {
                    point = getParameter(3L);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_JUST){
                if (mkPremium) {
                    point = getParameter(4L);
                }else if (mkTouch) {
                    point = getParameter(5L);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_VIP) {
                if (sale.getManagerBonusWorkerId() != null) {
                    if (mkPremium) {
                        point = getParameter(6L).subtract(getParameter(9L));
                    }else if (mkTouch) {
                        point = getParameter(7L).subtract(getParameter(10L));
                    }
                }else if (mkPremium) {
                    point = getParameter(6L);
                }else if (mkTouch) {
                    point = getParameter(7L);
                }
            }
        }else if (sale.getType() == SaleType.RANGE && sale.getTotal() != null) {
            point = sale.getTotal().multiply(getParameter(8L));
        }

        return point;
    }

    public Reward getPersonalMycookReward(Sale sale, SaleItem saleItem, Period period) {
        if (sale.getPersonalRewardPoint() != null && sale.getPersonalRewardPoint().compareTo(ZERO) > 0) {
            return getReward(PERSONAL_MYCOOK, sale.getSellerWorkerId(), sale.getPersonalRewardPoint(), sale, saleItem, period);
        }

        return null;
    }

    public BigDecimal getManagerBonusRewardPoint(Sale sale, List<SaleItem> saleItems){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            if (saleService.isMycookPremiumSaleItems(saleItems)){
                point = getParameter(9L);
            }else if (saleService.isMycookTouchSaleItems(saleItems)){
                point = getParameter(10L);
            }
        }

        return point;
    }

    private Reward getManagerBonusReward(Sale sale, SaleItem saleItem, Period period) {
        if (sale.getManagerBonusRewardPoint() != null && sale.getManagerBonusRewardPoint().compareTo(ZERO) > 0 && sale.getManagerBonusWorkerId() != null) {
            return getReward(MANAGER_BONUS, sale.getManagerBonusWorkerId(), sale.getManagerBonusRewardPoint(), sale, saleItem, period);
        }

        return null;
    }

    public BigDecimal getCulinaryRewardPoint(Sale sale, List<SaleItem> saleItems){
        if (saleService.isMycookSaleItems(saleItems)){
            if (!sale.isSasRequest()) {
                return getParameter(41L);
            } else {
                return getParameter(42L);
            }
        }

        return ZERO;
    }

    public Reward getCulinaryReward(Sale sale, SaleItem saleItem, Period period) {
        if (sale.getCulinaryRewardPoint() != null && sale.getCulinaryRewardPoint().compareTo(ZERO) > 0 && sale.getCulinaryWorkerId() != null) {
            return getReward(CULINARY_WORKSHOP, sale.getCulinaryWorkerId(), sale.getCulinaryRewardPoint(), sale, saleItem, period);
        }

        return null;
    }

    private BigDecimal getParameter(Long rewardParameterId) {
        return parameterCacheService.getParameter(rewardParameterId);
    }

    private BigDecimal getManagerPremiumPoint(Long rank) {
        switch (rank.intValue()) {
            case (int) RankType.MANAGER_ASSISTANT:
                return getParameter(48L);
            case (int) RankType.MANAGER_JUNIOR:
                return getParameter(21L);
            case (int) RankType.TEAM_MANAGER:
                return getParameter(22L);
            case (int) RankType.SENIOR_ASSISTANT:
                return getParameter(23L);
            case (int) RankType.SENIOR_MANAGER:
                return getParameter(24L);
            case (int) RankType.DIVISION_MANAGER:
                return getParameter(25L);
            case (int) RankType.AREA_MANAGER:
                return getParameter(26L);
            case (int) RankType.REGIONAL_MANAGER:
                return getParameter(27L);
            case (int) RankType.SILVER_DIRECTOR:
                return getParameter(28L);
            case (int) RankType.GOLD_DIRECTOR:
                return getParameter(29L);
            case (int) RankType.PLATINUM_DIRECTOR:
                return getParameter(30L);
            default:
                return ZERO;
        }
    }

    private Reward getManagerPremiumReward(RewardNode rewardNode, Sale sale, SaleItem saleItem, Period period) {
        Long rank = Objects.equals(sale.getPeriodId(), period.getObjectId())
                ? rewardNode.getRank()
                : rewardTreeCacheService.getRewardNode(sale.getPeriodId(), rewardNode.getWorkerId()).getRank();

        BigDecimal point = getManagerPremiumPoint(rank);

        if (point.compareTo(ZERO) > 0) {
            Reward reward = getReward(MANAGER_PREMIUM, sale.getSellerWorkerId(), point, sale, saleItem, period);

            reward.setRank(rank);

            reward.setStructureSaleVolume(Objects.equals(sale.getPeriodId(), period.getObjectId())
                    ? rewardNode.getStructureSaleVolume()
                    : rewardTreeCacheService.getRewardNode(sale.getPeriodId(), rewardNode.getWorkerId()).getStructureSaleVolume());

            return reward;
        }

        return null;
    }

    private Reward getPersonalVolumeReward(RewardNode rewardNode, Period period) {
        BigDecimal minPoint = getParameter(43L);
        BigDecimal avgPoint = getParameter(44L);
        BigDecimal lowerPoint = getParameter(45L);
        BigDecimal greaterPoint = getParameter(46L);

        if (minPoint.compareTo(rewardNode.getPaymentVolume()) <= 0) {
            BigDecimal point = avgPoint.compareTo(rewardNode.getPaymentVolume()) > 0 ? lowerPoint : greaterPoint;

            if (point.compareTo(ZERO) > 0) {
                Long countryId = workerService.getCountryId(rewardNode.getWorkerId());

                Reward reward = getReward(PERSONAL_VOLUME, rewardNode.getWorkerId(), point, null, null, period, countryId);

                reward.setSaleVolume(rewardNode.getSaleVolume());

                reward.setPaymentVolume(rewardNode.getPaymentVolume());

                return reward;
            }
        }

        return null;
    }

    private Reward getRankReward(RewardNode rewardNode, Period period) {
        if (rewardNode.getRank() > 0) {
            Reward reward = new Reward();

            reward.setType(RewardType.RANK);
            reward.setWorkerId(rewardNode.getWorkerId());

            reward.setRank(rewardNode.getRank());
            reward.setSaleVolume(rewardNode.getSaleVolume());
            reward.setPaymentVolume(rewardNode.getPaymentVolume());
            reward.setGroupSaleVolume(rewardNode.getGroupSaleVolume());
            reward.setGroupPaymentVolume(rewardNode.getGroupPaymentVolume());
            reward.setStructureSaleVolume(rewardNode.getStructureSaleVolume());
            reward.setStructurePaymentVolume(rewardNode.getStructurePaymentVolume());

            reward.setDate(Dates.currentDate());
            reward.setMonth(period.getOperatingMonth());
            reward.setPeriodId(period.getObjectId());

            return reward;
        }

        return null;
    }

    private BigDecimal getGroupVolumePercent(Long rank) {
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
            default:
                return ZERO;
        }
    }

    private List<Reward> getGroupVolumeRewards(RewardNode rewardNode, Period period) {
        if (rewardNode.getRank() > 0) {
            List<Reward> rewards = new ArrayList<>();

            BigDecimal percent = getGroupVolumePercent(rewardNode.getRank());

            BigDecimal sum = percent.multiply(rewardNode.getGroupSaleVolume()).divide(BD_100, 5, HALF_EVEN);

            List<Sale> groupSales = rewardNode.getGroupSales().stream()
                    .filter(sale -> Objects.equals(sale.getPeriodId(), period.getObjectId()))
                    .filter(sale -> !Objects.equals(sale.getSaleStatus(), SaleStatus.CREATED))
                    .collect(Collectors.toList());

            for (int i = 0; i < groupSales.size(); i++) {
                Sale sale = groupSales.get(i);

                BigDecimal point = percent.multiply(sale.getTotal()).divide(BD_100, 5, HALF_EVEN);

                sum = sum.subtract(point);

                if (i == groupSales.size() - 1) {
                    if (sum.compareTo(ZERO) != 0) {
                        log.warn("getGroupVolumeRewards sum " + sale + " " + sum);
                    }

                    point = point.add(sum);
                }

                if (point.compareTo(ZERO) > 0) {
                    Long countryId = saleService.getCountryId(sale.getObjectId());

                    Reward reward = getReward(GROUP_VOLUME, rewardNode.getWorkerId(), point, sale, null, period, countryId);

                    reward.setRank(rewardNode.getRank());
                    reward.setGroupSaleVolume(rewardNode.getGroupSaleVolume());
                    reward.setGroupPaymentVolume(rewardNode.getGroupPaymentVolume());

                    rewards.add(reward);
                }
            }

            return rewards;
        }

        return Collections.emptyList();
    }

    private List<Reward> getStructureVolumeRewards(RewardNode rewardNode, Period period) {
        List<Reward> rewards = new ArrayList<>();

        BigDecimal sum = ZERO;

        for (RewardNode m : rewardNode.getFirstStructureManagers()) {
            BigDecimal total = getGroupVolumePercent(rewardNode.getRank())
                    .subtract(getGroupVolumePercent(m.getRank()))
                    .multiply(m.getStructureSaleVolume())
                    .divide(BD_100, 5, HALF_EVEN);

            sum = sum.add(total);
        }

        List<RewardNode> firstStructureManagers = rewardNode.getFirstStructureManagers();

        for (int i = 0; i < firstStructureManagers.size(); i++) {
            RewardNode m = firstStructureManagers.get(i);

            List<Sale> structureSales = m.getStructureSales().stream()
                    .filter(sale -> Objects.equals(sale.getPeriodId(), period.getObjectId()))
                    .filter(sale -> !Objects.equals(sale.getSaleStatus(), SaleStatus.CREATED))
                    .collect(Collectors.toList());

            for (int j = 0; j < structureSales.size(); j++) {
                Sale sale = structureSales.get(j);

                BigDecimal point = getGroupVolumePercent(rewardNode.getRank())
                        .subtract(getGroupVolumePercent(m.getRank()))
                        .multiply(sale.getTotal())
                        .divide(BD_100, 5, HALF_EVEN);

                sum = sum.subtract(point);

                if (i == firstStructureManagers.size() - 1 && j == structureSales.size() - 1) {
                    if (sum.compareTo(ZERO) != 0) {
                        log.warn("getStructureVolumeRewards sum " + m + " " + sale + " " + sum);
                    }

                    point = point.add(sum);
                }

                if (point.compareTo(ZERO) > 0) {
                    Long countryId = saleService.getCountryId(sale.getObjectId());

                    Reward reward = getReward(STRUCTURE_VOLUME, rewardNode.getWorkerId(), point, sale, null, period, countryId);

                    reward.setRank(rewardNode.getRank());
                    reward.setManagerId(m.getWorkerId());
                    reward.setManagerRank(m.getRank());
                    reward.setStructureSaleVolume(rewardNode.getStructureSaleVolume());
                    reward.setStructurePaymentVolume(rewardNode.getStructurePaymentVolume());

                    rewards.add(reward);
                }
            }
        }

        return rewards;
    }

    public BigDecimal getPaymentsPointSum(Long saleId) {
        BigDecimal sum = paymentMapper.getPaymentsPointSum(saleId);

        return sum != null ? sum : ZERO;
    }

    public BigDecimal getRewardsPointSumBefore(Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId, Long periodId) {
        BigDecimal sum = rewardMapper.getRewardsPointSumBefore(rewardTypeId, saleId, managerId, rewardStatusId, periodId);

        return sum != null ? sum : ZERO;
    }

    public void updateRewardPoint(Long rewardType, Reward reward) {
        if (reward.getSaleId() != null) {
            BigDecimal saleTotal = reward.getSaleTotal();

            if (saleTotal == null) {
                saleTotal = domainService.getDecimal(Sale.ENTITY_NAME, reward.getSaleId(), Sale.TOTAL);
            }

            if (saleTotal != null && saleTotal.compareTo(ZERO) > 0) {
                BigDecimal paid = getPaymentsPointSum(reward.getSaleId()).divide(saleTotal, 5, HALF_EVEN);

                BigDecimal point = ZERO;

                if (paid.compareTo(new BigDecimal("0.2")) >= 0) {
                    point = reward.getPoint().multiply(new BigDecimal("0.25"));
                }

                if (paid.compareTo(new BigDecimal("0.7")) >= 0) {
                    point = point.add(reward.getPoint().multiply(new BigDecimal("0.35")));
                }

                if (paid.compareTo(new BigDecimal("1")) >= 0) {
                    point = point.add(reward.getPoint().multiply(new BigDecimal("0.40")));
                }

                BigDecimal sum = getRewardsPointSumBefore(rewardType, reward.getSaleId(), reward.getManagerId(), CHARGED, reward.getPeriodId());

                reward.setPoint(point.subtract(sum));
            } else {
                reward.setPoint(ZERO);
            }
        }
    }

    private void save(Reward reward) {
        if (test) {
            Reward r = new Reward();

            r.copy(reward, true);

            if (r.getRewardStatus().equals(CHARGED)) {
                r.setEstimatedId(reward.getObjectId());
            }

            rewards.add(r);
        } else {
            reward.setObjectId(null);
            reward.setStartDate(Dates.currentDate());

            domainService.save(reward);
        }
    }

    private boolean isEstimated(Reward reward) {
        return getRewardsPointSumBefore(reward.getType(), reward.getSaleId(), reward.getManagerId(), ESTIMATED,  reward.getPeriodId())
                .compareTo(ZERO) != 0;
    }

    private boolean isCharged(Reward reward) {
        return getRewardsPointSumBefore(reward.getType(), reward.getSaleId(), reward.getManagerId(), ESTIMATED, reward.getPeriodId())
                .compareTo(ZERO) != 0;
    }

    private boolean isEstimatedAndCharged(Reward reward, Period period) {
        Long periodId = period != null ? period.getObjectId() : reward.getPeriodId();

        return getRewardsPointSumBefore(reward.getType(), reward.getSaleId(), reward.getManagerId(), ESTIMATED, periodId)
                .compareTo(getRewardsPointSumBefore(reward.getType(), reward.getSaleId(), reward.getManagerId(), CHARGED, periodId)) == 0;
    }

    private boolean isWithdraw(Reward reward) {
        return getRewardsPointSumBefore(reward.getType(), reward.getSaleId(), reward.getManagerId(), WITHDRAWN,
                reward.getPeriodId()).compareTo(ZERO) != 0;
    }

    private void estimateReward(Reward reward) {
        if (reward != null && !isEstimated(reward) && !isCharged(reward)) {
            reward.setRewardStatus(ESTIMATED);

            save(reward);
        }
    }

    private void chargeReward(Reward reward) {
        if (reward != null) {
            updateRewardPoint(reward.getType(), reward);

            if (reward.getPoint() != null && reward.getPoint().compareTo(ZERO) != 0) {
                reward.setRewardStatus(CHARGED);

                save(reward);
            }
        }
    }

    public void withdrawReward(Reward reward) {
        if (reward != null && !isWithdraw(reward)) {
            reward.setRewardStatus(WITHDRAWN);

            save(reward);
        }
    }

    public void calculateReward(Reward reward) {
        estimateReward(reward);

        chargeReward(reward);
    }

    private List<Reward> getEstimatedRewards(Period period) {
        return rewardMapper.getRewards(FilterWrapper.of(new Reward().setRewardStatus(ESTIMATED))).stream()
                .filter(reward -> reward.getPoint() != null)
                .filter(reward -> periodMapper.getOperationMonth(reward.getPeriodId()).before(period.getOperatingMonth()))
                .filter(reward -> !isEstimatedAndCharged(reward, period))
                .collect(Collectors.toList());
    }

    @Transactional
    public void calculateRewards(Period period) {
        Long periodId = period.getObjectId();

        if (!test) {
            rewardMapper.deleteRewards(periodId);

            rewardNodeMapper.deleteRewardNodes(periodId);

            workerNodeMapper.deleteWorkerNodes(periodId);

            repair();
        }

        rewards.clear();

        clearCache();

        RewardTree rewardTree = new RewardTree(workerNodeService.getWorkerNodeMap());

        rewardTreeService.updateRewardTree(rewardTree, period, rewardNode -> {
            rewardNode.getSales().stream()
                    .filter(sale -> !test || sale.getPeriodId() <= periodId)
                    .filter(sale -> !Objects.equals(sale.getSaleStatus(), SaleStatus.CREATED))
                    .forEach(sale -> {
                            if (Objects.equals(sale.getPeriodId(), periodId)) {
                                SaleItem saleItem = saleService.getSaleItem(sale.getObjectId());

                                Reward personalMycookReward = getPersonalMycookReward(sale, saleItem, period);

                                calculateReward(personalMycookReward);

                                if (sale.isFeeWithdraw()) {
                                    withdrawReward(personalMycookReward);
                                }

                                if (!sale.isSasRequest()) {
                                    calculateReward(getManagerPremiumReward(rewardNode, sale, saleItem, period));
                                }

                                calculateReward(getManagerBonusReward(sale, saleItem, period));

                                calculateReward(getCulinaryReward(sale, saleItem, period));
                            } else {
                                calculateReward(getCulinaryReward(sale, saleService.getSaleItem(sale.getObjectId()), period));
                            }
                    });

            calculateReward(getRankReward(rewardNode, period));

            calculateReward(getPersonalVolumeReward(rewardNode, period));

            getGroupVolumeRewards(rewardNode, period).forEach(this::calculateReward);

            getStructureVolumeRewards(rewardNode, period).forEach(this::calculateReward);

            if (!test) {
                WorkerNode workerNode = rewardNode.getWorkerNode();

                workerNode.setPeriodId(periodId);

                domainService.save(workerNode);

                rewardNode.zeroToNull();

                if (!rewardNode.isNull()) {
                    rewardNode.setPeriodId(periodId);

                    domainService.save(rewardNode);
                }
            }
        });

        getEstimatedRewards(period).forEach(reward -> {
            reward.setDate(Dates.currentDate());
            reward.setMonth(period.getOperatingMonth());
            reward.setPeriodId(period.getObjectId());

            chargeReward(reward);
        });
    }

    private void clearCache() {
        saleCacheService.clear();
        paymentCacheService.clear();
        parameterCacheService.clear();
        rewardTreeCacheService.clear();
        rewardCacheService.clear();
    }

    @Transactional
    public void calculateRewards() {
        calculateRewards(periodMapper.getActualPeriod());
    }

    public void testRewards(Period period) {
        test = true;

        calculateRewards(period);

        clearCache();

        test = false;
    }

    private void repair() {
        rewardMapper.getRewards(FilterWrapper.of(new Reward())).forEach(this::repair);
    }

    private void repair(Reward reward) {
        if (Objects.equals(reward.getType(), CULINARY_WORKSHOP) && reward.getWorkerId() == null) {
            reward.setDetail("empty worker");

            log.warn("empty worker {}", reward);

            domainService.delete(reward);
        }

        if (periodMapper.getPeriod(reward.getPeriodId()) == null) {
            reward.setDetail("empty period");

            log.warn("empty period {}", reward);

            domainService.delete(reward);
        }

        if (reward.getPoint() != null && reward.getPoint().compareTo(ZERO) == 0) {
            reward.setDetail("zero point");

            log.warn("zero point {}", reward);

            domainService.delete(reward);
        }
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }
}
