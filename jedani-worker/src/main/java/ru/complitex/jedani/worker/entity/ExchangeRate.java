package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 20:25
 */
public class ExchangeRate extends Domain<ExchangeRate> {
    public static final String ENTITY_NAME = "exchange_rate";

    public static final long NAME = 1;
    public static final long CODE = 2;
    public static final long BASE_CURRENCY = 3;
    public static final long COUNTER_CURRENCY = 4;
    public static final long URI_XML = 5;
    public static final long XPATH_NAME = 6;
    public static final long XPATH_CODE = 7;
    public static final long XPATH_DATE = 8;
    public static final long XPATH_VALUE = 9;
    public static final long VALUE = 10;
    public static final long URI_DATE_PARAM = 11;
    public static final long URI_DATE_FORMAT = 12;


    public ExchangeRate() {
        super(ENTITY_NAME);
    }

    public String getName(){
        return getTextValue(NAME);
    }

    public String getCode(){
        return getText(CODE);
    }

    public String getUriXml(){
        return getText(URI_XML);
    }

    public String getXpathDate(){
        return getText(XPATH_DATE);
    }

    public String getXpathValue(){
        return getText(XPATH_VALUE);
    }

    public String getUriDateParam(){
        return getText(URI_DATE_PARAM);
    }

    public String getUriDateFormat(){
        return getText(URI_DATE_FORMAT);
    }
}
