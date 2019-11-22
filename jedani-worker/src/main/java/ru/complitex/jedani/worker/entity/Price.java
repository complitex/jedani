package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;

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

    public static final String FILTER_DATE = "date";

    public Price(){
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getNomenclatureId(){
        return getParentId();
    }

    public Price setNomenclatureId(Long nomenclatureId){
        setParentId(nomenclatureId);

        return this;
    }

    public BigDecimal getPrice(){
        return getDecimal(PRICE);
    }

    public void setPrice(BigDecimal price){
        setDecimal(PRICE, price);
    }

    public Long getCountyId(){
        return getNumber(COUNTRY);
    }

    public Price setCountryId(Long countryId){
        setNumber(COUNTRY, countryId);

        return this;
    }
}
