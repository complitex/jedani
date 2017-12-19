package ru.complitex.common.wicket.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 16:57
 */
public abstract class DataProvider<T extends Serializable> extends SortableDataProvider<T, String>
        implements IFilterStateLocator<FilterWrapper<T>>{
    private FilterWrapper<T> filterState;

    public DataProvider(FilterWrapper<T> filterState) {
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
}
