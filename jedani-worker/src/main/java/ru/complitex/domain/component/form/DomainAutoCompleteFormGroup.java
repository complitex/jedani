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
    public DomainAutoCompleteFormGroup(String id, String entityName, Long entityAttributeId, IModel<Long> model) {
        this(id, new ResourceModel(id), entityName, entityAttributeId, model);
    }

    public DomainAutoCompleteFormGroup(String id, IModel<String> label, String entityName, Long entityAttributeId, IModel<Long> model) {
        super(id);

        FormGroup group = new FormGroup("group", label);
        group.add(new DomainAutoComplete("input", entityName, entityAttributeId, model));

        add(group);
    }
}
