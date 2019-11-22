package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2019 1:31 PM
 */
public class RewardParameter extends Domain<RewardParameter> {
    public static final String ENTITY_NAME = "reward_parameter";

    public static final long DATE_BEGIN = 1;
    public static final long DATE_END = 2;
    public static final long REWARD_TYPE = 3;
    public static final long NAME = 4;
    public static final long VALUE = 5;

    public RewardParameter() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }
}
