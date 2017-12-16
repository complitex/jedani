package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:47
 */
public class CityType extends Domain{
    public static final Integer SHORT_NAME = 1;
    public static final Integer NAME = 2;

    @Override
    public String getEntityName() {
        return "city_type";
    }

}
