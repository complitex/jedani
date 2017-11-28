package ru.complitex.ui.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 16:57
 */
public abstract class DataProvider<T> extends SortableDataProvider<T, String> implements IFilterStateLocator<T>{
    private T filterState;

    @Override
    public T getFilterState() {
        return filterState;
    }

    @Override
    public void setFilterState(T filterState) {
        this.filterState = filterState;
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<>(object);
    }
}
