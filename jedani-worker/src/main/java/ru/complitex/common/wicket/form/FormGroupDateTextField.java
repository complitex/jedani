package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.DateAttributeModel;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class FormGroupDateTextField extends Panel{
    private DateTextField dateTextField;

    private boolean required;

    public FormGroupDateTextField(String id, IModel<Date> model) {
        this(id, new ResourceModel(id), model);
    }

    public FormGroupDateTextField(String id, IModel<String> label, IModel<Date> model) {
        super(id);

        setOutputMarkupId(true);

        FormGroup group = new FormGroup("group", label){
            @Override
            protected Component newLabel(String id, IModel<String> model) {
                return new Label(id, model){
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);

                        if (isRequired()){
                            tag.put("required", "required");
                        }
                    }
                };
            }
        };
        group.add(dateTextField = new DateTextField("input", model, getDateTextFieldConfig()){
            @Override
            public boolean isRequired() {
                return FormGroupDateTextField.this.isRequired();
            }
        });
        dateTextField.setLabel(label);

        add(group);
    }

    protected DateTextFieldConfig getDateTextFieldConfig() {
        return new DateTextFieldConfig()
                .withFormat("dd.MM.yyyy")
                .withLanguage("ru")
                .autoClose(true)
                .highlightToday(true);
    }

    public FormGroupDateTextField(String id, IModel<? extends Domain> domainModel, Long entityAttributeId){
        this(id, new ResourceModel(id), DateAttributeModel.of(domainModel, entityAttributeId));
    }

    public boolean isRequired() {
        return required;
    }

    public FormGroupDateTextField setRequired(boolean required){
        this.required = required;

        return this;
    }

    public FormGroupDateTextField onUpdate(SerializableConsumer<AjaxRequestTarget> onUpdate){
        dateTextField.add(OnChangeAjaxBehavior.onChange(onUpdate));

        return this;
    }
}
