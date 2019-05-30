package ru.complitex.common.wicket.converter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 12.04.2019 21:31
 */
public class BigDecimalConverter extends org.apache.wicket.util.convert.converter.BigDecimalConverter {
    @Override
    protected NumberFormat newNumberFormat(Locale locale) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();

        DecimalFormatSymbols decimalFormatSymbols = (DecimalFormatSymbols) DecimalFormatSymbols.getInstance().clone();
        decimalFormatSymbols.setDecimalSeparator('.');

        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setGroupingUsed(false);

        return decimalFormat;
    }
}
