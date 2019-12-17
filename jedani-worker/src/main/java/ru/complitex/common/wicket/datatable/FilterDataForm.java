package ru.complitex.common.wicket.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 18:07
 */
public class FilterDataForm<T> extends Form<T> {
    private IFilterStateLocator<T> locator;

    public FilterDataForm(String id, IFilterStateLocator<T> locator) {
        super(id, new IModel<T>() {
            @Override
            public T getObject() {
                return locator.getFilterState();
            }

            @Override
            public void setObject(T object) {
                locator.setFilterState(object);
            }
        });

        this.locator = locator;

        setOutputMarkupId(true);
    }

    public IFilterStateLocator<T> getLocator() {
        return locator;
    }
}
