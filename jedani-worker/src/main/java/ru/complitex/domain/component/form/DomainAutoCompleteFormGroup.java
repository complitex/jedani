package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class DomainAutoCompleteFormGroup extends Panel{
    private DomainAutoComplete domainAutoComplete;

    public DomainAutoCompleteFormGroup(String id, String entityName, Long entityAttributeId, IModel<Long> model,
                                       boolean capitalize) {
        this(id, new ResourceModel(id), entityName, entityAttributeId, model, capitalize);
    }

    public DomainAutoCompleteFormGroup(String id, IModel<String> label, String entityName, Long entityAttributeId,
                                       IModel<Long> model, boolean capitalize) {
        super(id);

        setOutputMarkupId(true);

        FormGroup group = new FormGroup("group", label);
        group.add(domainAutoComplete = new DomainAutoComplete("input", entityName, entityAttributeId, model, capitalize));
        domainAutoComplete.getAutoCompleteTextField().setLabel(label);

        add(group);
    }

    public DomainAutoComplete getDomainAutoComplete(){
        return domainAutoComplete;
    }

    public String getInput(){
        return domainAutoComplete.getAutoCompleteTextField().getInput();
    }

    public DomainAutoCompleteFormGroup setRequired(boolean required){
        domainAutoComplete.getAutoCompleteTextField().setRequired(required);

        return this;
    }
}
