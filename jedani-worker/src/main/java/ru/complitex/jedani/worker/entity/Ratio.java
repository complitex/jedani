package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Ivanov Anatoliy
 */
public class Ratio extends Domain {
    public static final String ENTITY_NAME = "ratio";

    public static final long BEGIN = 1;
    public static final long END = 2;
    public static final long COUNTRY = 3;
    public static final long VALUE = 4;

    public Ratio() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Date getBegin() {
        return getDate(BEGIN);
    }

    public Ratio setBegin(Date begin) {
        setDate(BEGIN, begin);

        return this;
    }

    public Date getEnd() {
        return getDate(END);
    }

    public Ratio setEnd(Date end) {
        setDate(END, end);

        return this;
    }

    public Long getCountryId() {
        return getNumber(COUNTRY);
    }

    public Ratio setCountryId(Long countryId) {
        setNumber(COUNTRY, countryId);

        return this;
    }

    public BigDecimal getValue() {
        return getDecimal(VALUE);
    }
}
