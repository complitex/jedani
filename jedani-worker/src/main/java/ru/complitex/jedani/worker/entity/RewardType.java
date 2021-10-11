package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RewardType extends Domain {
    public static final String ENTITY_NAME = "reward_type";

    public static final long PERSONAL_MYCOOK = 4;
    public static final long PERSONAL_RANGE = 5;
    public static final long PERSONAL_VOLUME = 6;
    public static final long CULINARY_WORKSHOP = 8;
    public static final long MANAGER_BONUS = 9;
    public static final long MANAGER_PREMIUM = 10;
    public static final long GROUP_VOLUME = 11;
    public static final long STRUCTURE_VOLUME = 12;
    public static final long RANK = 13;

    public static final long NAME = 1;

    public RewardType() {
        super(ENTITY_NAME);
    }
}
