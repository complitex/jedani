package ru.complitex.common.wicket.form;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 15.11.2019 10:53 PM
 */
public abstract class LongChoiceRenderer implements IChoiceRenderer<Long> {

    @Override
    public String getIdValue(Long object, int index) {
        return object + "";
    }

    @Override
    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
        return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
    }
}
