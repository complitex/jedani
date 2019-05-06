package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Attributes;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 02.05.2018 7:57
 */
public class AttributeSelectFormGroup extends Panel {
    @Inject
    private DomainMapper domainMapper;

    private AttributeSelect attributeSelect;

    public AttributeSelectFormGroup(String id, IModel<Long> model, String refEntityName, Long refEntityAttributeId) {
        this(id, new ResourceModel(id), model, refEntityName, refEntityAttributeId);

        setOutputMarkupId(true);
    }

    public AttributeSelectFormGroup(String id, IModel<String> label, IModel<Long> model, String refEntityName,
                                    Long refEntityAttributeId) {
        super(id);

        FormGroupBorder group = new FormGroupBorder("group", label);

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

    public AttributeSelectFormGroup setRequired(boolean required){
        if (attributeSelect != null) {
            attributeSelect.setRequired(required);
        }

        return this;
    }
}
