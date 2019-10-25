package ru.complitex.jedani.worker.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:00 AM
 */
public class WorkerReward {
    private WorkerNode workerNode;

    private List<WorkerReward> childRewards = new ArrayList<>();

    private BigDecimal saleVolume = BigDecimal.ZERO;
    private BigDecimal paymentVolume = BigDecimal.ZERO;

    private BigDecimal groupSaleVolume = BigDecimal.ZERO;
    private BigDecimal groupPaymentVolume = BigDecimal.ZERO;

    public WorkerReward(WorkerNode workerNode) {
        this.workerNode = workerNode;
    }

    public WorkerNode getWorkerNode() {
        return workerNode;
    }

    public void setWorkerNode(WorkerNode workerNode) {
        this.workerNode = workerNode;
    }

    public List<WorkerReward> getChildRewards() {
        return childRewards;
    }

    public void setChildRewards(List<WorkerReward> childRewards) {
        this.childRewards = childRewards;
    }

    public BigDecimal getSaleVolume() {
        return saleVolume;
    }

    public void setSaleVolume(BigDecimal saleVolume) {
        this.saleVolume = saleVolume;
    }

    public BigDecimal getPaymentVolume() {
        return paymentVolume;
    }

    public void setPaymentVolume(BigDecimal paymentVolume) {
        this.paymentVolume = paymentVolume;
    }

    public BigDecimal getGroupSaleVolume() {
        return groupSaleVolume;
    }

    public void setGroupSaleVolume(BigDecimal groupSaleVolume) {
        this.groupSaleVolume = groupSaleVolume;
    }

    public BigDecimal getGroupPaymentVolume() {
        return groupPaymentVolume;
    }

    public void setGroupPaymentVolume(BigDecimal groupPaymentVolume) {
        this.groupPaymentVolume = groupPaymentVolume;
    }
}
