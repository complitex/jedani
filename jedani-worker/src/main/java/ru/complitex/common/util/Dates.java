package ru.complitex.common.util;

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

}
