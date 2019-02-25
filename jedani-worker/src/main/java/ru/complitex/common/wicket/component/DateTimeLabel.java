package ru.complitex.common.wicket.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import ru.complitex.common.wicket.converter.DateTimeConverter;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 22.02.2019 6:00
 */
public class DateTimeLabel extends Label {
    public DateTimeLabel(String id) {
        super(id);
    }

    public DateTimeLabel(String id, Serializable label) {
        super(id, label);
    }

    public DateTimeLabel(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    protected IConverter<?> createConverter(Class<?> type) {
        return new DateTimeConverter();
    }
}
