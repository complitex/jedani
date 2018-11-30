package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2018 15:41
 */
public class Transaction extends Domain {
    public static final String ENTITY_NAME = "transaction";

    public static final long NOMENCLATURE = 1;
    public static final long QUANTITY = 2;
    public static final long STORAGE_FROM = 3;
    public static final long STORAGE_TO = 4;
    public static final long WORKER_TO = 5;
    public static final long FIRST_NAME = 6;
    public static final long MIDDLE_NAME = 7;
    public static final long LAST_NAME = 8;
    public static final long TYPE = 9;
    public static final long TRANSFER_TYPE = 10;
    public static final long SERIAL_NUMBER = 11;
    public static final long COMMENTS = 12;

    public Transaction() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }
}
