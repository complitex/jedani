package ru.complitex.jedani.worker.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 16.11.2018 13:39
 */
public class TransferType implements Serializable {
    public static final long ACCEPT = 1;
    public static final long SELL = 2;
    public static final long RELOCATION = 3;
    public static final long WITHDRAW = 4;
    public static final long RESERVE = 5;
}
