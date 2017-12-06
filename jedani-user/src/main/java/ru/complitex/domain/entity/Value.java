package ru.complitex.domain.entity;

import ru.complitex.domain.util.Locales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:42
 */
public class Value implements Serializable{
    private Long id;
    private Long attributeId;
    private Long localeId;
    private String value;

    private String entityName;

    public Value() {
    }

    public Value(Long localeId) {
        this.localeId = localeId;
    }

    public Value(Long localeId, String value) {
        this.localeId = localeId;
        this.value = value;
    }

    public static List<Value> newStringValues() {
        List<Value> values = new ArrayList<>();

        for (Long localeId : Locales.getLocaleIds()){
            values.add(new Value(localeId));
        }

        return values;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
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
