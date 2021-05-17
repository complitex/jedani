package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.10.2018 16:03
 */
public class Product extends Domain {
    public static final String ENTITY_NAME = "product";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long SENDING_QUANTITY = 3;
    public static final long RECEIVING_QUANTITY = 4;
    public static final long GIFT_QUANTITY = 5;
    public static final long GIFT_SENDING_QUANTITY = 6;
    public static final long GIFT_RECEIVING_QUANTITY = 7;
    public static final long RESERVE_QUANTITY = 8;

    public Product() {
        super(ENTITY_NAME);
    }

    public Long getAvailableQuantity(){
        return getNumber(QUANTITY) - getNumber(RESERVE_QUANTITY, 0L);
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

    public void setQuantity(Long quantity){
        setNumber(QUANTITY, quantity);
    }

    public Long getSendingQuantity(){
        return getNumber(SENDING_QUANTITY, 0L);
    }

    public void setSendingQuantity(Long sendingQuantity){
        setNumber(SENDING_QUANTITY, sendingQuantity);
    }

    public Long getReceivingQuantity(){
        return getNumber(RECEIVING_QUANTITY, 0L);
    }

    public void setReceivingQuantity(Long receivingQuantity){
        setNumber(RECEIVING_QUANTITY, receivingQuantity);
    }

    public Long getGiftQuantity(){
        return getNumber(GIFT_QUANTITY, 0L);
    }

    public void setGiftQuantity(Long giftQuantity){
        setNumber(GIFT_QUANTITY, giftQuantity);
    }

    public Long getGiftSendingQuantity(){
        return getNumber(GIFT_SENDING_QUANTITY, 0L);
    }

    public void setGiftSendingQuantity(Long giftSendingQuantity){
        setNumber(GIFT_SENDING_QUANTITY, giftSendingQuantity);
    }

    public Long getGiftReceivingQuantity(){
        return getNumber(GIFT_RECEIVING_QUANTITY, 0L);
    }

    public void setGiftReceivingQuantity(Long giftReceivingQuantity){
        setNumber(GIFT_RECEIVING_QUANTITY, giftReceivingQuantity);
    }

    public Long getReserveQuantity(){
        return getNumber(RESERVE_QUANTITY, 0L);
    }

    public void setReserveQuantity(Long reserveQuantity){
        setNumber(RESERVE_QUANTITY, reserveQuantity);
    }


}
