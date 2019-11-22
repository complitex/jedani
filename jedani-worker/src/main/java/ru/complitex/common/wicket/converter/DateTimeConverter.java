package ru.complitex.common.wicket.converter;

import org.apache.wicket.util.convert.converter.AbstractDateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 24.02.2019 5:52
 */
public class DateTimeConverter extends AbstractDateConverter<Date> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    protected Date createDateLike(long date) {
        return new Date(date);
    }

    @Override
    protected Class<Date> getTargetType() {
        return Date.class;
    }

    @Override
    public DateFormat getDateFormat(Locale locale) {
       return simpleDateFormat;
    }
}
