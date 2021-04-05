package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
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

    private boolean required;

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
            attributeSelect = new AttributeSelect("select", model, refEntityName, refEntityAttributeId){
                @Override
                public String getDisplayValue(Long id) {
                    return getPrefix(id) + super.getDisplayValue(id);
                }

                @Override
                public boolean isRequired() {
                    return FormGroupAttributeSelect.this.isRequired();
                }

                @Override
                protected FilterWrapper<? extends Domain<?>> getFilterWrapper() {
                    FilterWrapper<? extends Domain<?>> filterWrapper = FormGroupAttributeSelect.this.getFilterWrapper();

                    return filterWrapper != null ? filterWrapper : super.getFilterWrapper();
                }
            };

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

    public FormGroupAttributeSelect(String id, IModel<? extends Domain<?>> domainModel, Long entityAttributeId,
                                    String refEntityName, Long refEntityAttributeId){
        this(id, NumberAttributeModel.of(domainModel, entityAttributeId), refEntityName, refEntityAttributeId);
    }

    public FormGroupAttributeSelect setRequired(boolean required){
        this.required = required;

        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public FormGroupAttributeSelect setNullValid(boolean nullValid){
        if (attributeSelect != null) {
            attributeSelect.setNullValid(nullValid);
        }

        return this;
    }

    protected String getPrefix(Long id) {
        return "";
    }

    public void onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        if (attributeSelect != null) {
            attributeSelect.add(OnChangeAjaxBehavior.onChange(onChange));
        }
    }

    protected FilterWrapper<? extends Domain<?>> getFilterWrapper() {
        return null;
    }

}
