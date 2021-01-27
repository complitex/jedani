package ru.complitex.common.wicket.table;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:05
 */
public interface IFilterColumn<T, S> extends IColumn<T, S> {
    Component getFilter(String componentId, FilterForm<?> form);
}
