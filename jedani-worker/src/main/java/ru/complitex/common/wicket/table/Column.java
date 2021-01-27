package ru.complitex.common.wicket.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.SortProperty;

/**
 * @author Anatoly A. Ivanov
 * 15.03.2019 22:50
 */
public abstract class Column<T> extends AbstractColumn<T, SortProperty>
        implements IFilterColumn<T, SortProperty> {

    public Column(IModel<String> displayModel, SortProperty sortProperty) {
        super(displayModel, sortProperty);
    }

    public Column(IModel<String> displayModel) {
        super(displayModel);
    }
}
