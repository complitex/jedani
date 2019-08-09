package ru.complitex.common.wicket.form;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
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
        setOutputMarkupPlaceholderTag(true);

        FormGroupBorder group = new FormGroupBorder("group", new ResourceModel(id)){
            @Override
            protected boolean isRequired() {
                if (component instanceof FormComponent){
                    ((FormComponent) component).isRequired();
                }

                return false;
            }
        };
        add(group);

        group.add(component);

        if (component instanceof FormComponent){
            ((FormComponent) component).setLabel(new ResourceModel(id));
        }
    }
}
