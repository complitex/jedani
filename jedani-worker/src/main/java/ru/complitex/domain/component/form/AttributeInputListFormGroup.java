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
public class AttributeInputListFormGroup extends Panel {
    private AttributeInputList attributeInputList;

    private boolean required;

    public AttributeInputListFormGroup(String id, IModel<Attribute> model) {
        this(id, new ResourceModel(id), model);
    }

    public AttributeInputListFormGroup(String id, IModel<String> label, IModel<Attribute> model) {
        super(id);

        FormGroupBorder group = new FormGroupBorder("group", label);

        group.add(attributeInputList = new AttributeInputList("input", model){
            @Override
            public boolean isRequired() {
                return AttributeInputListFormGroup.this.isRequired();
            }
        });
        attributeInputList.setLabel(label);

        add(group);
    }

    public boolean isRequired() {
        return required;
    }

    public AttributeInputListFormGroup setRequired(boolean required){
        this.required = required;

        return this;
    }
}
