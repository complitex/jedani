package ru.complitex.jedani.worker.entity;

import java.io.Serializable;

/**
 * @author Ivanov Anatoliy
 */
public class RewardError implements Serializable {
    public static final long TOTAL_NULL = 1;
    public static final long TOTAL_LESS_OR_EQUAL_ZERO = 2;
    public static final long TOTAL_ESTIMATED_LESS_THAN_CHARGED = 3;
    public static final long AMOUNT_NULL = 4;
    public static final long AMOUNT_LESS_OR_EQUAL_ZERO = 5;
    public static final long AMOUNT_ESTIMATED_LESS_THAN_CHARGED = 6;
    public static final long RATE_NOT_EQUAL = 7;
    public static final long CROSS_RATE_NOT_EQUAL = 8;
    public static final long DISCOUNT_NOT_EQUAL = 9;
    public static final long ESTIMATED_EMPTY = 10;
    public static final long WITHDRAW_EMPTY = 11;
    public static final long PAYMENT_SUM_LESS_THAN_PAID_SALE_TOTAL = 12;
    public static final long ESTIMATED_POINT_NOT_EQUAL_CHARGED = 13;
    public static final long ESTIMATED_POINT_NOT_EQUAL_WITHDRAW = 14;
    public static final long ESTIMATED_AMOUNT_NOT_EQUAL_CHARGED = 15;
    public static final long ESTIMATED_AMOUNT_NOT_EQUAL_WITHDRAW = 16;
    public static final long PERSONAL_MYCOOK_REWARD_NOT_EXISTS = 17;
    public static final long PERSONAL_RANGE_REWARD_NOT_EXISTS = 18;
    public static final long PERSONAL_VOLUME_REWARD_NOT_EXISTS = 19;
    public static final long CULINARY_WORKSHOP_REWARD_NOT_EXISTS = 20;
    public static final long MANAGER_BONUS_REWARD_NOT_EXISTS = 21;
    public static final long MANAGER_PREMIUM_REWARD_NOT_EXISTS = 22;
    public static final long GROUP_VOLUME_REWARD_NOT_EXISTS = 23;
    public static final long STRUCTURE_VOLUME_REWARD_NOT_EXISTS = 24;
}
