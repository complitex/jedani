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
    public static final long CLOSE_TIMESTAMP = 2;
    public static final long WORKER = 3;

    public Period() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Date getOperatingMonth(){
        return getDate(OPERATING_MONTH);
    }

    public void setOperatingMonth(Date operatingMonth){
        setDate(OPERATING_MONTH, operatingMonth);
    }

    public void setCloseTimestamp(Date closeTimestamp){
        setDate(CLOSE_TIMESTAMP, closeTimestamp);
    }

    public Date getCloseTimestamp(){
        return getDate(CLOSE_TIMESTAMP);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public void setWorkerId(Long workerId){
        setNumber(WORKER, workerId);
    }
}
