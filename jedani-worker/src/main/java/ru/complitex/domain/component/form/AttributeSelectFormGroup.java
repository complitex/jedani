package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Anatoly A. Ivanov
 * 02.05.2018 7:57
 */
public class AttributeSelectFormGroup extends Panel {
    private AttributeSelect attributeSelect;

    public AttributeSelectFormGroup(String id, IModel<Long> model, String refEntityName, Long refEntityAttributeId) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId);
    }

    public AttributeSelectFormGroup(String id, IModel<String> label, IModel<Long> model, String refEntityName,
                                    Long refEntityAttributeId) {
        super(id);

        FormGroup group = new FormGroup("group", label);
        group.add(attributeSelect = new AttributeSelect("select", model, refEntityName, refEntityAttributeId));
        attributeSelect.setLabel(label);

        add(group);
    }

    public AttributeSelectFormGroup setRequired(boolean required){
        attributeSelect.setRequired(required);

        return this;
    }
}
