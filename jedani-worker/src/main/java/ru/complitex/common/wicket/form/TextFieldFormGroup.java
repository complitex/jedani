package ru.complitex.common.wicket.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class TextFieldFormGroup<T> extends Panel{
    private boolean required;

    private TextField<T> textField;

    public TextFieldFormGroup(String id, IModel<T> model) {
        this(id, new ResourceModel(id), model);
    }

    public TextFieldFormGroup(String id, IModel<String> label, IModel<T> model) {
        super(id);

        setOutputMarkupId(true);

        FormGroupBorder group = new FormGroupBorder("group", label);
        group.add(textField = new TextField<T>("input", model){
            @Override
            protected void onComponentTag(final ComponentTag tag){
                super.onComponentTag(tag);

                tag.put("autocomplete", "off");
            }

            @Override
            public boolean isRequired() {
                return TextFieldFormGroup.this.isRequired();
            }
        });
        textField.setLabel(label);

        add(group);
    }

    public void onUpdate(SerializableConsumer<AjaxRequestTarget> onUpdate){
        textField.add(AjaxFormComponentUpdatingBehavior.onUpdate("change", onUpdate));
    }

    public TextField<T> getTextField() {
        return textField;
    }


    public boolean isRequired() {
        return required;
    }

    public TextFieldFormGroup<T> setRequired(boolean required){
        this.required = required;

        return this;
    }

    public TextFieldFormGroup<T> setType(Class<?> type){
        textField.setType(type);

        return this;
    }
}
