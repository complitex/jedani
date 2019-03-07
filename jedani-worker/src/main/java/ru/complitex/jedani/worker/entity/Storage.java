package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.10.2018 16:01
 */
public class Storage extends Domain<Storage> {
    public static final String ENTITY_NAME = "storage";

    public static final long CITY = 1;
    public static final long WORKERS = 2;
    public static final long TYPE = 3;

    public static final String FILTER_CITIES = "cities";
    public static final String FILTER_CURRENT_WORKER = "currentWorker";
    public static final String FILTER_WORKERS = "workers";
    public static final String FILTER_WORKER = "worker";
    public static final String FILTER_NOMENCLATURE_COUNT = "nomenclatureCount";
    public static final String FILTER_TRANSACTION_COUNT = "transactionCount";
    public static final String FILTER_OBJECT_ID = "id";

    private Long nomenclatureCount;
    private Long transactionCount;

    public Storage() {
        super(ENTITY_NAME);

        setUseNumberValue(true);
    }

    public Long getCityId(){
        return getNumber(CITY);
    }

    public void setCityId(Long cityId){
        setNumber(CITY, cityId);
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public void setType(Long type){
        setNumber(TYPE, type);
    }

    public Long getNomenclatureCount() {
        return nomenclatureCount;
    }

    public void setNomenclatureCount(Long nomenclatureCount) {
        this.nomenclatureCount = nomenclatureCount;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }
}
