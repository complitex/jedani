package ru.complitex.common.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 22.04.2019 0:50
 */
public class Dates {
    public static Date currentDate(){
        return new Date();
    }

    public static Date lastDayOfMonth(Date date){
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static boolean isDayEquals(Date d1, Date d2){
        return d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .isEqual(d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public static Date atStartOfDay(Date date){
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date atEndOfDay(Date date){
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MAX)
                .withNano(0).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isSameMonth(Date d1, Date d2){
        LocalDate ld1 = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ld2 = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return ld1.getYear() == ld2.getYear() && ld1.getMonthValue() == ld2.getMonthValue();
    }
}
