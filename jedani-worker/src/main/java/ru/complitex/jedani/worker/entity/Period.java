package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2019 8:26 PM
 */
public class Period extends Domain<Period> {
    public static final String ENTITY_NAME = "period";

    public static final long OPERATING_MONTH = 1;
    public static final long PERIOD_START = 2;
    public static final long PERIOD_END = 3;
    public static final long PERIOD_CLOSE = 4;
    public static final long WORKER = 5;

    public Period() {
        super(ENTITY_NAME);
    }

    public void setOperatingMonth(Date operatingMonth){
        setDate(OPERATING_MONTH, operatingMonth);
    }

    public void setPeriodStart(Date periodStart){
        setDate(PERIOD_START, periodStart);
    }

    public void setPeriodEnd(Date periodEnd){
        setDate(PERIOD_END, periodEnd);
    }

    public void setPeriodClose(Date periodClose){
        setDate(PERIOD_CLOSE, periodClose);
    }
}
