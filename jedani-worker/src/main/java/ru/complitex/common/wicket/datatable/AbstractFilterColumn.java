package ru.complitex.common.wicket.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.SortProperty;

/**
 * @author Anatoly A. Ivanov
 * 15.03.2019 22:50
 */
public abstract class AbstractFilterColumn<T> extends AbstractColumn<T, SortProperty>
        implements IFilterDataColumn<T, SortProperty> {

    public AbstractFilterColumn(IModel<String> displayModel, SortProperty sortProperty) {
        super(displayModel, sortProperty);
    }

    public AbstractFilterColumn(IModel<String> displayModel) {
        super(displayModel);
    }
}
