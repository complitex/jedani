package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:51
 */
public class City extends Domain{
    public static final Long SHORT_NAME = 1L;
    public static final Long NAME = 2L;
    public static final Long MANAGER_ID = 3L;

    public City() {
        setEntityName("city");
    }
}
