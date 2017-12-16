package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:06
 */
public class Region extends Domain{
    public static final Long SHORT_NAME = 1L;
    public static final Long NAME = 2L;
    public static final Long MANAGER_ID = 3L;

    public Region() {
        addAttribute(SHORT_NAME);
        addAttribute(NAME);
        addAttribute(MANAGER_ID, ValueType.INTEGER);

        setEntityName("region");
    }

    public Region(String externalId){
        this();

        setExternalId(externalId);
    }
}
