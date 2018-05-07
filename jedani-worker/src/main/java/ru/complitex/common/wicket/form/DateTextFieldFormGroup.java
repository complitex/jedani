package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class DateTextFieldFormGroup extends Panel{
    private DatetimePicker datetimePicker;

    public DateTextFieldFormGroup(String id, IModel<Date> model) {
        this(id, new ResourceModel(id), model);
    }

    public DateTextFieldFormGroup(String id, IModel<String> label, IModel<Date> model) {
        super(id);

        setOutputMarkupId(true);

        FormGroup group = new FormGroup("group", label);
        group.add(datetimePicker = new DatetimePicker("input", model, "dd.MM.yyyy"));
        datetimePicker.setLabel(label);

        add(group);
    }

    public DateTextFieldFormGroup setRequired(boolean required){
        datetimePicker.setRequired(required);

        return this;
    }

    public DateTextFieldFormGroup onUpdate(SerializableConsumer<AjaxRequestTarget> onUpdate){
        datetimePicker.add(AjaxFormComponentUpdatingBehavior.onUpdate("blur", onUpdate));

        return this;
    }
}
