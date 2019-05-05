package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 14:35
 */
public class SaleItem extends Domain<SaleItem> {
    public static final String ENTITY_NAME = "sale_item";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long PRICE = 3;

    public static final String FILTER_DATE = "date";
    public static final String FILTER_BUYER = "buyer";
    public static final String FILTER_STORAGE = "storage";
    public static final String FILTER_INSTALLMENT_PERCENTAGE = "installmentPercentage";
    public static final String FILTER_INSTALLMENT_MONTHS = "installmentMonths";
    public static final String FILTER_SELLER_WORKER = "sellerWorker";
    public static final String FILTER_REGION_IDS = "regionIds";

    public SaleItem() {
        super(ENTITY_NAME);
    }

    public Long getNomenclatureId(){
        return getNumber(NOMENCLATURE);
    }

    public void setNomenclatureId(Long nomenclatureId){
        setNumber(NOMENCLATURE, nomenclatureId);
    }

    public Long getQuantity(){
        return getNumber(QUANTITY);
    }

    public BigDecimal getPrice(){
        return getDecimal(PRICE);
    }

    public void setPrice(BigDecimal price){
        setDecimal(PRICE, price);
    }
}
