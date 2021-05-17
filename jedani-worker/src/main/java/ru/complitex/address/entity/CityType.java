package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:47
 */
public class CityType extends Domain{
    public static final String ENTITY_NAME = "city_type";

    public static final long ENTITY_ID = 3;

    public static final long NAME = 1;
    public static final long SHORT_NAME = 2;

    public CityType() {
        super(ENTITY_NAME);
    }
}
