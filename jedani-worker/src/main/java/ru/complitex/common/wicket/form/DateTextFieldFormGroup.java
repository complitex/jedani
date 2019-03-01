package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
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
    private DateTextField dateTextField;

    private boolean required;

    public DateTextFieldFormGroup(String id, IModel<Date> model) {
        this(id, new ResourceModel(id), model);
    }

    public DateTextFieldFormGroup(String id, IModel<String> label, IModel<Date> model) {
        super(id);

        setOutputMarkupId(true);

        FormGroup group = new FormGroup("group", label);
        group.add(dateTextField = new DateTextField("input", model,
                new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true)){
            @Override
            public boolean isRequired() {
                return DateTextFieldFormGroup.this.isRequired();
            }
        });
        dateTextField.setLabel(label);

        add(group);
    }

    public boolean isRequired() {
        return required;
    }

    public DateTextFieldFormGroup setRequired(boolean required){
        this.required = required;

        return this;
    }

    public DateTextFieldFormGroup onUpdate(SerializableConsumer<AjaxRequestTarget> onUpdate){
        dateTextField.add(AjaxFormComponentUpdatingBehavior.onUpdate("blur", onUpdate));

        return this;
    }
}
