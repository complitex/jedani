package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2019 1:31 PM
 */
public class RewardParameter extends Domain {
    public static final String ENTITY_NAME = "reward_parameter";

    public static final long DATE_BEGIN = 1;
    public static final long DATE_END = 2;
    public static final long REWARD_TYPE = 3;
    public static final long NAME = 4;
    public static final long VALUE = 5;
    public static final long PARAMETER = 6;

    public RewardParameter() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Date getBegin() {
        return getDate(DATE_BEGIN);
    }

    public RewardParameter setBegin(Date date) {
        setDate(DATE_BEGIN, date);

        return this;
    }

    public Date getEnd() {
        return getDate(DATE_END);
    }

    public RewardParameter setEnd(Date date) {
        setDate(DATE_END, date);

        return this;
    }

    public BigDecimal getValue() {
        return getDecimal(VALUE);
    }

    public Long getParameterId() {
        return getNumber(PARAMETER);
    }

    public RewardParameter setParameterId(Long parameterId) {
        setNumber(PARAMETER, parameterId);

        return this;
    }
}
