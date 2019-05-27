package ru.complitex.jedani.worker.entity;

import java.util.Arrays;
import java.util.List;

public class RuleConditionComparator {
    public final static long EQUAL = 1;
    public final static long NOT_EQUAL = 2;
    public final static long GREATER = 3;
    public final static long LOWER = 4;
    public final static long GREATER_OR_EQUAL = 5;
    public final static long LOWER_OR_EQUAL = 6;

    public static List<Long> getComparatorIds(){
        return Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
    }
}
