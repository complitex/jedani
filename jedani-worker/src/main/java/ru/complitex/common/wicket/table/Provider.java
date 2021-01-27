package ru.complitex.common.wicket.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 16:57
 */
public abstract class Provider<T extends Serializable> extends SortableDataProvider<T, Sort>
        implements IFilterStateLocator<FilterWrapper<T>>{
    private FilterWrapper<T> filterState;

    public Provider(FilterWrapper<T> filterState) {
        this.filterState = filterState;
    }

    @Override
    public FilterWrapper<T> getFilterState() {
        return filterState;
    }

    @Override
    public void setFilterState(FilterWrapper<T> filterState) {
        this.filterState = filterState;
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<>(object);
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        FilterWrapper<T> filterWrapper = getFilterState().limit(first, count);

        if (getSort() != null){
            filterWrapper.setSortProperty(getSort().getProperty());
            filterWrapper.setAscending(getSort().isAscending());
        }else{
            filterWrapper.sort("id", false);
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
