package ru.complitex.jedani.worker.entity;

import java.io.Serializable;

/**
 * @author Ivanov Anatoliy
 */
public class RewardError implements Serializable {
    public final static long TOTAL_NULL = 1;
    public final static long TOTAL_LESS_OR_EQUAL_ZERO = 2;
    public final static long TOTAL_ESTIMATED_LESS_THAN_CHARGED = 3;
    public final static long AMOUNT_NULL = 4;
    public final static long AMOUNT_LESS_OR_EQUAL_ZERO = 5;
    public final static long AMOUNT_ESTIMATED_LESS_THAN_CHARGED = 6;
    public final static long RATE_NOT_EQUAL = 7;
    public final static long CROSS_RATE_NOT_EQUAL = 8;
    public final static long DISCOUNT_NOT_EQUAL = 9;
    public final static long ESTIMATED_EMPTY = 10;
}
