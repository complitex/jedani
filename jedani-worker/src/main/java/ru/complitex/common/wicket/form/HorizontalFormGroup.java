package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormGroup;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 7:39
 */
public class HorizontalFormGroup extends FormGroup{
    public HorizontalFormGroup(String id) {
        super(id);
    }

    public HorizontalFormGroup(String id, IModel<String> label) {
        super(id, label);
    }

    public HorizontalFormGroup(String id, IModel<String> label, IModel<String> help) {
        super(id, label, help);
    }
}
