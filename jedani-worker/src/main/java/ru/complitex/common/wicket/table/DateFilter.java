package ru.complitex.common.wicket.table;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2018 20:04
 */
public class DateFilter extends Panel {
    private DateTextField filter;

    public DateFilter(String id, IModel<Date> model) {
        super(id);

        filter =  new DateTextField("filter", model, new DateTextFieldConfig()
                .withFormat("dd.MM.yyyy")
                .withLanguage("ru")
                .autoClose(true));
        add(filter);
    }

    public DateTextField getFilter() {
        return filter;
    }
}
