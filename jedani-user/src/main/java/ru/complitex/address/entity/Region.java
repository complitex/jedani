package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:06
 */
public class Region extends Domain{
    public static final Integer SHORT_NAME = 1;
    public static final Integer NAME = 2;

    @Override
    public String getEntityName() {
        return "region";
    }
}
