package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2018 21:39
 */
public class FormGroupAttributeSelectList extends Panel {
    private AttributeSelectList attributeSelectList;

    private boolean required;

    public FormGroupAttributeSelectList(String id, IModel<String> label, IModel<Attribute> model, String refEntityName,
                                        Long refEntityAttributeId, IModel<List<Long>> parentListModel, boolean upperCase) {
        super(id);

        setOutputMarkupId(true);

        FormGroupBorder group = new FormGroupBorder("group", label){
            @Override
            protected boolean isRequired() {
                return attributeSelectList.isRequired();
            }
        };

        group.add(attributeSelectList = new AttributeSelectList("select", model, refEntityName,
                refEntityAttributeId, parentListModel, upperCase){
            @Override
            protected String getPrefix(Domain domain) {
                return FormGroupAttributeSelectList.this.getPrefix(domain);
            }

            @Override
            public boolean isRequired() {
                return FormGroupAttributeSelectList.this.isRequired();
            }
        });
        attributeSelectList.setLabel(label);

        add(group);
    }


    public FormGroupAttributeSelectList(String id, IModel<Attribute> model, String refEntityName,
                                        Long refEntityAttributeId, IModel<List<Long>> parentListModel, boolean upperCase) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId, parentListModel, upperCase);
    }

    public FormGroupAttributeSelectList(String id, IModel<Attribute> model, String refEntityName, Long refEntityAttributeId, boolean upperCase) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId, null, upperCase);
    }

    public IModel<List<Long>> getListModel(){
        return attributeSelectList.getListModel();
    }

    public void onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        attributeSelectList.onChange(onChange);
    }

    public boolean isRequired() {
        return required;
    }

    public FormGroupAttributeSelectList setRequired(boolean required){
        this.required = required;

        return this;
    }
    protected String getPrefix(Domain domain){
        return "";
    }
}
