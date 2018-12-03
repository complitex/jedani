package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
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

        FormGroup group = new FormGroup("group", label);
        group.add(domainAutoComplete = new DomainAutoComplete("input",
                entityService.getEntityAttribute(entityName, entityAttributeId), model));
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
        domainAutoComplete.setRequired(required);

        return this;
    }
}
