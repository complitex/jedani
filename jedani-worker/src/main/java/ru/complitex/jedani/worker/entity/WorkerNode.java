package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:00 AM
 */
public class WorkerNode extends Domain {
    public static final String ENTITY_NAME = "worker_node";

    public static final long WORKER_ID = 1;
    public static final long MANAGER_ID = 2;
    public static final long LEFT = 3;
    public static final long RIGHT = 4;
    public static final long LEVEL = 5;
    public static final long PERIOD = 6;

    private List<WorkerNode> workerNodes = new ArrayList<>();

    public Long getWorkerId() {
        return getNumber(WORKER_ID);
    }

    public void setWorkerId(Long workerId) {
        setNumber(WORKER_ID, workerId);
    }

    public Long getManagerId() {
        return getNumber(MANAGER_ID);
    }

    public void setManagerId(Long managerId) {
        setNumber(MANAGER_ID, managerId);
    }

    public Long getLeft() {
        return getNumber(LEFT);
    }

    public void setLeft(Long left) {
        setNumber(LEFT, left);
    }

    public Long getRight() {
        return getNumber(RIGHT);
    }

    public void setRight(Long right) {
        setNumber(RIGHT, right);
    }

    public Long getLevel() {
        return getNumber(LEVEL);
    }

    public void setLevel(Long level) {
        setNumber(LEVEL, level);
    }

    public Long getPeriodId() {
        return getNumber(PERIOD);
    }

    public void setPeriodId(Long periodId) {
        setNumber(PERIOD, periodId);
    }

    public List<WorkerNode> getWorkerNodes() {
        return workerNodes;
    }

    public void setWorkerNodes(List<WorkerNode> workerNodes) {
        this.workerNodes = workerNodes;
    }
}
