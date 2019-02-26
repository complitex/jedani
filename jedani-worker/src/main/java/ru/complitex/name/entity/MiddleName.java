package ru.complitex.name.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 16:34
 */
public class MiddleName extends Domain<MiddleName>{
    public final static String ENTITY_NAME = "middle_name";

    public static final long ENTITY_ID = 12;

    public static final long NAME = 1;

    public MiddleName() {
        super(ENTITY_NAME);
    }
}
