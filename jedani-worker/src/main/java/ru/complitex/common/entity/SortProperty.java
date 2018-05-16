package ru.complitex.common.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 6:12
 */
public class SortProperty implements Serializable{
    private String key;
    private Object value;

    public SortProperty(String key) {
        this.key = key;
    }

    public SortProperty(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
