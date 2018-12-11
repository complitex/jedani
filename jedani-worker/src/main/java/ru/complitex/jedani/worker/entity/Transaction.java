package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2018 15:41
 */
public class Transaction extends Domain {
    public static final String ENTITY_NAME = "transaction";

    public static final String FILTER_STORAGE_TO_ID = "storageToId";
    public static final String FILTER_RECEIVING = "receiving";
    public static final String FILTER_RECEIVING_GIFT = "receivingGift";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long TYPE = 3;
    public static final long TRANSFER_TYPE = 4;
    public static final long RECIPIENT_TYPE = 5;
    public static final long STORAGE_FROM = 6;
    public static final long STORAGE_TO = 7;
    public static final long WORKER_TO = 8;
    public static final long FIRST_NAME_TO = 9;
    public static final long MIDDLE_NAME_TO = 10;
    public static final long LAST_NAME_TO = 11;
    public static final long SERIAL_NUMBER = 12;
    public static final long COMMENTS = 13;

    public Transaction() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }
}
