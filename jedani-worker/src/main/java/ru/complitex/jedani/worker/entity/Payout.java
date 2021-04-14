package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class Payout extends Domain<Payout> {
    public static final String ENTITY_NAME = "payout";

    public static final long WORKER = 1;
    public static final long DATE = 2;
    public static final long PERIOD = 3;
    public static final long CURRENCY = 4;
    public static final long AMOUNT = 5;

    public Payout() {
        super(ENTITY_NAME);
    }

    public Long getWorkerId() {
        return getNumber(WORKER);
    }

    public void setWorkerId(Long workerId) {
        setNumber(WORKER, workerId);
    }

    public Date getDate() {
        return getDate(DATE);
    }

    public void setDate(Date date) {
        setDate(DATE, date);
    }

    public Long getPeriodId() {
        return getNumber(PERIOD);
    }

    public void setPeriodId(Long periodId) {
        setNumber(PERIOD, periodId);
    }

    public Long getCurrencyId() {
        return getNumber(CURRENCY);
    }

    public void setCurrencyId(Long currencyId) {
        setNumber(CURRENCY, currencyId);
    }

    public BigDecimal getAmount() {
        return getDecimal(AMOUNT);
    }

    public void setAmount(BigDecimal amount) {
        setDecimal(AMOUNT, amount);
    }
}
