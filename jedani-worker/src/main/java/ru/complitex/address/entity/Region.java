package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:06
 */
public class Region extends Domain{
    public static final String ENTITY_NAME = "region";

    public static final long ENTITY_ID = 2;

    public static final long NAME = 1;
    public static final long SHORT_NAME = 2;

    public static final long MANAGER = 10;

    public static final long IMPORT_ID = 100;
    public static final long IMPORT_MANAGER_ID = 101;

    public Region() {
        super(ENTITY_NAME);
    }

    public String getName() {
        return getTextValue(NAME);
    }

    public Region setName(String name) {
        setText(NAME, name);

        return this;
    }

    public String getShortName() {
        return getTextValue(SHORT_NAME);
    }

    public Region setShortName(String shortName) {
        setText(SHORT_NAME, shortName);

        return this;
    }
}
