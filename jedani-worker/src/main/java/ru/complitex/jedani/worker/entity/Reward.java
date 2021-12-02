package ru.complitex.jedani.worker.entity;

import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Reward extends Domain {
    public static final String ENTITY_NAME = "reward";

    public static final long DATE = 1;
    public static final long WORKER = 2;
    public static final long POINT = 3;
    public static final long TYPE = 4;
    public static final long RANK = 5;
    public static final long DETAIL = 6;
    public static final long SALE = 7;
    public static final long MONTH = 8;
    public static final long SALE_VOLUME = 9;
    public static final long GROUP_SALE_VOLUME = 10;
    public static final long RATE = 11;
    public static final long DISCOUNT = 12;
    public static final long AMOUNT = 13;
    public static final long PAYMENT_VOLUME = 14;
    public static final long GROUP_PAYMENT_VOLUME = 15;
    public static final long STRUCTURE_SALE_VOLUME = 16;
    public static final long STRUCTURE_PAYMENT_VOLUME = 17;
    public static final long MANAGER = 18;
    public static final long MANAGER_RANK = 19;
    public static final long TOTAL = 20;
    public static final long STATUS = 21;
    public static final long BASE_PRICE = 22;
    public static final long PRICE = 23;
    public final static long PERIOD = 24;
    public final static long CROSS_RATE = 25;
    public final static long ESTIMATED = 26;

    public final static String FILTER_MONTH = "month";
    public final static String FILTER_ACTUAL_MONTH = "actualMonth";
    public final static String FILTER_PERIOD = "period";
    public final static String FILTER_NULL_DETAIL_STATUS = "nullDetailStatus";

    private BigDecimal saleTotal;

    private List<Long> errors;

    public Reward() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Reward(Reward reward, Period period) {
        this();

        setSaleId(reward.getSaleId());
        setWorkerId(reward.getWorkerId());
        setGroupSaleVolume(reward.getGroupSaleVolume());
        setGroupPaymentVolume(reward.getGroupPaymentVolume());
        setRank(reward.getRank());
        setRewardStatus(RewardStatus.CHARGED);
        setTotal(reward.getTotal());
        setDate(Dates.currentDate());
        setMonth(period.getOperatingMonth());
        setPeriodId(period.getObjectId());
    }

    public Date getDate(){
        return getDate(DATE);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public Reward setWorkerId(Long workerId){
        setNumber(WORKER, workerId);

        return this;
    }

    public void setPoint(BigDecimal point){
        setDecimal(POINT, point);
    }

    public BigDecimal getPoint(){
        return getDecimal(POINT);
    }

    public void setTotal(BigDecimal total){
        setDecimal(TOTAL, total);
    }

    public BigDecimal getTotal(){
        return getDecimal(TOTAL);
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public Reward setType(Long type){
        setNumber(TYPE, type);

        return this;
    }

    public void setRank(Long rank){
        setNumber(RANK, rank);
    }

    public String getDetail() {
        return getText(DETAIL);
    }

    public void setDetail(String detail) {
        setText(DETAIL, detail);
    }

    public Long getSaleId() {
        return getNumber(SALE);
    }

    public Reward setSaleId(Long saleId) {
        setNumber(SALE, saleId);

        return this;
    }

    public Reward setMonth(Date month){
        setDate(MONTH, month);

        return this;
    }

    public Date getMonth(){
        return getDate(MONTH);
    }

    public void setSaleVolume(BigDecimal volume){
        setDecimal(SALE_VOLUME, volume);
    }

    public void setPaymentVolume(BigDecimal volume){
        setDecimal(PAYMENT_VOLUME, volume);
    }

    public void setGroupSaleVolume(BigDecimal volume){
        setDecimal(GROUP_SALE_VOLUME, volume);
    }

    public void setGroupPaymentVolume(BigDecimal volume){
        setDecimal(GROUP_PAYMENT_VOLUME, volume);
    }

    public void setStructureSaleVolume(BigDecimal volume){
        setDecimal(STRUCTURE_SALE_VOLUME, volume);
    }

    public void setStructurePaymentVolume(BigDecimal volume){
        setDecimal(STRUCTURE_PAYMENT_VOLUME, volume);
    }

    public BigDecimal getRate(){
        return getDecimal(RATE);
    }

    public void setRate(BigDecimal rate){
        setDecimal(RATE, rate);
    }

    public BigDecimal getDiscount(){
        return getDecimal(DISCOUNT);
    }

    public void setDiscount(BigDecimal discount){
        setDecimal(DISCOUNT, discount);
    }

    public BigDecimal getAmount(){
        return getDecimal(AMOUNT);
    }

    public void setAmount(BigDecimal amount){
        setDecimal(AMOUNT, amount);
    }

    public Long getRank() {
        return getNumber(RANK);
    }

    public Long getManagerId(){
        return getNumber(MANAGER);
    }

    public Reward setManagerId(Long managerId){
        setNumber(MANAGER, managerId);

        return this;
    }

    public Long getManagerRank() {
        return getNumber(MANAGER_RANK);
    }

    public void setManagerRank(Long managerRank){
        setNumber(MANAGER_RANK, managerRank);
    }

    public Long getRewardStatus(){
        return getNumber(STATUS);
    }

    public Reward setRewardStatus(Long rewardStatus){
        setNumber(STATUS, rewardStatus);

        return this;
    }

    public void setBasePrice(BigDecimal basePrice){
        setDecimal(BASE_PRICE, basePrice);
    }

    public BigDecimal getBasePrice(){
        return getDecimal(BASE_PRICE);
    }

    public void setPrice(BigDecimal price){
        setDecimal(PRICE, price);
    }

    public BigDecimal getPrice(){
        return getDecimal(PRICE);
    }

    public Long getPeriodId(){
        return getNumber(PERIOD);
    }

    public Reward setPeriodId(Long periodId){
        setNumber(PERIOD, periodId);

        return this;
    }

    public BigDecimal getSaleVolume() {
        return getDecimal(SALE_VOLUME);
    }

    public BigDecimal getPaymentVolume() {
        return getDecimal(PAYMENT_VOLUME);
    }

    public BigDecimal getGroupSaleVolume() {
        return getDecimal(GROUP_SALE_VOLUME);
    }

    public BigDecimal getGroupPaymentVolume() {
        return getDecimal(GROUP_PAYMENT_VOLUME);
    }

    public BigDecimal getStructureSaleVolume() {
        return getDecimal(STRUCTURE_SALE_VOLUME);
    }

    public BigDecimal getStructurePaymentVolume() {
        return getDecimal(STRUCTURE_PAYMENT_VOLUME);
    }

    public void setCrossRate(BigDecimal crossRate) {
        setDecimal(CROSS_RATE, crossRate);
    }

    public BigDecimal getCrossRate() {
        return getDecimal(CROSS_RATE);
    }

    public Long getEstimatedId() {
        return getNumber(ESTIMATED);
    }

    public void setEstimatedId(Long estimatedId) {
        setNumber(ESTIMATED, estimatedId);
    }

    public BigDecimal getSaleTotal() {
        return saleTotal;
    }

    public void setSaleTotal(BigDecimal saleTotal) {
        this.saleTotal = saleTotal;
    }

    public List<Long> getErrors() {
        return errors;
    }

    public void setErrors(List<Long> errors) {
        this.errors = errors;
    }
}
