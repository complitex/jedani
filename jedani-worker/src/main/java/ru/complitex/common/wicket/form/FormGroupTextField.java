package ru.complitex.common.wicket.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidator;
import org.danekja.java.util.function.serializable.SerializableConsumer;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class FormGroupTextField<T> extends Panel{
    private boolean required;

    private TextField<T> textField;

    public FormGroupTextField(String id, IModel<String> label, IModel<T> model, Class<T> type) {
        super(id);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        FormGroupBorder group = new FormGroupBorder("group", label){
            @Override
            protected boolean isRequired() {
                return FormGroupTextField.this.isRequired();
            }
        };
        group.add(textField = new TextField<T>("input", model, type){
            @Override
            protected void onComponentTag(final ComponentTag tag){
                super.onComponentTag(tag);

                tag.put("autocomplete", "off");

                onTextFieldTag(tag);
            }

            @Override
            public boolean isRequired() {
                return FormGroupTextField.this.isRequired();
            }
        });
        textField.setLabel(label);

        add(group);
    }

    public FormGroupTextField(String id, IModel<T> model, Class<T> type) {
        this(id, new ResourceModel(id), model, type);
    }

    public FormGroupTextField(String id, IModel<T> model) {
        this(id, model, null);
    }

    public FormGroupTextField(String id, IModel<String> label, IModel<T> model) {
        this(id, label, model, null);
    }

    public FormGroupTextField onUpdate(SerializableConsumer<AjaxRequestTarget> onUpdate){
        textField.add(OnChangeAjaxBehavior.onChange(onUpdate));

        return this;
    }

    public TextField<T> getTextField() {
        return textField;
    }


    public boolean isRequired() {
        return required;
    }

    public FormGroupTextField<T> setRequired(boolean required){
        this.required = required;

        return this;
    }

    public FormGroupTextField<T> setType(Class<?> type){
        textField.setType(type);

        return this;
    }

    public FormGroupTextField<T> addValidator(IValidator<T> validator){
        textField.add(validator);

        return this;
    }

    @Override
    protected void onModelChanged() {
        textField.modelChanged();
    }

    protected void onTextFieldTag(ComponentTag tag){

    }
}
