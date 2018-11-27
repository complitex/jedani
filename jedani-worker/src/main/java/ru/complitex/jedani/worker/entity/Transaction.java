package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2018 15:41
 */
public class Transaction extends Domain {
    public static final String ENTITY_NAME = "transaction";

    public static final long NOMENCLATURE_ID = 1;
    public static final long QUANTITY = 2;
    public static final long STORAGE_ID_FROM = 3;
    public static final long STORAGE_ID_TO = 4;
    public static final long WORKER_ID_TO = 5;
    public static final long TYPE = 6;
    public static final long TRANSFER_TYPE = 7;
    public static final long SERIAL_NUMBER = 8;
    public static final long COMMENTS = 9;

    public Transaction() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }
}
