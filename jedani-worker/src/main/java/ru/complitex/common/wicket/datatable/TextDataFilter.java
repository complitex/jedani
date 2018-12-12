package ru.complitex.common.wicket.datatable;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:22
 */
public class TextDataFilter<T> extends AbstractDataFilter {
    private TextField<T> filter;

    public TextDataFilter(String id, IModel<T> model, FilterDataForm<?> form) {
        super(id, form);

        add(filter = new TextField<>("filter", model));
    }

    public TextField<T> getFilter() {
        return filter;
    }
}
