package ru.complitex.common.wicket.table;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.Sort;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 15.03.2019 22:50
 */
public abstract class Column<T extends Serializable> extends AbstractColumn<T, Sort> {

    public Column(IModel<String> displayModel, Sort sort) {
        super(displayModel, sort);
    }

    public Column(IModel<String> displayModel) {
        super(displayModel);
    }

    public abstract Component getHeader(String componentId, Table<T> table);
}
