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
    public static final long PERSONAL_SALE_VOLUME = 9;
    public static final long GROUP_SALE_VOLUME = 10;
    public static final long RATE = 11;
    public static final long DISCOUNT = 12;
    public static final long LOCAL = 13;
    public static final long PERSONAL_PAYMENT_VOLUME = 14;
    public static final long GROUP_PAYMENT_VOLUME = 15;
    public static final long STRUCTURE_SALE_VOLUME = 16;
    public static final long STRUCTURE_PAYMENT_VOLUME = 17;

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

    public void setPersonalSaleVolume(BigDecimal volume){
        setDecimal(PERSONAL_SALE_VOLUME, volume);
    }

    public void setGroupSaleVolume(BigDecimal volume){
        setDecimal(GROUP_SALE_VOLUME, volume);
    }

    public void setGroupPaymentVolume(BigDecimal volume){
        setDecimal(GROUP_PAYMENT_VOLUME, volume);
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
}
