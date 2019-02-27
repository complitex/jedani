package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 20:25
 */
public class Currency extends Domain<Currency> {
    public static final String ENTITY_NAME = "currency";

    public static final long NAME = 1;
    public static final long CODE = 2;

    public Currency() {
        super(ENTITY_NAME);
    }
}
