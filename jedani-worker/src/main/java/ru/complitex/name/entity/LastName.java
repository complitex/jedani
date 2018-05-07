package ru.complitex.name.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 16:34
 */
public class LastName extends Domain{
    public static final String ENTITY_NAME = "last_name";

    public static final long ENTITY_ID = 13;

    public static final long NAME = 1;

    public LastName() {
        super(ENTITY_NAME);
    }
}
