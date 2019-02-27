package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 15.06.2018 17:07
 */
public class Country extends Domain<Country> {
    public static final String ENTITY_NAME = "country";

    public static final long ENTITY_ID = 1;

    public static final long NAME = 1;
    public static final long SHORT_NAME = 2;
    public static final long CURRENCY = 3;
    public static final long EXCHANGE_RATE_EUR = 4;

    public Country() {
        super(ENTITY_NAME);
    }
}
