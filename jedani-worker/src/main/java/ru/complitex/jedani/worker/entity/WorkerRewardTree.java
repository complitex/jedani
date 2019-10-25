package ru.complitex.jedani.worker.entity;

import java.util.*;

/**
 * @author Anatoly A. Ivanov
 * 25.10.2019 9:50 PM
 */
public class WorkerRewardTree extends HashMap<Long, List<WorkerReward>> {
    private Long treeDepth;

    public WorkerRewardTree(Map<Long, List<WorkerNode>> workerNodeMap) {
        treeDepth = workerNodeMap.keySet().stream().max(Comparator.naturalOrder()).orElse(-1L);

        Map<Long, WorkerReward> map = new HashMap<>();

        for (long l = treeDepth; l > 0; l--){
            List<WorkerReward> list = new ArrayList<>();

            workerNodeMap.get(l).forEach(n -> {
                WorkerReward r = new WorkerReward(n);

                list.add(r);

                map.put(n.getObjectId(), r);

                r.getWorkerNode().getChildNodes()
                        .forEach(c -> r.getChildRewards().add(map.get(c.getObjectId())));
            });

            put(l, list);
        }
    }

    public Long getTreeDepth() {
        return treeDepth;
    }
}
