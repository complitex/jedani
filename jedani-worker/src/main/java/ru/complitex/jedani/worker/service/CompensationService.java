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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;
import static ru.complitex.jedani.worker.entity.MkStatus.*;
import static ru.complitex.jedani.worker.entity.RankType.*;
import static ru.complitex.jedani.worker.entity.RewardParameterType.*;
import static ru.complitex.jedani.worker.entity.RewardStatus.*;
import static ru.complitex.jedani.worker.entity.RewardType.*;
import static ru.complitex.jedani.worker.entity.SaleStatus.CREATED;
import static ru.complitex.jedani.worker.entity.SaleType.MYCOOK;
import static ru.complitex.jedani.worker.entity.SaleType.RANGE;

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
    private RewardParameterCacheService parameterCacheService;

    @Inject
    private RewardTreeCacheService rewardTreeCacheService;

    @Inject
    private RewardCacheService rewardCacheService;

    @Inject
    private PaymentService paymentService;

    private final boolean incremental = true;

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
        reward.setAmount(reward.getPoint().multiply(reward.getRate())
                .multiply(reward.getCrossRate() != null ? reward.getCrossRate() : ONE)
                .multiply(reward.getDiscount() != null ? reward.getDiscount() : ONE)
                .setScale(5, HALF_EVEN));
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

    private BigDecimal getParameter(Long rewardParameterId, Period period) {
        return parameterCacheService.getParameter(rewardParameterId, period.getObjectId());
    }

    public BigDecimal getPersonalRewardPoint(Sale sale, List<SaleItem> saleItems, Period period) {
        BigDecimal point = ZERO;

        if (sale.getType() == MYCOOK) {
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null) {
                w.setMkStatus(STATUS_PROMO);
            }

            boolean mkPremium = saleService.isMycookPremiumSaleItems(saleItems);
            boolean mkTouch = saleService.isMycookTouchSaleItems(saleItems);

            if (sale.isSasRequest()) {
                point = getParameter(PERSONAL_MYCOOK_SAP, period);
            }else if (w.getMkStatus() == STATUS_PROMO) {
                if (mkPremium) {
                    point = getParameter(PERSONAL_MYCOOK_PROMO_PREMIUM, period);
                }else if (mkTouch) {
                    point = getParameter(PERSONAL_MYCOOK_PROMO_TOUCH, period);
                }
            }else if (w.getMkStatus() == STATUS_JUST) {
                if (mkPremium) {
                    point = getParameter(PERSONAL_MYCOOK_JUST_PREMIUM, period);
                }else if (mkTouch) {
                    point = getParameter(PERSONAL_MYCOOK_JUST_TOUCH, period);
                }
            }else if (w.getMkStatus() == STATUS_VIP) {
                if (sale.getManagerBonusWorkerId() != null) {
                    if (mkPremium) {
                        point = getParameter(PERSONAL_MYCOOK_JUST_PREMIUM, period)
                                .subtract(getParameter(MANAGER_BONUS_PREMIUM, period));
                    }else if (mkTouch) {
                        point = getParameter(PERSONAL_MYCOOK_VIP_TOUCH, period)
                                .subtract(getParameter(MANAGER_BONUS_TOUCH, period));
                    }
                }else if (mkPremium) {
                    point = getParameter(PERSONAL_MYCOOK_VIP_PREMIUM, period);
                }else if (mkTouch) {
                    point = getParameter(PERSONAL_MYCOOK_VIP_TOUCH, period);
                }
            }
        }else if (sale.getType() == RANGE && sale.getTotal() != null) {
            point = sale.getTotal().multiply(getParameter(PERSONAL_RANGE_REWARD, period));
        }

        return point;
    }

    public Reward getPersonalReward(Sale sale, SaleItem saleItem, Period period) {
        if (sale.getPersonalRewardPoint() != null && sale.getPersonalRewardPoint().compareTo(ZERO) > 0) {
            Long rewardType = Objects.equals(sale.getType(), MYCOOK) ? PERSONAL_MYCOOK :
                    Objects.equals(sale.getType(), RANGE) ? PERSONAL_RANGE : 0;

            return getReward(rewardType, sale.getSellerWorkerId(), sale.getPersonalRewardPoint(), sale, saleItem, period);
        }

        return null;
    }

    public BigDecimal getManagerBonusRewardPoint(Sale sale, List<SaleItem> saleItems, Period period) {
        BigDecimal point = ZERO;

        if (sale.getType() == MYCOOK) {
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(STATUS_PROMO);
            }

            if (saleService.isMycookPremiumSaleItems(saleItems)) {
                point = getParameter(MANAGER_BONUS_PREMIUM, period);
            }else if (saleService.isMycookTouchSaleItems(saleItems)) {
                point = getParameter(MANAGER_BONUS_TOUCH, period);
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

    public BigDecimal getCulinaryRewardPoint(Sale sale, Period period) {
        if (sale.getType() == MYCOOK) {
            if (!sale.isSasRequest()) {
                return getParameter(CULINARY_WORKSHOP_REWARD, period);
            } else {
                return getParameter(CULINARY_WORKSHOP_SAP, period);
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

    private BigDecimal getManagerMycookPoint(Long rank, Period period) {
        switch (rank.intValue()) {
            case (int) MANAGER_ASSISTANT:
                return getParameter(MANAGER_MYCOOK_MANAGER_ASSISTANT, period);
            case (int) MANAGER_JUNIOR:
                return getParameter(MANAGER_MYCOOK_MANAGER_JUNIOR, period);
            case (int) TEAM_MANAGER:
                return getParameter(MANAGER_MYCOOK_TEAM_MANAGER, period);
            case (int) SENIOR_ASSISTANT:
                return getParameter(MANAGER_MYCOOK_SENIOR_ASSISTANT, period);
            case (int) SENIOR_MANAGER:
                return getParameter(MANAGER_MYCOOK_SENIOR_MANAGER, period);
            case (int) DIVISION_MANAGER:
                return getParameter(MANAGER_MYCOOK_DIVISION_MANAGER, period);
            case (int) AREA_MANAGER:
                return getParameter(MANAGER_MYCOOK_AREA_MANAGER, period);
            case (int) REGIONAL_MANAGER:
                return getParameter(MANAGER_MYCOOK_REGIONAL_MANAGER, period);
            case (int) SILVER_DIRECTOR:
                return getParameter(MANAGER_MYCOOK_SILVER_DIRECTOR, period);
            case (int) GOLD_DIRECTOR:
                return getParameter(MANAGER_MYCOOK_GOLD_DIRECTOR, period);
            case (int) PLATINUM_DIRECTOR:
                return getParameter(MANAGER_MYCOOK_PLATINUM_DIRECTOR, period);
            default:
                return ZERO;
        }
    }

    private BigDecimal getManagerRangePoint(Long rank, Period period) {
        switch (rank.intValue()) {
            case (int) MANAGER_ASSISTANT:
                return getParameter(MANAGER_RANGE_MANAGER_ASSISTANT, period);
            case (int) MANAGER_JUNIOR:
                return getParameter(MANAGER_RANGE_MANAGER_JUNIOR, period);
            case (int) TEAM_MANAGER:
                return getParameter(MANAGER_RANGE_TEAM_MANAGER, period);
            case (int) SENIOR_ASSISTANT:
                return getParameter(MANAGER_RANGE_SENIOR_ASSISTANT, period);
            case (int) SENIOR_MANAGER:
                return getParameter(MANAGER_RANGE_SENIOR_MANAGER, period);
            case (int) DIVISION_MANAGER:
                return getParameter(MANAGER_RANGE_DIVISION_MANAGER, period);
            case (int) AREA_MANAGER:
                return getParameter(MANAGER_RANGE_AREA_MANAGER, period);
            case (int) REGIONAL_MANAGER:
                return getParameter(MANAGER_RANGE_REGIONAL_MANAGER, period);
            case (int) SILVER_DIRECTOR:
                return getParameter(MANAGER_RANGE_SILVER_DIRECTOR, period);
            case (int) GOLD_DIRECTOR:
                return getParameter(MANAGER_RANGE_GOLD_DIRECTOR, period);
            case (int) PLATINUM_DIRECTOR:
                return getParameter(MANAGER_RANGE_PLATINUM_DIRECTOR, period);
            default:
                return ZERO;
        }
    }

    private Reward getManagerReward(RewardNode rewardNode, Sale sale, SaleItem saleItem, Period period) {
        Long rank = Objects.equals(sale.getPeriodId(), period.getObjectId())
                ? rewardNode.getRank()
                : rewardTreeCacheService.getRewardNode(rewardNode.getWorkerId(), sale.getPeriodId()).getRank();

        BigDecimal point = Objects.equals(sale.getType(), MYCOOK) ? getManagerMycookPoint(rank, period)
                : Objects.equals(sale.getType(), RANGE)  ? getManagerRangePoint(rank, period) : ZERO;

        if (point.compareTo(ZERO) > 0) {
            Long rewardTypeId = Objects.equals(sale.getType(), MYCOOK) ? MANAGER_MYCOOK
                    : Objects.equals(sale.getType(), RANGE) ? MANAGER_RANGE : 0;

            Reward reward = getReward(rewardTypeId, sale.getSellerWorkerId(), point, sale, saleItem, period);

            reward.setRank(rank);

            reward.setStructureSaleVolume(Objects.equals(sale.getPeriodId(), period.getObjectId())
                    ? rewardNode.getStructureSaleVolume()
                    : rewardTreeCacheService.getRewardNode(rewardNode.getWorkerId(), sale.getPeriodId()).getStructureSaleVolume());

            return reward;
        }

        return null;
    }

    private Reward getPersonalVolumeReward(RewardNode rewardNode, Period period) {
        BigDecimal minPoint = getParameter(PERSONAL_VOLUME_MINIMUM, period);
        BigDecimal avgPoint = getParameter(PERSONAL_VOLUME_AVERAGE, period);
        BigDecimal lowerPoint = getParameter(PERSONAL_VOLUME_LESS_THAN_AVERAGE, period);
        BigDecimal greaterPoint = getParameter(PERSONAL_VOLUME_MORE_THAN_AVERAGE, period);

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

            reward.setType(RANK);
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

    private BigDecimal getGroupVolumePercent(Long rank, Period period) {
        switch (rank.intValue()){
            case (int) MANAGER_ASSISTANT:
                return getParameter(GROUP_VOLUME_MANAGER_ASSISTANT, period);
            case (int) MANAGER_JUNIOR:
                return getParameter(GROUP_VOLUME_MANAGER_JUNIOR, period);
            case (int) TEAM_MANAGER:
                return getParameter(GROUP_VOLUME_TEAM_MANAGER, period);
            case (int) SENIOR_ASSISTANT:
                return getParameter(GROUP_VOLUME_SENIOR_ASSISTANT, period);
            case (int) SENIOR_MANAGER:
                return getParameter(GROUP_VOLUME_SENIOR_MANAGER, period);
            case (int) DIVISION_MANAGER:
                return getParameter(GROUP_VOLUME_DIVISION_MANAGER, period);
            case (int) AREA_MANAGER:
                return getParameter(GROUP_VOLUME_AREA_MANAGER, period);
            case (int) REGIONAL_MANAGER:
                return getParameter(GROUP_VOLUME_REGIONAL_MANAGER, period);
            case (int) SILVER_DIRECTOR:
                return getParameter(GROUP_VOLUME_SILVER_DIRECTOR, period);
            case (int) GOLD_DIRECTOR:
                return getParameter(GROUP_VOLUME_GOLD_DIRECTOR, period);
            case (int) PLATINUM_DIRECTOR:
                return getParameter(GROUP_VOLUME_PLATINUM_DIRECTOR, period);
            default:
                return ZERO;
        }
    }

    private List<Reward> getGroupVolumeRewards(RewardNode rewardNode, Period period) {
        if (rewardNode.getRank() > 0) {
            List<Reward> rewards = new ArrayList<>();

            BigDecimal percent = getGroupVolumePercent(rewardNode.getRank(), period);

            BigDecimal sum = percent.multiply(rewardNode.getGroupSaleVolume()).divide(BD_100, 5, HALF_EVEN);

            List<Sale> groupSales = rewardNode.getGroupSales().stream()
                    .filter(sale -> Objects.equals(sale.getPeriodId(), period.getObjectId()))
                    .filter(sale -> !Objects.equals(sale.getSaleStatus(), CREATED))
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
            BigDecimal total = getGroupVolumePercent(rewardNode.getRank(), period)
                    .subtract(getGroupVolumePercent(m.getRank(), period))
                    .multiply(m.getStructureSaleVolume())
                    .divide(BD_100, 5, HALF_EVEN);

            sum = sum.add(total);
        }

        List<RewardNode> firstStructureManagers = rewardNode.getFirstStructureManagers();

        for (int i = 0; i < firstStructureManagers.size(); i++) {
            RewardNode m = firstStructureManagers.get(i);

            List<Sale> structureSales = m.getStructureSales().stream()
                    .filter(sale -> Objects.equals(sale.getPeriodId(), period.getObjectId()))
                    .filter(sale -> !Objects.equals(sale.getSaleStatus(), CREATED))
                    .collect(Collectors.toList());

            for (int j = 0; j < structureSales.size(); j++) {
                Sale sale = structureSales.get(j);

                BigDecimal point = getGroupVolumePercent(rewardNode.getRank(), period)
                        .subtract(getGroupVolumePercent(m.getRank(), period))
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

    public BigDecimal getRewardsPointSumBefore(Long workerId, Long rewardTypeId, Long saleId, Long managerId, Long rewardStatusId, Long periodId) {
        BigDecimal sum = rewardMapper.getRewardsPointSumBefore(workerId, rewardTypeId, saleId, managerId, rewardStatusId, periodId);

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

                BigDecimal sum = getRewardsPointSumBefore(reward.getWorkerId(), rewardType, reward.getSaleId(), reward.getManagerId(), CHARGED, reward.getPeriodId());

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
        } else if (reward.getPoint() == null || reward.getPoint().compareTo(ZERO) > 0) {
            reward.setObjectId(null);
            reward.setStartDate(Dates.currentDate());

            domainService.save(reward);
        }
    }

    private boolean isEstimated(Reward reward) {
        return getRewardsPointSumBefore(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), ESTIMATED,  reward.getPeriodId())
                .compareTo(ZERO) != 0;
    }

    private boolean isCharged(Reward reward) {
        return getRewardsPointSumBefore(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), ESTIMATED, reward.getPeriodId())
                .compareTo(ZERO) != 0;
    }

    private boolean isEstimatedAndCharged(Reward reward, Period period) {
        Long periodId = period != null ? period.getObjectId() : reward.getPeriodId();

        return getRewardsPointSumBefore(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), ESTIMATED, periodId)
                .compareTo(getRewardsPointSumBefore(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), CHARGED, periodId)) == 0;
    }

    private boolean isWithdraw(Reward reward) {
        return getRewardsPointSumBefore(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), WITHDRAWN,
                reward.getPeriodId()).compareTo(ZERO) != 0;
    }

    private void estimateReward(Reward reward, Consumer<Reward> consumer) {
        if (reward != null && !isEstimated(reward) && !isCharged(reward)) {
            reward.setRewardStatus(ESTIMATED);

            save(reward);

            if (consumer != null) {
                consumer.accept(reward);
            }
        }
    }

    private void chargeReward(Reward reward, Consumer<Reward> consumer) {
        if (reward != null) {
            updateRewardPoint(reward.getType(), reward);

            if (reward.getPoint() != null && reward.getPoint().compareTo(ZERO) != 0) {
                updateAmount(reward);

                reward.setRewardStatus(CHARGED);

                save(reward);

                if (consumer != null) {
                    consumer.accept(reward);
                }
            }
        }
    }

    public void withdrawReward(Reward reward, Consumer<Reward> consumer) {
        if (reward != null && !isWithdraw(reward)) {
            reward.setRewardStatus(WITHDRAWN);

            save(reward);

            if (consumer != null) {
                consumer.accept(reward);
            }
        }
    }

    public void withdrawReward(Reward reward) {
        withdrawReward(reward, null);
    }

    public void calculateReward(Reward reward, Consumer<Reward> consumer) {
        estimateReward(reward, consumer);

        chargeReward(reward, consumer);
    }

    public void calculateReward(Reward reward) {
        calculateReward(reward, null);
    }

    private List<Reward> getEstimatedRewards(Period period) {
        return rewardMapper.getRewards(FilterWrapper.of(new Reward().setRewardStatus(ESTIMATED))).stream()
                .filter(reward -> reward.getPoint() != null)
                .filter(reward -> periodMapper.getOperationMonth(reward.getPeriodId()).before(period.getOperatingMonth()))
                .filter(reward -> !isEstimatedAndCharged(reward, period))
                .collect(Collectors.toList());
    }

    private boolean hasPayment(Long saleId, Long periodId) {
        return paymentService.getPaymentsBySaleId(saleId).stream()
                .anyMatch(payment -> Objects.equals(payment.getPeriodId(), periodId));
    }

    @Transactional
    public void calculateRewards(Period period, Consumer<Reward> consumer) {
        Long periodId = period.getObjectId();

        if (!test) {
            rewardMapper.deleteRewards(periodId);

            rewardNodeMapper.deleteRewardNodes(periodId);

            workerNodeMapper.deleteWorkerNodes(periodId);

            repair();
        }

        repair();

        rewards.clear();

        clearCache();

        RewardTree rewardTree = new RewardTree(workerNodeService.getWorkerNodeMap());

        rewardTreeService.updateRewardTree(rewardTree, period, rewardNode -> {
            rewardNode.getSales().stream()
                    .filter(sale -> !test || sale.getPeriodId() <= periodId)
                    .forEach(sale -> {
                            if (!incremental || Objects.equals(sale.getPeriodId(), periodId)) {
                                SaleItem saleItem = saleService.getSaleItem(sale.getObjectId());

                                Reward personalReward = getPersonalReward(sale, saleItem, period);

                                calculateReward(personalReward, consumer);

                                if (sale.isFeeWithdraw()) {
                                    withdrawReward(personalReward, consumer);
                                }

                                if (!sale.isSasRequest()) {
                                    calculateReward(getManagerReward(rewardNode, sale, saleItem, period), consumer);
                                }

                                calculateReward(getManagerBonusReward(sale, saleItem, period), consumer);

                                calculateReward(getCulinaryReward(sale, saleItem, period), consumer);
                            } else {
                                if (getRewardsPointSumBefore(sale.getCulinaryWorkerId(), CULINARY_WORKSHOP, sale.getObjectId(), null, ESTIMATED, periodId)
                                        .compareTo(ZERO) == 0) {
                                    calculateReward(getCulinaryReward(sale, saleService.getSaleItem(sale.getObjectId()), period), consumer);
                                }
                            }
                    });

            calculateReward(getRankReward(rewardNode, period), consumer);

            calculateReward(getPersonalVolumeReward(rewardNode, period), consumer);

            getGroupVolumeRewards(rewardNode, period).forEach(reward -> calculateReward(reward, consumer));

            getStructureVolumeRewards(rewardNode, period).forEach(reward -> calculateReward(reward, consumer));

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

        getEstimatedRewards(period).stream()
                .filter(reward -> incremental ||
                        Objects.equals(reward.getType(), PERSONAL_VOLUME) ||
                        Objects.equals(reward.getType(), GROUP_VOLUME) ||
                        Objects.equals(reward.getType(), STRUCTURE_VOLUME))
                .filter(reward -> hasPayment(reward.getSaleId(), periodId))
                .forEach(reward -> {
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(period.getOperatingMonth());
                    reward.setPeriodId(period.getObjectId());
                    reward.setEstimatedId(reward.getObjectId());

                    chargeReward(reward, consumer);
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
    public void calculateRewards(Consumer<Reward> consumer) {
        calculateRewards(periodMapper.getActualPeriod(), consumer);
    }

    public void testRewards(Period period) {
        test = true;

        calculateRewards(period, null);

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

        if (Objects.equals(reward.getType(), MANAGER_BONUS) &&
                saleService.getManagerBonusWorkerId(reward.getSaleId()) == null) {
            reward.setDetail("empty manager bonus worker");

            log.warn("empty manager bonus worker {}", reward);

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
