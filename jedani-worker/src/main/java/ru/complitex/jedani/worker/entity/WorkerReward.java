package ru.complitex.jedani.worker.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.*;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:00 AM
 */
public class WorkerReward {
    private WorkerNode workerNode;

    private List<WorkerReward> childRewards = new ArrayList<>();

    private BigDecimal saleVolume = ZERO;
    private BigDecimal paymentVolume = ZERO;

    private Long registrationCount = 0L;

    private Long firstLevelCount = 0L;

    private BigDecimal groupSaleVolume = ZERO;
    private BigDecimal groupPaymentVolume = ZERO;

    private BigDecimal structureSaleVolume = ZERO;
    private BigDecimal structurePaymentVolume = ZERO;

    private Long groupRegistrationCount = 0L;

    private Long structureManagerCount = 0L;

    private Long rank = 0L;

    private List<Reward> rewards = new ArrayList<>();

    public boolean isManager() {
        return rank > 0;
    }

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

    public BigDecimal getStructureSaleVolume() {
        return structureSaleVolume;
    }

    public void setStructureSaleVolume(BigDecimal structureSaleVolume) {
        this.structureSaleVolume = structureSaleVolume;
    }

    public BigDecimal getStructurePaymentVolume() {
        return structurePaymentVolume;
    }

    public void setStructurePaymentVolume(BigDecimal structurePaymentVolume) {
        this.structurePaymentVolume = structurePaymentVolume;
    }

    public Long getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(Long registrationCount) {
        this.registrationCount = registrationCount;
    }

    public Long getGroupRegistrationCount() {
        return groupRegistrationCount;
    }

    public void setGroupRegistrationCount(Long groupRegistrationCount) {
        this.groupRegistrationCount = groupRegistrationCount;
    }

    public Long getStructureManagerCount() {
        return structureManagerCount;
    }

    public void setStructureManagerCount(Long structureManagerCount) {
        this.structureManagerCount = structureManagerCount;
    }

    public Long getFirstLevelCount() {
        return firstLevelCount;
    }

    public void setFirstLevelCount(Long firstLevelCount) {
        this.firstLevelCount = firstLevelCount;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Long getWorkerId(){
        return workerNode.getObjectId();
    }
}
