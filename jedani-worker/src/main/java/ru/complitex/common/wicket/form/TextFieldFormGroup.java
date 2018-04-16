package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 8:03
 */
public class TextFieldFormGroup<T> extends Panel{
    public TextFieldFormGroup(String id, IModel<T> model) {
        this(id, new ResourceModel(id), model);
    }

    public TextFieldFormGroup(String id, IModel<String> label, IModel<T> model) {
        super(id);

        FormGroup group = new FormGroup("group", label);
        group.add(new TextField<>("input", model));

        add(group);
    }
}
