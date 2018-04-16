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
public class AttributeSelectFormGroup extends Panel {
    public AttributeSelectFormGroup(String id, IModel<Attribute> model, String referenceEntityName, Long referenceEntityAttributeId) {
        this(id, new ResourceModel(id), model, referenceEntityName, referenceEntityAttributeId);
    }

    public AttributeSelectFormGroup(String id, IModel<String> label, IModel<Attribute> model, String referenceEntityName, Long referenceEntityAttributeId) {
        super(id);

        FormGroup group = new FormGroup("group", label);
        group.add(new AttributeSelectList("select", model, referenceEntityName, referenceEntityAttributeId));

        add(group);
    }
}
