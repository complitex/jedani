package ru.complitex.jedani.worker.service;

import ru.complitex.common.util.Dates;
import ru.complitex.jedani.worker.entity.*;

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

    @Inject
    private WorkerService workerService;

    public WorkerRewardTree calcRewards(){
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        calcSaleVolume(tree);
        calcGroupSaleVolume(tree);
        calcRegistrationCount(tree, Dates.currentDate());
        calcFirstLevelCount(tree);
        calcPersonalReward(tree, new Date(0));

        return tree;
    }

    private void calcSaleVolume(WorkerRewardTree tree){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(w -> w.setSaleVolume(saleService.getSaleVolume(w.getWorkerNode().getObjectId())));
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

    private void calcPersonalReward(WorkerRewardTree tree, Date date){
        saleService.getSales(date).forEach(s -> {
            if (s.getType() == SaleType.MYCOOK){
                Worker w = workerService.getWorker(s.getSellerWorkerId());

                boolean mkPremium = saleService.isMkPremiumSaleItem(s.getObjectId());
                boolean mkTouch = saleService.isMkTouchSaleItem(s.getObjectId());

                Reward r = new Reward();

                r.setType(7L);

                if (w.getMkStatus() == MkStatus.STATUS_NO_MK){
                    if (mkPremium){
                        r.setPoint(new BigDecimal("80"));
                    }else if (mkTouch){
                        r.setPoint(new BigDecimal("90"));
                    }
                }else if (w.getMkStatus() == MkStatus.STATUS_INSTALMENT_MK){ //todo >10%
                    if (mkPremium){
                        r.setPoint(new BigDecimal("120"));
                    }else if (mkTouch){
                        r.setPoint(new BigDecimal("130"));
                    }
                }else if (w.getMkStatus() == MkStatus.STATUS_HAS_MK){ //todo sap
                    if (mkPremium){
                        r.setPoint(new BigDecimal("170"));
                    }else if (mkTouch){
                        r.setPoint(new BigDecimal("195"));
                    }
                }

                if (r.getPoint() != null) {
                    tree.getWorkerReward(s.getSellerWorkerId()).getRewards().add(r);
                }
            }
        });
    }
}
