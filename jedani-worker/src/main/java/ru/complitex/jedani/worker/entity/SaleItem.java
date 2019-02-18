package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 14:35
 */
public class SaleItem extends Domain {
    public static final String ENTITY_NAME = "sale_item";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long PRICE = 3;
    public static final long STORAGE = 4;
    public static final long INSTALLMENT_PERCENTAGE = 5;
    public static final long INSTALLMENT_MONTHS = 6;

    public SaleItem() {
        super(ENTITY_NAME);
    }
}
