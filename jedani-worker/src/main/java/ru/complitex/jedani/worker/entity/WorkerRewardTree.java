package ru.complitex.jedani.worker.entity;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author Anatoly A. Ivanov
 * 25.10.2019 9:50 PM
 */
public class WorkerRewardTree {
    private Long treeDepth;

    private Map<Long, WorkerReward> idMap = new HashMap<>();
    private Map<Long, List<WorkerReward>> levelMap = new HashMap<>();

    public WorkerRewardTree(Map<Long, List<WorkerNode>> workerNodeMap) {
        treeDepth = workerNodeMap.keySet().stream().max(Comparator.naturalOrder()).orElse(-1L);

        for (long l = treeDepth; l > 0; l--){
            List<WorkerReward> list = new ArrayList<>();

            workerNodeMap.get(l).forEach(n -> {
                WorkerReward r = new WorkerReward(n);

                list.add(r);

                idMap.put(n.getObjectId(), r);

                r.getWorkerNode().getChildNodes()
                        .forEach(c -> r.getChildRewards().add(idMap.get(c.getObjectId())));
            });

            levelMap.put(l, list);
        }
    }

    public Long getTreeDepth() {
        return treeDepth;
    }

    public WorkerReward getWorkerReward(Long workerId){
        return idMap.get(workerId);
    }

    public List<WorkerReward> getWorkerRewards(Long level){
        return levelMap.get(level);
    }

    public void forEachLevel(BiConsumer<Long, List<WorkerReward>> action){
        for (long l = treeDepth; l > 0 ; l--){
            action.accept(l, levelMap.get(l));
        }
    }
}
