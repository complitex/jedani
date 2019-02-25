package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.wicket.component.FormGroupBorder;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2018 21:39
 */
public class AttributeSelectListFormGroup extends Panel {
    private AttributeSelectList attributeSelectList;

    public AttributeSelectListFormGroup(String id, IModel<String> label, IModel<Attribute> model, String refEntityName,
                                        Long refEntityAttributeId, IModel<List<Long>> parentListModel, boolean upperCase) {
        super(id);

        setOutputMarkupId(true);

        FormGroupBorder group = new FormGroupBorder("group", label);
        group.add(attributeSelectList = new AttributeSelectList("select", model, refEntityName,
                refEntityAttributeId, parentListModel, upperCase){
            @Override
            protected String getPrefix(Domain domain) {
                return AttributeSelectListFormGroup.this.getPrefix(domain);
            }
        });
        attributeSelectList.setLabel(label);

        add(group);
    }


    public AttributeSelectListFormGroup(String id, IModel<Attribute> model, String refEntityName,
                                        Long refEntityAttributeId, IModel<List<Long>> parentListModel, boolean upperCase) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId, parentListModel, upperCase);
    }

    public AttributeSelectListFormGroup(String id, IModel<Attribute> model, String refEntityName, Long refEntityAttributeId, boolean upperCase) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId, null, upperCase);
    }

    public IModel<List<Long>> getListModel(){
        return attributeSelectList.getListModel();
    }

    public void onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        attributeSelectList.onChange(onChange);
    }

    public AttributeSelectListFormGroup setRequired(boolean required){
        attributeSelectList.setRequired(required);

        return this;
    }

    protected String getPrefix(Domain domain){
        return "";
    }
}
