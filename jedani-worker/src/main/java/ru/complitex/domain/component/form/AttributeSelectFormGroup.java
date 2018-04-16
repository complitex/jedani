package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.domain.entity.Attribute;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2018 21:39
 */
public class AttributeSelectFormGroup extends Panel {
    private AttributeSelectList attributeSelectList;

    public AttributeSelectFormGroup(String id, IModel<String> label, IModel<Attribute> model, String referenceEntityName,
                                    Long referenceEntityAttributeId, IModel<List<Long>> parentListModel) {
        super(id);

        setOutputMarkupId(true);

        FormGroup group = new FormGroup("group", label);
        group.add(attributeSelectList = new AttributeSelectList("select", model, referenceEntityName,
                referenceEntityAttributeId, parentListModel));

        add(group);
    }


    public AttributeSelectFormGroup(String id, IModel<Attribute> model, String referenceEntityName,
                                    Long referenceEntityAttributeId, IModel<List<Long>> parentListModel) {
        this(id, new ResourceModel(id), model, referenceEntityName, referenceEntityAttributeId, parentListModel);
    }

    public AttributeSelectFormGroup(String id, IModel<Attribute> model, String referenceEntityName, Long referenceEntityAttributeId) {
        this(id, new ResourceModel(id), model, referenceEntityName, referenceEntityAttributeId, null);
    }

    public IModel<List<Long>> getListModel(){
        return attributeSelectList.getListModel();
    }

    public void setOnChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        attributeSelectList.setOnChange(onChange);
    }
}
