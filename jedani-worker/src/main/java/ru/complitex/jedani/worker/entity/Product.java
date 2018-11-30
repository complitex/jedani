package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.10.2018 16:03
 */
public class Product extends Domain {
    public static final String ENTITY_NAME = "product";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long SENT = 3;
    public static final long RECEIVED = 4;
    public static final long GIFT_QUANTITY = 5;
    public static final long GIFT_SENT = 6;
    public static final long GIFT_RECEIVED = 7;

    public Product() {
        super(ENTITY_NAME);
    }
}
