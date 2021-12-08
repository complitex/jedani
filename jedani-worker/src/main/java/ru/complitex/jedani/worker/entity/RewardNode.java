package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.math.BigDecimal.ZERO;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2019 11:00 AM
 */
public class RewardNode extends Domain {
    public static final String ENTITY_NAME = "reward_node";

    public static final long WORKER = 1;
    public static final long SALE_VOLUME = 2;
    public static final long PAYMENT_VOLUME = 3;
    public static final long YEAR_PAYMENT_VOLUME = 4;
    public static final long GROUP_SALE_VOLUME = 5;
    public static final long GROUP_PAYMENT_VOLUME = 6;
    public static final long STRUCTURE_SALE_VOLUME = 7;
    public static final long STRUCTURE_PAYMENT_VOLUME = 8;
    public static final long WORKER_STATUS = 9;
    public static final long PK = 10;
    public static final long FIRST_LEVEL_COUNT = 11;
    public static final long FIRST_LEVEL_PERSONAL_COUNT = 12;
    public static final long REGISTRATION_COUNT = 13;
    public static final long GROUP_REGISTRATION_COUNT = 14;
    public static final long STRUCTURE_MANAGER_COUNT = 15;
    public static final long RANK = 16;
    public static final long PERIOD = 17;

    private WorkerNode workerNode;

    private Date registrationDate;

    private List<Sale> sales = new ArrayList<>();

    private List<Sale> groupSales = new ArrayList<>();

    private List<Sale> structureSales = new ArrayList<>();

    private List<Reward> rewards = new ArrayList<>();

    private List<RewardNode> rewardNodes = new ArrayList<>();

    public RewardNode() {
        super(ENTITY_NAME);
    }

    public RewardNode(WorkerNode workerNode) {
        this();

        this.workerNode = workerNode;

        setWorkerId(workerNode.getWorkerId());
    }

