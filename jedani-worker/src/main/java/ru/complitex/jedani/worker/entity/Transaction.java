package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class Transaction extends Domain<Transaction> {
    public static final String ENTITY_NAME = "transaction";

    public final static long DATE = 1;
    public final static long PERIOD = 2;
    public final static long ACCOUNT_FROM = 3;
    public final static long ACCOUNT_TO = 4;
    public final static long TYPE = 5;
    public final static long POINT = 6;
    public final static long LOCAL = 7;

    public Date getDate() {
        return getDate(DATE);
    }

    public void setDate(Date date) {
        setDate(DATE, date);
    }

    public Long getPeriod() {
        return getNumber(PERIOD);
    }

    public void setPeriodId(Long periodId) {
        setNumber(PERIOD, periodId);
    }

    public Long getAccountFrom() {
        return getNumber(ACCOUNT_FROM);
    }

    public void setAccountFrom(Long accountFrom) {
        setNumber(ACCOUNT_FROM, accountFrom);
    }

    public Long getAccountTo() {
        return getNumber(ACCOUNT_TO);
    }

    public void setAccountTo(Long accountTo) {
        setNumber(ACCOUNT_TO, accountTo);
    }

    public Long getType() {
        return getNumber(TYPE);
    }

    public void setType(Long type) {
        setNumber(TYPE, type);
    }

    public BigDecimal getPoint() {
        return getDecimal(POINT);
    }

    public void setPoint(BigDecimal point) {
        setDecimal(POINT, point);
    }

    public BigDecimal getLocal() {
        return getDecimal(LOCAL);
    }

    public void setLocal(BigDecimal local) {
        setDecimal(LOCAL, local);
    }
}
