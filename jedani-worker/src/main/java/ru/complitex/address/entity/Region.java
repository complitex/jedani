package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:06
 */
public class Region extends Domain<Region>{
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
}
