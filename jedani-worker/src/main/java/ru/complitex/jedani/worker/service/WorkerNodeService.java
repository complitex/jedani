package ru.complitex.jedani.worker.service;

import org.apache.wicket.util.lang.Objects;
import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.domain.service.DomainNodeService;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerNode;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
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
    private WorkerMapper workerMapper;

    @Inject
    private WorkerNodeMapper workerNodeMapper;

    @Inject
    private DomainNodeService domainNodeService;

    public WorkerNode getWorkerTree(List<WorkerNode> workerNodes, Long rootWorkerId){
        Map<Long, WorkerNode> map = workerNodes.stream()
                .collect(Collectors.toMap(w -> Objects.defaultIfNull(w.getWorkerId(), -1L), w -> w));

        map.forEach((k, w) -> {
            if (w.getManagerId() != -1L){
                WorkerNode m = map.get(w.getManagerId());

                if (m != null){
                    m.getNodes().add(w);
                }else{
                    //log.error("getWorkerTree no manager found " + w);
                }
            }else{
                //log.warn("getWorkerTree no manager id " + w);
            }
        });

        return map.get(rootWorkerId);
    }

    private void validateWorkerTree(WorkerNode rootWorkerNode){
        rootWorkerNode.getNodes().forEach(c -> validateWorkerNode(rootWorkerNode, c));
    }

    private void validateWorkerNode(WorkerNode managerWorkerNode, WorkerNode workerNode){
        if (!workerNode.getManagerId().equals(managerWorkerNode.getWorkerId())){
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

        workerNode.getNodes().forEach(c -> validateWorkerNode(workerNode, c));
    }

    public Map<Long, List<WorkerNode>> getWorkerNodeMap() {
        List<WorkerNode> workerNodes = workerNodeMapper.getAllWorkerNodes();

        validateWorkerTree(getWorkerTree(workerNodes, 2L));

        Map<Long, List<WorkerNode>> map = new HashMap<>();

        workerNodes.forEach(w -> map.computeIfAbsent(w.getLevel(), k -> new ArrayList<>()).add(w));

        return map;
    }

    @Transactional
    public void updateIndex(Worker worker) {
        Worker manager = workerMapper.getWorker(worker.getManagerId());

        domainNodeService.updateIndex(java.util.Objects.requireNonNullElseGet(manager, () -> new Worker(1L, 1L, 2L, 0L)), worker);
    }

    @Transactional
    public void rebuildIndex(){
        domainNodeService.rebuildIndex(Worker.ENTITY_NAME, 1L, Worker.MANAGER);
    }

    @Transactional
    public void moveIndex(Worker worker){
        Worker manager = workerMapper.getWorker(worker.getManagerId());

        if (!worker.getId().equals(manager.getId()) && (worker.getLeft() >= manager.getLeft() ||
                worker.getRight() <= manager.getRight())) {
            domainNodeService.move(manager, worker);
        } else {
            rebuildIndex();
        }
    }
}
