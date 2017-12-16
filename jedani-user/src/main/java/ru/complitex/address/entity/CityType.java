package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:47
 */
public class CityType extends Domain{
    public static final Long SHORT_NAME = 1L;
    public static final Long NAME = 2L;

    public CityType() {
        setEntityName("city_type");
    }
}
