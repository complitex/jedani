package ru.complitex.jedani.worker.entity;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:00 AM
 */
public class WorkerNode {
    private Long objectId;
    private Long managerId;

    private Long left;
    private Long right;
    private Long level;

    private List<WorkerNode> childNodes = new ArrayList<>();

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getLeft() {
        return left;
    }

    public void setLeft(Long left) {
        this.left = left;
    }

    public Long getRight() {
        return right;
    }

    public void setRight(Long right) {
        this.right = right;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public List<WorkerNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<WorkerNode> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("objectId", objectId)
                .add("managerId", managerId)
                .add("left", left)
                .add("right", right)
                .add("level", level)
                .toString();
    }
}
