package ru.complitex.jedani.worker.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:00 AM
 */
public class WorkerReward {
    private WorkerNode workerNode;

    private List<WorkerReward> workerRewards = new ArrayList<>();

    private List<Sale> sales = new ArrayList<>();

    private BigDecimal saleVolume = ZERO;

    private BigDecimal paymentVolume = ZERO;

    private BigDecimal yearPaymentVolume = ZERO;

    private Long registrationCount = 0L;

    private Long firstLevelCount = 0L;

    private Long firstLevelPersonalCount = 0L;

    private List<Sale> groupSales = new ArrayList<>();

    private BigDecimal groupSaleVolume = ZERO;
    private BigDecimal groupPaymentVolume = ZERO;

    private List<Sale> structureSales = new ArrayList<>();

    private BigDecimal structureSaleVolume = ZERO;
    private BigDecimal structurePaymentVolume = ZERO;

    private Long groupRegistrationCount = 0L;

    private Long structureManagerCount = 0L;

    private Long rank = 0L;

    private boolean pk;

    public WorkerReward(WorkerNode workerNode) {
        this.workerNode = workerNode;
    }

    public List<WorkerReward> getGroup() {
        return getGroup(this);
    }

    public List<WorkerReward> getGroup(WorkerReward workerReward) {
        List<WorkerReward> group = new ArrayList<>();

        for (WorkerReward r : workerReward.getWorkerRewards()){
            if (!r.isManager()){
                group.add(r);

                group.addAll(getGroup(r));
            }
        }

        return group;
    }

    public List<WorkerReward> getStructureManagers(WorkerReward r){
        List<WorkerReward> managers = new ArrayList<>();

        for (WorkerReward c : r.getWorkerRewards()){
            if (c.isManager()){
                managers.add(c);
            }

            managers.addAll(getStructureManagers(c));
        }

        return managers;
    }

    public List<WorkerReward> getFirstStructureManagers() {
        return getFirstStructureManagers(this);
    }

    public List<WorkerReward> getFirstStructureManagers(WorkerReward r) {
        List<WorkerReward> managers = new ArrayList<>();

        for (WorkerReward c : r.getWorkerRewards()){
            if (c.isManager()){
                managers.add(c);
            } else {
                managers.addAll(getFirstStructureManagers(c));
            }
        }

        return managers;
    }

    public boolean isManager() {
        return rank > 0;
    }

    public WorkerNode getWorkerNode() {
        return workerNode;
    }

    public void setWorkerNode(WorkerNode workerNode) {
        this.workerNode = workerNode;
    }

    public List<WorkerReward> getWorkerRewards() {
        return workerRewards;
    }

    public void setWorkerRewards(List<WorkerReward> workerRewards) {
        this.workerRewards = workerRewards;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
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

    public BigDecimal getYearPaymentVolume() {
        return yearPaymentVolume;
    }

    public void setYearPaymentVolume(BigDecimal yearPaymentVolume) {
        this.yearPaymentVolume = yearPaymentVolume;
    }

    public List<Sale> getGroupSales() {
        return groupSales;
    }

    public void setGroupSales(List<Sale> groupSales) {
        this.groupSales = groupSales;
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

    public List<Sale> getStructureSales() {
        return structureSales;
    }

    public void setStructureSales(List<Sale> structureSales) {
        this.structureSales = structureSales;
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

    public Long getFirstLevelPersonalCount() {
        return firstLevelPersonalCount;
    }

    public void setFirstLevelPersonalCount(Long firstLevelPersonalCount) {
        this.firstLevelPersonalCount = firstLevelPersonalCount;
    }

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public Long getWorkerId(){
        return workerNode.getObjectId();
    }
}
