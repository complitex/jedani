package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:51
 */
public class City extends Domain{
    public static final long ENTITY_ID = 4;

    public static final long NAME = 1;
    public static final long SHORT_NAME = 2;
    public static final long MANAGER_ID = 3;

    public City() {
        super("city");
    }
}
