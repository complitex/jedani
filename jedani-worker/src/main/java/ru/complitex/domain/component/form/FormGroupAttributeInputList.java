package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.entity.Attribute;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2018 21:39
 */
public class FormGroupAttributeInputList extends Panel {
    private AttributeInputList attributeInputList;

    private boolean required;

    public FormGroupAttributeInputList(String id, IModel<Attribute> model) {
        this(id, new ResourceModel(id), model);
    }

    public FormGroupAttributeInputList(String id, IModel<String> label, IModel<Attribute> model) {
        super(id);

        FormGroupBorder group = new FormGroupBorder("group", label);

        group.add(attributeInputList = new AttributeInputList("input", model){
            @Override
            public boolean isRequired() {
                return FormGroupAttributeInputList.this.isRequired();
            }
        });
        attributeInputList.setLabel(label);

        add(group);
    }

    public boolean isRequired() {
        return required;
    }

    public FormGroupAttributeInputList setRequired(boolean required){
        this.required = required;

        return this;
    }
}
