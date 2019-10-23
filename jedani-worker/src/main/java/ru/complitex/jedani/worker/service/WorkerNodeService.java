package ru.complitex.jedani.worker.service;

import org.apache.wicket.util.lang.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.worker.entity.WorkerNode;
import ru.complitex.jedani.worker.mapper.WorkerNodeMapper;

import javax.inject.Inject;
import java.io.Serializable;
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

    public WorkerNode getWorkerTree(Long rootObjectId){
        List<WorkerNode> list = workerNodeMapper.getAllWorkerNodes();

        Map<Long, WorkerNode> map = list.stream()
                .collect(Collectors.toMap(w -> Objects.defaultIfNull(w.getObjectId(), -1L), w -> w));

        map.forEach((k, w) -> {
            if (w.getManagerId() != -1L){
                WorkerNode m = map.get(w.getManagerId());

                if (m != null){
                    m.getChildNodes().add(w);
                }else{
                    log.error("no manager found for " + w);
                }
            }else{
                log.warn("no manager_id for " + w);
            }
        });

        return map.get(rootObjectId);
    }

    public void validateWorkerTree(){
        WorkerNode rootWorkerNode = getWorkerTree(2L);

        validateWorkerTree(rootWorkerNode);
    }

    private void validateWorkerTree(WorkerNode rootWorkerNode){
        rootWorkerNode.getChildNodes().forEach(c -> validateWorkerNode(rootWorkerNode, c));
    }

    private void validateWorkerNode(WorkerNode managerWorkerNode, WorkerNode workerNode){
        if (!workerNode.getManagerId().equals(managerWorkerNode.getObjectId())){
            throw new RuntimeException("validation manager id error " + managerWorkerNode + " " + workerNode);
        }

        if (workerNode.getLeft() <= managerWorkerNode.getLeft()){
            throw new RuntimeException("validation left error " + managerWorkerNode + " " + workerNode);
        }

        if (workerNode.getRight() >= managerWorkerNode.getRight()){
            throw new RuntimeException("validation right error " + managerWorkerNode + " " + workerNode);
        }

        if (workerNode.getLevel() != managerWorkerNode.getLevel() + 1){
            throw new RuntimeException("validation level error " + managerWorkerNode + " " + workerNode);
        }

        workerNode.getChildNodes().forEach(c -> validateWorkerNode(workerNode, c));
    }
}
