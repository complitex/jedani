package ru.complitex.jedani.worker.service;

import org.apache.wicket.util.lang.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.worker.entity.WorkerNode;
import ru.complitex.jedani.worker.mapper.WorkerNodeMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 23.10.2019 8:17 PM
 */
public class WorkerNodeService implements Serializable {
    private final Logger log = LoggerFactory.getLogger(WorkerNodeService.class);

    @Inject
    private WorkerNodeMapper workerNodeMapper;

    public WorkerNode getWorkerTree(List<WorkerNode> workerNodes, Long rootWorkerId){
        Map<Long, WorkerNode> map = workerNodes.stream()
                .collect(Collectors.toMap(w -> Objects.defaultIfNull(w.getObjectId(), -1L), w -> w));

        map.forEach((k, w) -> {
            if (w.getManagerId() != -1L){
                WorkerNode m = map.get(w.getManagerId());

                if (m != null){
                    m.getChildNodes().add(w);
                }else{
                    log.error("getWorkerTree no manager found " + w);
                }
            }else{
                log.warn("getWorkerTree no manager id " + w);
            }
        });

        return map.get(rootWorkerId);
    }

    private void validateWorkerTree(WorkerNode rootWorkerNode){
        rootWorkerNode.getChildNodes().forEach(c -> validateWorkerNode(rootWorkerNode, c));
    }

    private void validateWorkerNode(WorkerNode managerWorkerNode, WorkerNode workerNode){
        if (!workerNode.getManagerId().equals(managerWorkerNode.getObjectId())){
            throw new RuntimeException("validateWorkerNode manager id error " + managerWorkerNode + " " + workerNode);
        }

        if (workerNode.getLeft() <= managerWorkerNode.getLeft()){
            throw new RuntimeException("validateWorkerNode left error " + managerWorkerNode + " " + workerNode);
        }

        if (workerNode.getRight() >= managerWorkerNode.getRight()){
            throw new RuntimeException("validateWorkerNode right error " + managerWorkerNode + " " + workerNode);
        }

        if (workerNode.getLevel() != managerWorkerNode.getLevel() + 1){
            throw new RuntimeException("validateWorkerNode level error " + managerWorkerNode + " " + workerNode);
        }

        workerNode.getChildNodes().forEach(c -> validateWorkerNode(workerNode, c));
    }

    public Map<Long, List<WorkerNode>> getWorkerNodeLevelMap(){
        List<WorkerNode> workerNodes = workerNodeMapper.getAllWorkerNodes();

        validateWorkerTree(getWorkerTree(workerNodes, 2L));

        Map<Long, List<WorkerNode>> map = new HashMap<>();

        workerNodes.forEach(w -> map.computeIfAbsent(w.getLevel(), k -> new ArrayList<>()).add(w));

        return map;
    }
}
