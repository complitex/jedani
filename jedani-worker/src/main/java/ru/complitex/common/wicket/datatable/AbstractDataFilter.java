package ru.complitex.common.wicket.datatable;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:42
 */
public class AbstractDataFilter extends Panel {
    private final FilterDataForm<?> form;

    public AbstractDataFilter(String id, FilterDataForm<?> form) {
        super(id);

        this.form = form;
    }

    public FilterDataForm<?> getForm() {
        return form;
    }
}
