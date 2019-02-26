package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 07.05.2018 11:59
 */
public class Position extends Domain<Position> {
    public static final String ENTITY_NAME = "position";

    public static final long NAME = 1;

    public Position() {
        super(ENTITY_NAME);
    }
}
