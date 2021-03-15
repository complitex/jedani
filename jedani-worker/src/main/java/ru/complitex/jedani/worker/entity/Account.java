package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class Account extends Domain<Account> {
    public static final String ENTITY_NAME = "account";

    public static final long WORKER = 1;
    public static final long DATE = 2;
    public static final long PERIOD = 3;
    public static final long BALANCE = 4;
    public static final long BALANCE_LOCAL = 5;
    public static final long ESTIMATED = 6;
    public static final long ESTIMATED_LOCAL = 7;
    public static final long CHARGED = 8;
    public static final long CHARGED_LOCAL = 9;
    public static final long PAID = 10;
    public static final long PAID_LOCAL = 11;
    public static final long WITHDRAWN = 12;
    public static final long WITHDRAWN_LOCAL = 13;

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

    public Long getPeriodId(){
        return getNumber(PERIOD);
    }

    public void setPeriodId(Long periodId){
        setNumber(PERIOD, periodId);
    }

    public BigDecimal getBalance(){
        return getDecimal(BALANCE);
    }

    public void setBalance(BigDecimal balance){
        setDecimal(BALANCE, balance);
    }

    public BigDecimal getBalanceLocal(){
        return getDecimal(BALANCE_LOCAL);
    }

    public void setBalanceLocal(BigDecimal balanceLocal){
        setDecimal(BALANCE_LOCAL, balanceLocal);
    }

    public BigDecimal getEstimated(){
        return getDecimal(ESTIMATED);
    }

    public void setEstimated(BigDecimal estimated){
        setDecimal(ESTIMATED, estimated);
    }

    public BigDecimal getEstimatedLocal(){
        return getDecimal(ESTIMATED_LOCAL);
    }

    public void setEstimatedLocal(BigDecimal estimatedLocal){
        setDecimal(ESTIMATED_LOCAL, estimatedLocal);
    }

    public BigDecimal getCharged(){
        return getDecimal(CHARGED);
    }

    public void setCharged(BigDecimal charged){
        setDecimal(CHARGED, charged);
    }

    public BigDecimal getChargedLocal(){
        return getDecimal(CHARGED_LOCAL);
    }

    public void setChargedLocal(BigDecimal chargedLocal){
        setDecimal(CHARGED_LOCAL, chargedLocal);
    }

    public BigDecimal getPaid(){
        return getDecimal(PAID);
    }

    public void setPaid(BigDecimal paid){
        setDecimal(PAID, paid);
    }

    public BigDecimal getPaidLocal(){
        return getDecimal(PAID_LOCAL);
    }

    public void setPaidLocal(BigDecimal paidLocal){
        setDecimal(PAID_LOCAL, paidLocal);
    }

    public BigDecimal getWithdrawn(){
        return getDecimal(WITHDRAWN);
    }

    public void setWithdrawn(BigDecimal withdrawn){
        setDecimal(WITHDRAWN, withdrawn);
    }

    public BigDecimal getWithdrawnLocal(){
        return getDecimal(WITHDRAWN_LOCAL);
    }

    public void setWithdrawnLocal(BigDecimal withdrawnLocal){
        setDecimal(WITHDRAWN_LOCAL, withdrawnLocal);
    }
}
