package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

public class Payment extends Domain<Payment> {
    public final static String ENTITY_NAME = "payment";

    public final static long WORKER = 1;
    public final static long DATE = 2;
    public final static long PERIOD_START = 3;
    public final static long PERIOD_END = 4;
    public final static long LOCAL = 5;
    public final static long RATE = 6;
    public final static long POINT = 7;
    public final static long CONTRACT = 8;
    public final static long SALE = 9;
    public final static long TYPE = 10;

    public final static String FILTER_SALE_ID = "saleId";
    public final static String FILTER_SELLER_WORKER_ID = "sellerWorkerId";
    public final static String FILTER_MONTH = "month";

    public Payment() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public void setWorkerId(Long workerId){
        setNumber(WORKER, workerId);
    }

    public Date getDate(){
        return getDate(DATE);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Date getPeriodStart(){
        return getDate(PERIOD_START);
    }

    public void setPeriodStart(Date periodStart){
        setDate(PERIOD_START, periodStart);
    }

    public Date getPeriodEnd(){
        return getDate(PERIOD_END);
    }

    public void setPeriodEnd(Date periodEnd){
        setDate(PERIOD_END, periodEnd);
    }

    public BigDecimal getLocal(){
        return getDecimal(LOCAL);
    }

    public void setLocal(BigDecimal local){
        setDecimal(LOCAL, local);
    }

    public void setRate(BigDecimal rate){
        setDecimal(RATE, rate);
    }

    public void setPoint(BigDecimal point){
        setDecimal(POINT, point);
    }

    public BigDecimal getPoint(){
        return getDecimal(POINT);
    }

    public String getContract(){
        return getText(CONTRACT);
    }

    public Payment setContract(String contract){
        setText(CONTRACT, contract);

        return this;
    }

    public Long getSaleId(){
        return getNumber(SALE);
    }

    public Payment setSaleId(Long saleId){
        setNumber(SALE, saleId);

        return this;
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public void setType(Long type){
        setNumber(TYPE, type);
    }
}
