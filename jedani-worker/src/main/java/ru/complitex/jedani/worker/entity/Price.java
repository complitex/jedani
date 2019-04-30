package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2019 20:24
 */
public class Price extends Domain<Price> {
    public static final String ENTITY_NAME = "price";

    public static final long DATE_BEGIN = 1;
    public static final long DATE_END = 2;
    public static final long PRICE = 3;
    public static final long COUNTRY = 4;


    public Price(){
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }
}
