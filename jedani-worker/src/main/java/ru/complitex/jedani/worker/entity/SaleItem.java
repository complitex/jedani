package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 14:35
 */
public class SaleItem extends Domain<SaleItem> {
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

    public Long getNomenclatureId(){
        return getNumber(NOMENCLATURE);
    }

    public void setNomenclatureId(Long nomenclatureId){
        setNumber(NOMENCLATURE, nomenclatureId);
    }

    public Long getQuantity(){
        return getNumber(QUANTITY);
    }

    public Long getStorageId(){
        return getNumber(STORAGE);
    }

    public void setStorageId(Long storageId){
        setNumber(STORAGE, storageId);
    }

    public Long getInstallmentPercentage(){
        return getNumber(INSTALLMENT_PERCENTAGE);
    }

    public void setInstallmentPercentage(Long percentage){
        setNumber(INSTALLMENT_PERCENTAGE, percentage);
    }

    public Long getInstallmentMonths(){
        return getNumber(INSTALLMENT_MONTHS);
    }

    public void setInstallmentMonths(Long months){
        setNumber(INSTALLMENT_MONTHS, months);
    }
}
