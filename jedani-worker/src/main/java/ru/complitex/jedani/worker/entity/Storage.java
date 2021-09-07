package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.10.2018 16:01
 */
public class Storage extends Domain {
    public static final String ENTITY_NAME = "storage";

    public static final long CITY = 1;
    public static final long WORKERS = 2;
    public static final long TYPE = 3;
    public static final long NAME = 4;

    public static final String FILTER_CITY = "city";
    public static final String FILTER_REGION = "region";
    public static final String FILTER_CURRENT_WORKER = "currentWorker";
    public static final String FILTER_WORKERS = "workers";
    public static final String FILTER_WORKER = "worker";
    public static final String FILTER_NOMENCLATURE_COUNT = "nomenclatureCount";
    public static final String FILTER_TRANSFER_COUNT = "transferCount";
    public static final String FILTER_OBJECT_ID = "id";

    private Long nomenclatureCount;
    private Long transferCount;

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

    public String getName() {
        return getText(NAME);
    }

    public void setName(String name) {
        setText(NAME, name);
    }

    public Long getNomenclatureCount() {
        return nomenclatureCount;
    }

    public void setNomenclatureCount(Long nomenclatureCount) {
        this.nomenclatureCount = nomenclatureCount;
    }

    public Long getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(Long transferCount) {
        this.transferCount = transferCount;
    }
}
