package ru.complitex.jedani.worker.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2018 15:02
 */
public class TransferRecipientType implements Serializable {
    public static final long STORAGE = 1;
    public static final long WORKER = 2;
    public static final long CLIENT = 3;
}
