package ru.complitex.jedani.worker.service;

import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.entity.WorkerRewardTree;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        calcSaleVolume(tree);
        calcGroupSaleVolume(tree);

        return tree;
    }

    private void calcSaleVolume(WorkerRewardTree tree){
        tree.forEach((k, v) -> {
            v.forEach(w -> w.setSaleVolume(saleService.getSaleVolume(w.getWorkerNode().getObjectId())));
        });
    }

    private void calcGroupSaleVolume(WorkerRewardTree tree){
        for (long l = tree.getTreeDepth(); l > 0 ; l--){
            List<WorkerReward> list = tree.get(l);

            list.forEach(r -> {
                r.setGroupSaleVolume(r.getChildRewards().stream()
                        .reduce(BigDecimal.ZERO, (v, c) -> c.getSaleVolume().add(c.getGroupSaleVolume()),
                                BigDecimal::add));
            });
        }
    }

}
