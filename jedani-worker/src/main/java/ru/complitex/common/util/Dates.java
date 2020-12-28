package ru.complitex.common.util;

import ru.complitex.domain.util.Locales;

import java.text.SimpleDateFormat;
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
    public static SimpleDateFormat monthFormat = new SimpleDateFormat("LL.yy", Locales.getSystemLocale());
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locales.getSystemLocale());

    public static Date currentDate(){
        return new Date();
    }

    public static Date firstDayOfMonth(Date date){
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date firstDayOfMonth(){
        return firstDayOfMonth(currentDate());
    }

    public static Date lastDayOfMonth(Date date){
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date lastDayOfMonth(){
        return lastDayOfMonth(currentDate());
    }

    public static Date nextMonth(Date date){
        return Date.from(date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusMonths(1)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static boolean isSameDay(Date d1, Date d2){
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

    public static String getMonthText(Date date){
        return date != null ? monthFormat.format(date) : "";
    }

    public static String getDateText(Date date){
        return date != null ? dateFormat.format(date) : "";
    }



}
