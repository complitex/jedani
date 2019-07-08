package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RewardType extends Domain<RewardType> {
    public static final String ENTITY_NAME = "reward_type";

    public static final long NAME = 1;

    public RewardType() {
        super(ENTITY_NAME);
    }
}
