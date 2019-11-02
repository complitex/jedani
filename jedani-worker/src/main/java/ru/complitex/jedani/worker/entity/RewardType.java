package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RewardType extends Domain<RewardType> {
    public static final String ENTITY_NAME = "reward_type";

    public static final long TYPE_MYCOOK_SALE = 4L;
    public static final long TYPE_BASE_ASSORTMENT_SALE = 5L;
    public static final long TYPE_PERSONAL_VOLUME = 6L;
    public static final long TYPE_CULINARY_WORKSHOP = 8L;
    public static final long TYPE_MK_MANAGER_BONUS = 9L;

    public static final long NAME = 1;

    public RewardType() {
        super(ENTITY_NAME);
    }
}
