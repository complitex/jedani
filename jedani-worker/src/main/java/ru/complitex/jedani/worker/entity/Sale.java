package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

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
    public static final long PROMOTION = 8;
    public static final long STORAGE = 9;
    public static final long CONTRACT = 10;
    public static final long TOTAL = 11;
    public static final long TOTAL_LOCAL = 12;
    public static final long INSTALLMENT_MONTHS = 13;
    public static final long INITIAL_PAYMENT = 14;

    public Sale() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getSellerWorkerId(){
        return getNumber(SELLER_WORKER);
    }

    public void setSellerWorkerId(Long sellerWorkerId){
        setNumber(SELLER_WORKER, sellerWorkerId);
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

    public Date getDate(){
        return getDate(DATE);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public void setType(Long type){
        setNumber(TYPE, type);
    }

    public Long getInstallmentMonths(){
        return getNumber(INSTALLMENT_MONTHS);
    }

    public void setInstallmentMonths(Long months){
        setNumber(INSTALLMENT_MONTHS, months);
    }

    public Long getStorageId(){
        return getNumber(STORAGE);
    }

    public void setStorageId(Long storageId){
        setNumber(STORAGE, storageId);
    }

    public void setTotalLocal(BigDecimal total){
        setDecimal(TOTAL_LOCAL, total);
    }

    public BigDecimal getTotalLocal(){
        return getDecimal(TOTAL_LOCAL);
    }

    public BigDecimal getInitialPayment(){
        return getDecimal(INITIAL_PAYMENT);
    }

    public void setInitialPayment(BigDecimal initialPayment){
        setDecimal(INITIAL_PAYMENT, initialPayment);
    }

    public void setTotal(BigDecimal total){
        setDecimal(TOTAL, total);
    }

    public BigDecimal getTotal(){
        return getDecimal(TOTAL);
    }

    public String getContract(){
        return getText(CONTRACT);
    }
}
