package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

public class Reward extends Domain<Reward> {
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
    public static final long LOCAL = 13;
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

    public final static String FILTER_MONTH = "month";
    public final static String FILTER_ACTUAL_MONTH = "actualMonth";

    public Reward() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
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

    public void setType(Long type){
        setNumber(TYPE, type);
    }

    public void setRankId(Long rankId){
        setNumber(RANK, rankId);
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

    public BigDecimal getLocal(){
        return getDecimal(LOCAL);
    }

    public void setLocal(BigDecimal local){
        setDecimal(LOCAL, local);
    }

    public Long getManagerId(){
        return getNumber(MANAGER);
    }

    public Reward setManagerId(Long managerId){
        setNumber(MANAGER, managerId);

        return this;
    }

    public void setManagerRankId(Long managerRankId){
        setNumber(MANAGER_RANK, managerRankId);
    }

    public Long getRewardStatus(){
        return getNumber(STATUS);
    }

    public void setRewardStatus(Long rewardStatus){
        setNumber(STATUS, rewardStatus);
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
}
