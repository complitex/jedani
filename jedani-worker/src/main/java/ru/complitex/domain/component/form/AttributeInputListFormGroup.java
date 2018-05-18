package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.domain.entity.Attribute;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2018 21:39
 */
public class AttributeInputListFormGroup extends Panel {
    private AttributeInputList attributeInputList;

    public AttributeInputListFormGroup(String id, IModel<Attribute> model) {
        this(id, new ResourceModel(id), model);
    }

    public AttributeInputListFormGroup(String id, IModel<String> label, IModel<Attribute> model) {
        super(id);

        FormGroup group = new FormGroup("group", label);
        group.add(attributeInputList = new AttributeInputList("input", model));
        attributeInputList.setLabel(label);

        add(group);
    }

    public AttributeInputListFormGroup setRequired(boolean required){
        attributeInputList.setRequired(required);

        return this;
    }
}