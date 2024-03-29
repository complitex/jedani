package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.cache.RewardParameterCacheService;
import ru.complitex.jedani.worker.service.cache.SaleCacheService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.math.BigDecimal.ZERO;
import static ru.complitex.jedani.worker.entity.RankType.*;
import static ru.complitex.jedani.worker.entity.RewardParameterType.*;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class RewardTreeService {
    @Inject
    private DomainService domainService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private SaleService saleService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private WorkerNodeService workerNodeService;

    @Inject
    private SaleCacheService saleCacheService;

    @Inject
    private RewardParameterCacheService parameterCacheService;

    private BigDecimal getParameter(Long rewardParameterId, Period period) {
        return parameterCacheService.getParameter(rewardParameterId, period.getObjectId());
    }

    private Long getRank(BigDecimal saleVolume, Period period) {
        if (saleVolume.compareTo(getParameter(RANK_PLATINUM_DIRECTOR, period)) >= 0) {
            return PLATINUM_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(RANK_GOLD_DIRECTOR, period)) >= 0) {
            return GOLD_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(RANK_SILVER_DIRECTOR, period)) >= 0) {
            return SILVER_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(RANK_REGIONAL_MANAGER, period)) >= 0) {
            return REGIONAL_MANAGER;
        }else if (saleVolume.compareTo(getParameter(RANK_AREA_MANAGER, period)) >= 0) {
            return AREA_MANAGER;
        }else if (saleVolume.compareTo(getParameter(RANK_DIVISION_MANAGER, period)) >= 0) {
            return DIVISION_MANAGER;
        }else if (saleVolume.compareTo(getParameter(RANK_SENIOR_MANAGER, period)) >= 0) {
            return SENIOR_MANAGER;
        }else if (saleVolume.compareTo(getParameter(RANK_SENIOR_ASSISTANT, period)) >= 0) {
            return SENIOR_ASSISTANT;
        }else if (saleVolume.compareTo(getParameter(RANK_TEAM_MANAGER, period)) >= 0) {
            return TEAM_MANAGER;
        }else if (saleVolume.compareTo(getParameter(RANK_MANAGER_JUNIOR, period)) >= 0) {
            return MANAGER_JUNIOR;
        }else if (saleVolume.compareTo(getParameter(RANK_MANAGER_ASSISTANT, period)) >= 0) {
            return MANAGER_ASSISTANT;
        }

        return 0L;
    }

    private void updateRewardNode(RewardNode rewardNode, Period period) {
        if (saleCacheService.hasSale(rewardNode.getWorkerNode().getWorkerId())) {
            rewardNode.setSales(saleService.getSalesBeforeOrEqual(rewardNode.getWorkerId(), period.getObjectId()));

            rewardNode.setSaleVolume(rewardNode.getSales().stream()
                    .filter(s -> Objects.equals(s.getPeriodId(), period.getObjectId()))
                    .filter(s -> !Objects.equals(s.getSaleStatus(), SaleStatus.CREATED))
                    .map(s -> s.getTotal() != null ? s.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            rewardNode.setPaymentVolume(paymentService.getPaymentsVolumeBySellerWorkerId(rewardNode.getWorkerNode().getWorkerId(), period));

            rewardNode.setYearPaymentVolume(paymentService.getYearPaymentsVolumeBySellerWorkerId(rewardNode.getWorkerNode().getWorkerId()));
        }


        rewardNode.getGroup().forEach(r -> rewardNode.getGroupSales().addAll(r.getSales()));

        rewardNode.setGroupSaleVolume(rewardNode.getGroup().stream()
                .reduce(ZERO, (v, r) -> v.add(r.getSaleVolume()), BigDecimal::add));

        rewardNode.setGroupPaymentVolume(rewardNode.getGroup().stream()
                .reduce(ZERO, (v, r) -> v.add(r.getPaymentVolume()), BigDecimal::add));


        rewardNode.getStructureSales().addAll(rewardNode.getSales());

        rewardNode.getRewardNodes().forEach(r -> rewardNode.getStructureSales().addAll(r.getStructureSales()));

        rewardNode.setStructureSaleVolume(rewardNode.getRewardNodes().stream()
                .reduce(rewardNode.getSaleVolume(), (v, r) -> v.add(r.getStructureSaleVolume()), BigDecimal::add));

        rewardNode.setStructurePaymentVolume(rewardNode.getRewardNodes().stream()
                .reduce(rewardNode.getPaymentVolume(), (v, r) -> v.add(r.getStructurePaymentVolume()), BigDecimal::add));


        rewardNode.setRegistrationDate(domainService.getDate(Worker.ENTITY_NAME, rewardNode.getWorkerId(), Worker.REGISTRATION_DATE));

        rewardNode.setWorkerStatus(domainService.getNumber(Worker.ENTITY_NAME, rewardNode.getWorkerId(), Worker.STATUS));

        rewardNode.setPk(Dates.isLessYear(period.getOperatingMonth(), rewardNode.getRegistrationDate()) ||
                rewardNode.getYearPaymentVolume().compareTo(BigDecimal.valueOf(200)) >= 0);


        rewardNode.setFirstLevelCount(rewardNode.getRewardNodes().stream()
                .filter(r -> r.getRegistrationDate() != null &&
                        r.getRegistrationDate().before(Dates.nextMonth(period.getOperatingMonth())) &&
                        r.isPk())
                .count());

        rewardNode.setFirstLevelPersonalCount(rewardNode.getRewardNodes().stream()
                .filter(r -> r.getRegistrationDate() != null &&
                        r.getRegistrationDate().before(Dates.nextMonth(period.getOperatingMonth())) &&
                        !Objects.equals(r.getWorkerStatus(), WorkerStatus.MANAGER_CHANGED) &&
                        r.isPk())
                .count());

        rewardNode.setRegistrationCount(rewardNode.getRewardNodes().stream()
                .reduce(0L, (v, r) -> v + (r.getRegistrationDate() != null &&
                        Dates.isSameMonth(r.getRegistrationDate(), period.getOperatingMonth()) &&
                        !Objects.equals(r.getWorkerStatus(), WorkerStatus.MANAGER_CHANGED) ? 1L : 0L), Long::sum));

        rewardNode.setGroupRegistrationCount(rewardNode.getGroup().stream()
                .reduce(0L, (v, r) -> v + (r.getRegistrationDate() != null &&
                        Dates.isSameMonth(r.getRegistrationDate(), period.getOperatingMonth()) &&
                        !Objects.equals(r.getWorkerStatus(), WorkerStatus.MANAGER_CHANGED) ? 1 : 0), Long::sum));

        rewardNode.setStructureManagerCount(rewardNode.getRewardNodes().stream()
                .reduce(0L, (v, c) -> v + c.getStructureManagerCount() + (c.isManager() ? 1 : 0), Long::sum));

        List<RewardRank> rewardRanks = domainService.getDomains(RewardRank.class,
                new FilterWrapper<>(new RewardRank()
                        .setWorkerId(rewardNode.getWorkerId())
                        .setPeriodId(period.getObjectId())));

        if (rewardNode.getFirstLevelPersonalCount() >= 4 &&
                rewardNode.getGroupRegistrationCount() >= 2 &&
                rewardNode.getPaymentVolume().compareTo(BigDecimal.valueOf(200)) >= 0 &&
                (rewardRanks.isEmpty() || !Objects.equals(rewardRanks.get(0).getRank(), 0L))) {
            rewardNode.setRank(getRank(rewardNode.getStructureSaleVolume(), period));
        }
    }

    public void updateRewardTree(RewardTree rewardTree, Period period, Consumer<RewardNode> consumer) {
        rewardTree.forEachLevel((level, rewardNodes) ->
                rewardNodes.forEach(rewardNode -> {
                    updateRewardNode(rewardNode, period);

                    if (consumer != null) {
                        consumer.accept(rewardNode);
                    }
                }));
    }

    public RewardTree getRewardTree(Long periodId) {
        RewardTree rewardTree = new RewardTree(workerNodeService.getWorkerNodeMap());

        updateRewardTree(rewardTree, periodMapper.getPeriod(periodId), null);

        return rewardTree;
    }
}
