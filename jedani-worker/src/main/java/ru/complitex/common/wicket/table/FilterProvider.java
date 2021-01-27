package ru.complitex.common.wicket.table;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class FilterProvider<T extends Serializable> extends Provider<T> {
    public FilterProvider(FilterWrapper<T> filterState) {
        super(filterState);
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        FilterWrapper<T> filterWrapper = getFilterState().limit(first, count);

        if (getSort() != null){
            filterWrapper.setSortProperty(getSort().getProperty());
            filterWrapper.setAscending(getSort().isAscending());
        }else{
            filterWrapper.setSortProperty(new SortProperty("id"));
            filterWrapper.setAscending(false);
        }

        return getList().iterator();
    }

    @Override
    public long size() {
        return getCount();
    }

    public abstract List<T> getList();

    public abstract Long getCount();
}
