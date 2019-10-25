package ru.complitex.jedani.worker.service;

import ru.complitex.common.util.Dates;
import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.entity.WorkerRewardTree;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 20.10.2019 11:02 AM
 */
public class RewardService implements Serializable {
    @Inject
    private WorkerNodeService workerNodeService;

    @Inject
    private SaleService saleService;

    public WorkerRewardTree calcRewards(){
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap(), 2L);

        calcSaleVolume(tree);
        calcGroupSaleVolume(tree);
        calcRegistrationCount(tree, Dates.currentDate());
        calcFirstLevelCount(tree);

        return tree;
    }

    private void calcSaleVolume(WorkerRewardTree tree){
        tree.forEach((k, v) -> {
            v.forEach(w -> w.setSaleVolume(saleService.getSaleVolume(w.getWorkerNode().getObjectId())));
        });
    }

    private void calcGroupSaleVolume(WorkerRewardTree tree){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setGroupSaleVolume(r.getChildRewards().stream()
                .reduce(BigDecimal.ZERO, (v, c) -> v.add(c.getSaleVolume().add(c.getGroupSaleVolume())),
                        BigDecimal::add))));
    }

    private void calcRegistrationCount(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) ->  rl.forEach(r -> r.setRegistrationCount(r.getChildRewards().stream()
                .filter(c -> !c.isManager() && c.getWorkerNode().getRegistrationDate() != null)
                .reduce(0L, (v, c) -> c.getRegistrationCount() + (isNewWorker(c, date) ? 1 : 0), Long::sum))));
    }

    private boolean isNewWorker(WorkerReward workerReward, Date date){
        return Dates.isSameMonth(workerReward.getWorkerNode().getRegistrationDate(), date);
    }

    private void calcFirstLevelCount(WorkerRewardTree tree){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelCount(Long.valueOf(r.getChildRewards().size()))));
    }
}
