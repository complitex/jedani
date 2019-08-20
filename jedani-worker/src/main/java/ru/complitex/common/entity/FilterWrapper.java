package ru.complitex.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 16:46
 */
public class FilterWrapper<T extends Serializable> implements Serializable {
    public static final String FILTER_EQUAL = "equal";
    public static final String FILTER_SEARCH = "search";

    public static final String STATUS_ACTIVE_AND_ARCHIVE = "active_and_archive";

    private T object;
    private Long first = 0L;
    private Long count = 0L;
    private SortProperty sortProperty;
    private boolean ascending = false;
    private Map<String, Object> map = new HashMap<>();

    private String filter;

    private String status;

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

    public FilterWrapper<T> put(String key, Object value){
        map.put(key, value);

        return this;
    }

    public FilterWrapper<T> limit(Long first, Long count){
        this.first = first;
        this.count = count;

        return this;
    }

    public FilterWrapper<T> sort(String key, Object value){
        this.sortProperty = new SortProperty(key, value);

        return this;
    }

    public FilterWrapper<T> sort(String key, Object value, boolean ascending){
        this.sortProperty = new SortProperty(key, value);
        this.ascending = ascending;

        return this;
    }

    public FilterWrapper<T> sort(String key, boolean ascending){
        this.sortProperty = new SortProperty(key);
        this.ascending = ascending;

        return this;
    }

    public String getAsc(){
        return ascending ? "asc" : "desc";
    }

    public String getLimit(){
        return count != null && count > 0 ? " limit " + first + ", " + count : "";
    }

    public String getOrderLimit(){
        return "order by `" + getSortProperty().getKey() + "` " + getAsc() + getLimit();
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Long getFirst() {
        return first;
    }

    public void setFirst(Long first) {
        this.first = first;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public SortProperty getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(SortProperty sortProperty) {
        this.sortProperty = sortProperty;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public String getFilter() {
        return filter;
    }

    public FilterWrapper<T> setFilter(String filter) {
        this.filter = filter;

        return this;
    }

    public String getStatus() {
        return status;
    }

    public FilterWrapper<T> setStatus(String status) {
        this.status = status;

        return this;
    }
}
