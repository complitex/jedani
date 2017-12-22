package ru.complitex.common.wicket.form;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class HorizontalInputPanel<T> extends Panel{
    public HorizontalInputPanel(String id, IModel<T> model) {
        this(id, new ResourceModel(id), model);
    }

    public HorizontalInputPanel(String id, IModel<String> label, IModel<T> model) {
        super(id);

        HorizontalFormGroup group = new HorizontalFormGroup("group", label);
        add(group);

        group.add(new TextField<>("input", model));
    }
}
