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

        setUseDateAttribute(true);
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

    public Payout setDate(Date date) {
        setDate(DATE, date);

        return this;
    }

    public Long getPeriodId() {
        return getNumber(PERIOD);
    }

    public Payout setPeriodId(Long periodId) {
        setNumber(PERIOD, periodId);

        return this;
    }

    public Long getCurrencyId() {
        return getNumber(CURRENCY);
    }

    public Payout setCurrencyId(Long currencyId) {
        setNumber(CURRENCY, currencyId);

        return this;
    }

    public BigDecimal getAmount() {
        return getDecimal(AMOUNT);
    }

    public void setAmount(BigDecimal amount) {
        setDecimal(AMOUNT, amount);
    }
}
