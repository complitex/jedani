package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class DateTextFieldFormGroup extends Panel{
    public DateTextFieldFormGroup(String id, IModel<Date> model) {
        this(id, new ResourceModel(id), model);
    }

    public DateTextFieldFormGroup(String id, IModel<String> label, IModel<Date> model) {
        super(id);

        FormGroup group = new FormGroup("group", label);
        group.add(new DateTextField("input", model, "dd.MM.yyyy"));

        add(group);
    }
}
