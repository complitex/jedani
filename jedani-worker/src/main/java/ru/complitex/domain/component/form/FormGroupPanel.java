package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 16:17
 */
public class FormGroupPanel extends Panel {
    public static final String COMPONENT_ID = "component";

    public FormGroupPanel(String id, Component component) {
        super(id);

        setOutputMarkupId(true);

        FormGroup group = new FormGroup("group", new ResourceModel(id));
        add(group);

        group.add(component);

    }
}
