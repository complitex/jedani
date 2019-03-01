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
public class DomainAutoCompleteFormGroup extends Panel{
    @Inject
    private EntityService entityService;

    private DomainAutoComplete domainAutoComplete;

    public DomainAutoCompleteFormGroup(String id, String entityName, Long entityAttributeId, IModel<Long> model) {
        this(id, new ResourceModel(id), entityName, entityAttributeId, model);
    }

    public DomainAutoCompleteFormGroup(String id, IModel<String> label, String entityName, Long entityAttributeId,
                                       IModel<Long> model) {
        super(id);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        FormGroupBorder group = new FormGroupBorder("group", label);

        group.add(domainAutoComplete = new DomainAutoComplete("input", entityName,
                entityService.getEntityAttribute(entityName, entityAttributeId), model, t -> {
            if (!domainAutoComplete.isError() && domainAutoComplete.isErrorRendered()) {
                t.appendJavaScript(group.getRemoveErrorJs());
            }
        }));
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

    public DomainAutoCompleteFormGroup setRequired(boolean required){
        domainAutoComplete.setRequired(required);

        return this;
    }

    public DomainAutoCompleteFormGroup setInputRequired(boolean required){
        domainAutoComplete.getAutoCompleteTextField().setRequired(required);

        return this;
    }


}
