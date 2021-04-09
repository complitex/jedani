package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class FormGroupDomainAutoComplete<T extends Domain<T>> extends Panel{
    @Inject
    private EntityService entityService;

    private final DomainAutoComplete<T> domainAutoComplete;

    public FormGroupDomainAutoComplete(String id, Class<T> domainClass, Long entityAttributeId, IModel<Long> model) {
        this(id, new ResourceModel(id), domainClass, entityAttributeId, model);
    }

    public FormGroupDomainAutoComplete(String id, IModel<String> label, Class<T> domainClass, Long entityAttributeId,
                                       IModel<Long> model) {
        super(id);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        FormGroupBorder group = new FormGroupBorder("group", label){
            @Override
            protected boolean isRequired() {
                return domainAutoComplete.isRequired() ||
                        domainAutoComplete.getAutoCompleteTextField().isRequired();
            }
        };

        String entityName = Domains.getEntityName(domainClass);

        group.add(domainAutoComplete = new DomainAutoComplete<T>("input", domainClass,
                entityService.getEntityAttribute(entityName, entityAttributeId), model){
            @Override
            public boolean isEnabled() {
                return FormGroupDomainAutoComplete.this.isEnabled();
            }

            @Override
            protected T getFilterObject(String input) {
                T object = FormGroupDomainAutoComplete.this.getFilterObject(input);

                return object != null ? object : super.getFilterObject(input);
            }

            @Override
            protected String getTextValue(T domain) {
                String value = FormGroupDomainAutoComplete.this.getTextValue(domain);

                return value != null ? value : super.getTextValue(domain);
            }
        });

        domainAutoComplete.onChange(t -> {
            if (!domainAutoComplete.isError() && domainAutoComplete.isErrorRendered()) {
                t.appendJavaScript(group.getRemoveErrorJs());
            }

            FormGroupDomainAutoComplete.this.onUpdate(t);
        });

        domainAutoComplete.setLabel(label);

        add(group);
    }

    public String getInput(){
        return domainAutoComplete.getAutoCompleteTextField().getInput();
    }

    public Long getObjectId(){
        return domainAutoComplete.getModelObject();
    }

    public void setObjectId(Long objectId){
        domainAutoComplete.setModelObject(objectId);
    }

    public FormGroupDomainAutoComplete<T> setRequired(boolean required){
        domainAutoComplete.setRequired(required);

        return this;
    }

    public FormGroupDomainAutoComplete<T> setInputRequired(boolean required){
        domainAutoComplete.getAutoCompleteTextField().setRequired(required);

        return this;
    }

    @Override
    public void detachModels(){
        super.detachModels();

        domainAutoComplete.detachModels();
    }

    protected void onUpdate(AjaxRequestTarget target){

    }

    protected T getFilterObject(String input) {
        return null;
    }

    protected String getTextValue(T domain) {
        return null;
    }

}
