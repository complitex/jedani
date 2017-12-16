package ru.complitex.address.entity;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;

/**
 * @author Anatoly A. Ivanov
 * 16.12.2017 22:51
 */
public class City extends Domain{
    public static final Long SHORT_NAME = 1L;
    public static final Long NAME = 2L;
    public static final Long MANAGER_ID = 3L;

    public City() {
        addAttribute(SHORT_NAME);
        addAttribute(NAME);
        addAttribute(MANAGER_ID, ValueType.INTEGER);

        setEntityName("city");
    }
}
