package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2019 11:46 PM
 */
public class Rate extends Domain<Rate> {
    public static final String ENTITY_VALUE = "rate";

    public static final long DATE = 1;
    public static final long RATE = 2;

    public Rate() {
        super(ENTITY_VALUE);

        setUseDateAttribute(true);
    }

    public Date getDate(){
        return getDate(DATE);
    }

    public Rate setDate(Date date){
        setDate(DATE, date);

        return this;
    }

    public BigDecimal getRate(){
        return getDecimal(RATE);
    }

    public void setRate(BigDecimal rate){
        setDecimal(RATE, rate);
    }
}
