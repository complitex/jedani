package ru.complitex.common.wicket.table;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 05.12.2018 20:04
 */
public class DateFilter extends AbstractFilter<Date> {
    private final DateTextField dateTextField;

    public DateFilter(String id, IModel<Date> model) {
        super(id, model);

        dateTextField = new DateTextField("filter", model, new DateTextFieldConfig()
                .withFormat("dd.MM.yyyy")
                .withLanguage("ru")
                .autoClose(true)
                .highlightToday(true)){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                if (getLabelModel() != null){
                    tag.put("placeholder", getLabelModel().getObject());
                }
            }
        };

        add(dateTextField);
    }

    @Override
    public AbstractFilter<Date> onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        dateTextField.add(OnChangeAjaxBehavior.onChange(onChange));

        return this;
    }
}
