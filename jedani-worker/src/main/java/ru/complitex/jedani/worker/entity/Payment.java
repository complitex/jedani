package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

public class Payment extends Domain {
    public static final String ENTITY_NAME = "payment";

    public static final long WORKER = 1;
    public static final long DATE = 2;
    public static final long PERIOD_START = 3;
    public static final long PERIOD_END = 4;
    public static final long AMOUNT = 5;
    public static final long RATE = 6;
    public static final long POINT = 7;
    public static final long CONTRACT = 8;
    public static final long SALE = 9;
    public static final long TYPE = 10;
    public static final long PERIOD = 11;
    public static final long CURRENCY = 12;

    public static final String FILTER_SALE_ID = "saleId";
    public static final String FILTER_SELLER_WORKER_ID = "sellerWorkerId";
    public static final String FILTER_MONTH = "month";
    public static final String FILTER_PERIOD = "period";

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

    public BigDecimal getAmount(){
        return getDecimal(AMOUNT);
    }

    public void setAmount(BigDecimal amount){
        setDecimal(AMOUNT, amount);
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

    public Long getPeriodId(){
        return getNumber(PERIOD);
    }

    public Payment setPeriodId(Long periodId){
        setNumber(PERIOD, periodId);

        return this;
    }

    public Long getCurrencyId() {
        return getNumber(CURRENCY);
    }

    public Payment setCurrencyId(Long currencyId) {
        setNumber(CURRENCY, currencyId);

        return this;
    }
}
