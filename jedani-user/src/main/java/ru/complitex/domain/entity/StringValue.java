package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:42
 */
public class StringValue implements Serializable{
    private Long pkId;
    private Long id;
    private Long localeId;
    private String value;

    private String entityName;

    public StringValue() {
    }

    public StringValue(Long localeId) {
        this.localeId = localeId;
    }

    public StringValue(Long localeId, String value) {
        this.localeId = localeId;
        this.value = value;
    }

    public static List<StringValue> newStringValues() {
        List<StringValue> stringValues = new ArrayList<>();

        for (Long localeId : Locales.getLocaleIds()){
            stringValues.add(new StringValue(localeId));
        }

        return stringValues;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Long localeId) {
        this.localeId = localeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
