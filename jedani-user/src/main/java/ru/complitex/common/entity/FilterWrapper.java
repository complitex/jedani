package ru.complitex.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 16:46
 */
public class FilterWrapper<T extends Serializable> implements Serializable {
    private T object;
    private Long first = 0L;
    private Long count = 0L;
    private String sortProperty = "id";
    private boolean ascending = false;
    private Map<String, Object> map = new HashMap<>();

    public FilterWrapper() {
    }

    public FilterWrapper(T object) {
        this.object = object;
    }

    public FilterWrapper(T object, Long first, Long count) {
        this.object = object;
        this.first = first;
        this.count = count;
    }

    public FilterWrapper(Long first, Long count) {
        this.first = first;
        this.count = count;
    }

    public static <T extends Serializable> FilterWrapper<T> of(T object){
        return new FilterWrapper<>(object);
    }

    public static <T extends Serializable> FilterWrapper<T> of(T object, long first, long count){
        return new FilterWrapper<>(object, first, count);
    }

    public FilterWrapper<T> add(String key, Object value){
        map.put(key, value);

        return this;
    }

    public FilterWrapper<T> limit(Long first, Long count){
        this.first = first;
        this.count = count;

        return this;
    }

    public String getAsc(){
        return ascending ? "asc" : "desc";
    }

    public String getLimit(){
        return count != null && count > 0 ? " limit " + first + ", " + count : "";
    }

    public String getOrderLimit(){
        return "order by `" + getSortProperty() + "` " + getAsc() + getLimit();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public long getFirst() {
        return first > 0 ? first : 0;
    }

    public void setFirst(long first) {
        this.first = first;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getSortProperty() {
        if (sortProperty == null){
            return "id";
        }

        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
