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
    public static final long SENDING = 3;
    public static final long RECEIVING = 4;
    public static final long GIFT_QUANTITY = 5;
    public static final long GIFT_SENDING = 6;
    public static final long GIFT_RECEIVING = 7;
    public static final long RESERVE = 8;

    public Product() {
        super(ENTITY_NAME);
    }

    public Long getAvailableQuantity(){
        return getNumber(QUANTITY) - getNumber(RESERVE, 0L);
    }
}
