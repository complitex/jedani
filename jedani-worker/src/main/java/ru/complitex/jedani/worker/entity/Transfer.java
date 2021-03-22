package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2018 15:41
 */
public class Transfer extends Domain<Transfer> {
    public static final String ENTITY_NAME = "transfer";

    public static final String FILTER_STORAGE_TO_ID = "storageToId";
    public static final String FILTER_RECEIVING = "receiving";
    public static final String FILTER_RECEIVING_GIFT = "receivingGift";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long TYPE = 3;
    public static final long RELOCATION_TYPE = 4;
    public static final long RECIPIENT_TYPE = 5;
    public static final long STORAGE_FROM = 6;
    public static final long STORAGE_TO = 7;
    public static final long WORKER_TO = 8;
    public static final long FIRST_NAME_TO = 9;
    public static final long MIDDLE_NAME_TO = 10;
    public static final long LAST_NAME_TO = 11;
    public static final long SERIAL_NUMBER = 12;
    public static final long COMMENTS = 13;

    public Transfer() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getNomenclatureId() {
        return getNumber(NOMENCLATURE);
    }

    public void setNomenclatureId(Long nomenclatureId) {
        setNumber(NOMENCLATURE, nomenclatureId);
    }

    public Long getQuantity() {
        return getNumber(QUANTITY);
    }

    public void setQuantity(Long quantity) {
        setNumber(QUANTITY, quantity);
    }

    public Long getType() {
        return getNumber(TYPE);
    }

    public void setType(Long type) {
        setNumber(TYPE, type);
    }

    public Long getRelocationType(){
        return getNumber(RELOCATION_TYPE);
    }

    public void setRelocationType(Long relocationType){
        setNumber(RELOCATION_TYPE, relocationType);
    }

    public Long getRecipientType() {
        return getNumber(RECIPIENT_TYPE);
    }

    public void setRecipientType(Long recipientType) {
        setNumber(RECIPIENT_TYPE, recipientType);
    }

    public Long getStorageIdFrom() {
        return getNumber(STORAGE_FROM);
    }

    public void setStorageIdFrom(Long storageId) {
        setNumber(STORAGE_FROM, storageId);
    }

    public Long getStorageIdTo() {
        return getNumber(STORAGE_TO);
    }

    public void setStorageIdTo(Long storageId) {
        setNumber(STORAGE_TO, storageId);
    }

    public Long getWorkerIdTo() {
        return getNumber(WORKER_TO);
    }

    public void setWorkerIdTo(Long workerId) {
        setNumber(WORKER_TO, workerId);
    }

    public Long getFirstNameIdTo() {
        return getNumber(FIRST_NAME_TO);
    }

    public void setFirstNameIdTo(Long firstNameId) {
        setNumber(FIRST_NAME_TO, firstNameId);
    }

    public Long getMiddleNameIdTo(){
        return getNumber(MIDDLE_NAME_TO);
    }

    public void setMiddleNameIdTo(Long middleNameId){
        setNumber(MIDDLE_NAME_TO, middleNameId);
    }

    public Long getLastNameIdTo() {
        return getNumber(LAST_NAME_TO);
    }

    public void setLastNameIdTo(Long lastNameId){
        setNumber(LAST_NAME_TO, lastNameId);
    }

    public String getSerialNumber(){
        return getText(SERIAL_NUMBER);
    }

    public void setSerialNumber(String serialNumber){
        setText(SERIAL_NUMBER, serialNumber);
    }

    public String getComments(){
        return getText(COMMENTS);
    }

    public void setComments(String comments){
        setText(COMMENTS, comments);
    }


}
