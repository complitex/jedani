package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 07.05.2018 11:59
 */
public class Position extends Domain {
    public static final String ENTITY_NAME = "position";

    public static final long NAME = 1;

    public static final Long POSITION_REGIONAL_LEADER = 3L;

    public Position() {
        super(ENTITY_NAME);
    }
}
