package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 02.05.2018 7:57
 */
public class FormGroupAttributeSelect extends Panel {
    @Inject
    private DomainMapper domainMapper;

    private AttributeSelect attributeSelect;

    public FormGroupAttributeSelect(String id, IModel<Long> model, String refEntityName, Long refEntityAttributeId) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId);

        setOutputMarkupId(true);
    }

    public FormGroupAttributeSelect(String id, IModel<String> label, IModel<Long> model, String refEntityName,
                                    Long refEntityAttributeId) {
        super(id);

        FormGroupBorder group = new FormGroupBorder("group", label){
            @Override
            protected boolean isRequired() {
                return attributeSelect != null && attributeSelect.isRequired();
            }
        };

        if (isEnabledInHierarchy()){
            attributeSelect = new AttributeSelect("select", model, refEntityName, refEntityAttributeId);
            group.add(attributeSelect);

            attributeSelect.setLabel(label);

            group.add(new EmptyPanel("view").setVisible(false));
        }else{
            group.add(new EmptyPanel("select").setVisible(false));

            String view = "";

            if (model.getObject() != null){
                view = Attributes.capitalize(domainMapper.getDomain(refEntityName, model.getObject())
                        .getTextValue(refEntityAttributeId));
            }

            group.add(new TextField<>("view", Model.of(view)));
        }

        add(group);
    }

    public FormGroupAttributeSelect(String id, IModel<? extends Domain> domainModel, Long entityAttributeId,
                                    String refEntityName, Long refEntityAttributeId){
        this(id, NumberAttributeModel.of(domainModel, entityAttributeId), refEntityName, refEntityAttributeId);
    }

    public FormGroupAttributeSelect setRequired(boolean required){
        if (attributeSelect != null) {
            attributeSelect.setRequired(required);
        }

        return this;
    }

    public FormGroupAttributeSelect setNullValid(boolean nullValid){
        if (attributeSelect != null) {
            attributeSelect.setNullValid(nullValid);
        }

        return this;
    }
}
