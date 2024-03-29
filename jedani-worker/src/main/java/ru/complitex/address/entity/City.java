package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:51
 */
public class City extends Domain{
    public static final String ENTITY_NAME = "city";

    public static final long ENTITY_ID = 4;

    public static final long NAME = 1;
    public static final long SHORT_NAME = 2;
    public static final long CITY_TYPE = 3;

    public static final long MANAGER = 10;

    public static final long IMPORT_ID = 100;
    public static final long IMPORT_MANAGER_ID = 101;

    public City() {
        super(ENTITY_NAME);
    }

    public String getName() {
        return getTextValue(NAME);
    }

    public City setName(String name) {
        setText(NAME, name);

        return this;
    }

    public String getShortName() {
        return getTextValue(SHORT_NAME);
    }

    public City setShortName(String shortName) {
        setText(SHORT_NAME, shortName);

        return this;
    }

    public Long getCityTypeId() {
        return getNumber(CITY_TYPE);
    }
}
