package ru.complitex.domain.component.form;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.service.EntityService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class FormGroupDomainAutoComplete extends Panel{
    @Inject
    private EntityService entityService;

    private DomainAutoComplete domainAutoComplete;

    public FormGroupDomainAutoComplete(String id, String entityName, Long entityAttributeId, IModel<Long> model) {
        this(id, new ResourceModel(id), entityName, entityAttributeId, model);
    }

    public FormGroupDomainAutoComplete(String id, IModel<String> label, String entityName, Long entityAttributeId,
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

        group.add(domainAutoComplete = new DomainAutoComplete("input", entityName,
                entityService.getEntityAttribute(entityName, entityAttributeId), model, t -> {
            if (!domainAutoComplete.isError() && domainAutoComplete.isErrorRendered()) {
                t.appendJavaScript(group.getRemoveErrorJs());
            }
        }){
            @Override
            public boolean isEnabled() {
                return FormGroupDomainAutoComplete.this.isEnabled();
            }
        });
        domainAutoComplete.setLabel(label);

        add(group);
    }

    public DomainAutoComplete getDomainAutoComplete(){
        return domainAutoComplete;
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

    public FormGroupDomainAutoComplete setRequired(boolean required){
        domainAutoComplete.setRequired(required);

        return this;
    }

    public FormGroupDomainAutoComplete setInputRequired(boolean required){
        domainAutoComplete.getAutoCompleteTextField().setRequired(required);

        return this;
    }

    @Override
    public void detachModels(){
        super.detachModels();

        domainAutoComplete.detachModels();
    }

}
