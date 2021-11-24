package ru.complitex.jedani.worker.entity;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author Anatoly A. Ivanov
 * 25.10.2019 9:50 PM
 */
public class RewardTree {
    private final Long treeDepth;

    private final Map<Long, RewardNode> idMap = new HashMap<>();
    private final Map<Long, List<RewardNode>> levelMap = new HashMap<>();

    public RewardTree(Map<Long, List<WorkerNode>> workerNodeMap) {
        treeDepth = workerNodeMap.keySet().stream().max(Comparator.naturalOrder()).orElse(-1L);

        for (long l = treeDepth; l > 0; l--){
            List<RewardNode> list = new ArrayList<>();

            workerNodeMap.get(l).forEach(n -> {
                RewardNode r = new RewardNode(n);

                list.add(r);

                idMap.put(n.getWorkerId(), r);

                r.getWorkerNode().getNodes()
                        .forEach(c -> r.getRewardNodes().add(idMap.get(c.getWorkerId())));
            });

            levelMap.put(l, list);
        }
    }

    public Long getTreeDepth() {
        return treeDepth;
    }

    public RewardNode getRewardNode(Long workerId){
        return idMap.get(workerId);
    }

    public List<RewardNode> getRewardNodes(Long level){
        return levelMap.get(level);
    }

    public void forEachLevel(BiConsumer<Long, List<RewardNode>> action){
        for (long l = treeDepth; l > 0 ; l--){
            action.accept(l, levelMap.get(l));
        }
    }
}
