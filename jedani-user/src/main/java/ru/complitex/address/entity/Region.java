package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:06
 */
public class Region extends Domain{
    public static final long ENTITY_ID = 2;

    public static final long SHORT_NAME = 1;
    public static final long NAME = 2;
    public static final long MANAGER_ID = 3;

    public Region() {
        setEntityName("region");
    }

    public Region(String externalId){
        this();
        setExternalId(externalId);
    }
}
