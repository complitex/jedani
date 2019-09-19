package ru.complitex.common.wicket.form;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 16:17
 */
public class FormGroupPanel extends Panel {
    public static final String COMPONENT_ID = "component";

    private Component component;

    public FormGroupPanel(String id, Component component) {
        super(id);

        this.component = component;

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        FormGroupBorder group = new FormGroupBorder("group", getLabelModel(id)){
            @Override
            protected boolean isRequired() {
                if (component instanceof FormComponent){
                    return ((FormComponent) component).isRequired();
                }

                return false;
            }
        };
        add(group);

        group.add(component);

        if (component instanceof FormComponent){
            ((FormComponent) component).setLabel(getLabelModel(id));
        }
    }

    protected IModel<String> getLabelModel(String id) {
        return new ResourceModel(id);
    }

    public FormGroupPanel setRequired(boolean required){
        if (component instanceof FormComponent){
            ((FormComponent) component).setRequired(required);
        }

        return this;
    }

}
