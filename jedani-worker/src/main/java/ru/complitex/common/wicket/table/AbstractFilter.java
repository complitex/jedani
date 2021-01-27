package ru.complitex.common.wicket.table;

import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:42
 */
public class AbstractFilter extends Panel {
    private final FilterForm<?> form;

    public AbstractFilter(String id, FilterForm<?> form) {
        super(id);

        this.form = form;
    }

    public FilterForm<?> getForm() {
        return form;
    }
}
