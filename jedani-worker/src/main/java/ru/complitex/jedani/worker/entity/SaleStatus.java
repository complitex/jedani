package ru.complitex.jedani.worker.entity;

public class SaleStatus {
    public static final long CREATED = 1;
    public static final long PAYING = 2;
    public static final long RISK = 3;
    public static final long NOT_PAYING = 4;
    public static final long PAID = 5;
    public static final long OVERPAID = 6;
    public static final long ARCHIVE = 7;
}
