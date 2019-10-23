package ru.complitex.jedani.worker.service;

import ru.complitex.jedani.worker.entity.WorkerReward;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 20.10.2019 11:02 AM
 */
public class RewardService implements Serializable {
    @Inject
    private WorkerNodeService workerNodeService;

    @Inject
    private SaleService saleService;

    public Map<Long, List<WorkerReward>> calcRewards(){
        Map<Long, List<WorkerReward>> map = new HashMap<>();

        workerNodeService.getWorkerNodeLevelMap().forEach((k, v) -> {
            map.put(k, v.stream().map(WorkerReward::new).collect(Collectors.toList()));
        });

        calcSaleVolume(map);

        return map;
    }

    public Long getTreeDepth(Map<Long, List<WorkerReward>> map){
        return map.keySet().stream().max(Comparator.naturalOrder()).orElse(-1L);
    }

    private void calcSaleVolume(Map<Long, List<WorkerReward>> map){
        map.forEach((k, v) -> {
            v.forEach(w -> w.setSaleVolume(saleService.getSaleVolume(w.getWorkerNode().getObjectId())));
        });
    }

}
