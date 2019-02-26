package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 14:19
 */
public class Sale extends Domain<Sale> {
    public static final String ENTITY_NAME = "sale";

    public static final long SELLER_WORKER = 1;
    public static final long BUYER_FIRST_NAME = 2;
    public static final long BUYER_MIDDLE_NAME = 3;
    public static final long BUYER_LAST_NAME = 4;
    public static final long DATE = 5;
    public static final long TYPE = 6;
    public static final long SAS_REQUEST = 7;

    public Sale() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getBuyerFirstName(){
        return getNumber(BUYER_FIRST_NAME);
    }

    public void setBuyerFirstName(Long firstNameId){
        setNumber(BUYER_FIRST_NAME, firstNameId);
    }

    public Long getBuyerMiddleName(){
        return getNumber(BUYER_MIDDLE_NAME);
    }

    public void setBuyerMiddleName(Long middleNameId){
        setNumber(BUYER_MIDDLE_NAME, middleNameId);
    }

    public Long getBuyerLastName(){
        return getNumber(BUYER_LAST_NAME);
    }

    public void setBuyerLastName(Long lastNameId){
        setNumber(BUYER_LAST_NAME, lastNameId);
    }
}