    public boolean isNull() {
        return getSaleVolume() == null &&
                getPaymentVolume() == null &&
                getYearPaymentVolume() == null &&
                getGroupSaleVolume() == null &&
                getGroupPaymentVolume() == null &&
                getStructureSaleVolume() == null &&
                getStructurePaymentVolume() == null &&
                getFirstLevelCount() == null &&
                getFirstLevelPersonalCount() == null &&
                getRegistrationCount() == null &&
                getGroupRegistrationCount() == null &&
                getStructureManagerCount() == null &&
                getRank() == null;
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public void setWorkerId(Long workerId) {
        setNumber(WORKER, workerId);
    }

    public BigDecimal getSaleVolume() {
        return getDecimal(SALE_VOLUME, ZERO);
    }

    public void setSaleVolume(BigDecimal saleVolume) {
        setDecimal(SALE_VOLUME, saleVolume);
    }

    public BigDecimal getPaymentVolume() {
        return getDecimal(PAYMENT_VOLUME, ZERO);
    }

    public void setPaymentVolume(BigDecimal paymentVolume) {
        setDecimal(PAYMENT_VOLUME, paymentVolume);
    }

    public BigDecimal getYearPaymentVolume() {
        return getDecimal(YEAR_PAYMENT_VOLUME, ZERO);
    }

    public void setYearPaymentVolume(BigDecimal yearPaymentVolume) {
        setDecimal(YEAR_PAYMENT_VOLUME, yearPaymentVolume);
    }

    public BigDecimal getGroupSaleVolume() {
        return getDecimal(GROUP_SALE_VOLUME, ZERO);
    }

    public void setGroupSaleVolume(BigDecimal groupSaleVolume) {
        setDecimal(GROUP_SALE_VOLUME, groupSaleVolume);
    }

    public BigDecimal getGroupPaymentVolume() {
        return getDecimal(GROUP_PAYMENT_VOLUME, ZERO);
    }

    public void setGroupPaymentVolume(BigDecimal groupPaymentVolume) {
        setDecimal(GROUP_PAYMENT_VOLUME, groupPaymentVolume);
    }

    public BigDecimal getStructureSaleVolume() {
        return getDecimal(STRUCTURE_SALE_VOLUME, ZERO);
    }

    public void setStructureSaleVolume(BigDecimal structureSaleVolume) {
        setDecimal(STRUCTURE_SALE_VOLUME, structureSaleVolume);
    }

    public BigDecimal getStructurePaymentVolume() {
        return getDecimal(STRUCTURE_PAYMENT_VOLUME, ZERO);
    }

    public void setStructurePaymentVolume(BigDecimal structurePaymentVolume) {
        setDecimal(STRUCTURE_PAYMENT_VOLUME, structurePaymentVolume);
    }

    public Long getWorkerStatus() {
        return getNumber(WORKER_STATUS, 0L);
    }

    public void setWorkerStatus(Long workerStatus) {
        setNumber(WORKER_STATUS, workerStatus);
    }

    public boolean isPk() {
        return isBoolean(PK);
    }

    public void setPk(boolean pk) {
        setBoolean(PK, pk);
    }

    public Long getFirstLevelCount() {
        return getNumber(FIRST_LEVEL_COUNT, 0L);
    }

    public void setFirstLevelCount(Long firstLevelCount) {
        setNumber(FIRST_LEVEL_COUNT, firstLevelCount);
    }

    public Long getFirstLevelPersonalCount() {
        return getNumber(FIRST_LEVEL_PERSONAL_COUNT, 0L);
    }

    public void setFirstLevelPersonalCount(Long firstLevelPersonalCount) {
        setNumber(FIRST_LEVEL_PERSONAL_COUNT, firstLevelPersonalCount);
    }

    public Long getRegistrationCount() {
        return getNumber(REGISTRATION_COUNT, 0L);
    }

    public void setRegistrationCount(Long registrationCount) {
        setNumber(REGISTRATION_COUNT, registrationCount);
    }

    public Long getGroupRegistrationCount() {
        return getNumber(GROUP_REGISTRATION_COUNT, 0L);
    }

    public void setGroupRegistrationCount(Long groupRegistrationCount) {
        setNumber(GROUP_REGISTRATION_COUNT, groupRegistrationCount);
    }

    public Long getStructureManagerCount() {
        return getNumber(STRUCTURE_MANAGER_COUNT, 0L);
    }

    public void setStructureManagerCount(Long structureManagerCount) {
        setNumber(STRUCTURE_MANAGER_COUNT, structureManagerCount);
    }

    public Long getRank() {
        return getNumber(RANK, 0L);
    }

    public void setRank(Long rank) {
        setNumber(RANK, rank);
    }

    public Long getPeriodId() {
        return getNumber(PERIOD);
    }

    public void setPeriodId(Long periodId) {
        setNumber(PERIOD, periodId);
    }

    public WorkerNode getWorkerNode() {
        return workerNode;
    }

    public void setWorkerNode(WorkerNode workerNode) {
        this.workerNode = workerNode;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public List<Sale> getGroupSales() {
        return groupSales;
    }

    public void setGroupSales(List<Sale> groupSales) {
        this.groupSales = groupSales;
    }

    public List<Sale> getStructureSales() {
        return structureSales;
    }

    public void setStructureSales(List<Sale> structureSales) {
        this.structureSales = structureSales;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
    }

    public List<RewardNode> getRewardNodes() {
        return rewardNodes;
    }

    public void setRewardNodes(List<RewardNode> rewardNodes) {
        this.rewardNodes = rewardNodes;
    }

    public List<RewardNode> getGroup() {
        return getGroup(this);
    }

    public boolean isManager() {
        return getRank() > 0;
    }

    public List<RewardNode> getGroup(RewardNode workerReward) {
        List<RewardNode> group = new ArrayList<>();

        for (RewardNode r : workerReward.getRewardNodes()){
            if (!r.isManager()){
                group.add(r);

                group.addAll(getGroup(r));
            }
        }

        return group;
    }

    public List<RewardNode> getStructureManagers(RewardNode r) {
        List<RewardNode> managers = new ArrayList<>();

        for (RewardNode c : r.getRewardNodes()){
            if (c.isManager()){
                managers.add(c);
            }

            managers.addAll(getStructureManagers(c));
        }

        return managers;
    }

    public List<RewardNode> getFirstStructureManagers() {
        return getFirstStructureManagers(this);
    }

    public List<RewardNode> getFirstStructureManagers(RewardNode r) {
        List<RewardNode> managers = new ArrayList<>();

        for (RewardNode c : r.getRewardNodes()){
            if (c.isManager()){
                managers.add(c);
            } else {
                managers.addAll(getFirstStructureManagers(c));
            }
        }

        return managers;
    }
}
