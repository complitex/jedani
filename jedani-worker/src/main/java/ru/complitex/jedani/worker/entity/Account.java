package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class Account extends Domain {
    public static final String ENTITY_NAME = "account";

    public static final long WORKER = 1;
    public static final long DATE = 2;
    public static final long PERIOD = 3;
    public static final long CURRENCY = 4;
    public static final long BALANCE = 5;
    public static final long CHARGED = 6;
    public static final long PAID = 7;
    public static final long WITHDRAWN = 8;
    public static final long SPENT = 9;

    public Account() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public Account setWorkerId(Long number){
        setNumber(WORKER, number);

        return this;
    }

    public Date getDate(){
        return getDate(DATE);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Long getPeriodId(){
        return getNumber(PERIOD);
    }

    public Account setPeriodId(Long periodId){
        setNumber(PERIOD, periodId);

        return this;
    }

    public Long getCurrencyId() {
        return getNumber(CURRENCY);
    }

    public Account setCurrencyId(Long currencyId) {
        setNumber(CURRENCY, currencyId);

        return this;
    }

    public BigDecimal getBalance(){
        return getDecimal(BALANCE);
    }

    public void setBalance(BigDecimal balance){
        setDecimal(BALANCE, balance);
    }

    public BigDecimal getCharged(){
        return getDecimal(CHARGED);
    }

    public void setCharged(BigDecimal charged){
        setDecimal(CHARGED, charged);
    }

    public BigDecimal getPaid(){
        return getDecimal(PAID);
    }

    public void setPaid(BigDecimal paid){
        setDecimal(PAID, paid);
    }

    public BigDecimal getWithdrawn(){
        return getDecimal(WITHDRAWN);
    }

    public void setWithdrawn(BigDecimal withdrawn){
        setDecimal(WITHDRAWN, withdrawn);
    }

    public BigDecimal getSpent(){
        return getDecimal(SPENT);
    }

    public void setSpent(BigDecimal spent){
        setDecimal(SPENT, spent);
    }
}
