package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:06
 */
public class Region extends Domain{
    public static final Long ENTITY_ID = 2L;

    public static final Long SHORT_NAME = 1L;
    public static final Long NAME = 2L;
    public static final Long MANAGER_ID = 3L;

    public Region() {
        setEntityName("region");
    }

    public Region(String externalId){
        this();
        setExternalId(externalId);
    }
}
