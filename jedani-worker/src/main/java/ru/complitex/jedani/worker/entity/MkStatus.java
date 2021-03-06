package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 07.05.2018 11:55
 */
public class MkStatus extends Domain {
    public static final String ENTITY_NAME = "mk_status";

    public static final long NAME = 1;

    public static final long STATUS_PROMO = 1;
    public static final long STATUS_JUST = 2;
    public static final long STATUS_VIP = 3;

    public MkStatus() {
        super(ENTITY_NAME);
    }
}
