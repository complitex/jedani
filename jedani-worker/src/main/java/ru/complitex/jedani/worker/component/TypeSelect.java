package ru.complitex.jedani.worker.component;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.02.2019 21:52
 */
public class TypeSelect extends DropDownChoice<Long> {
    public TypeSelect(String id, String key, IModel<Long> model, Long... types) {
        super(id, model, Arrays.asList(types));

        setChoiceRenderer(new IChoiceRenderer<>() {
            @Override
            public Object getDisplayValue(Long object) {
                return getString((key != null ? key : id) + "." + object);
            }

            @Override
            public String getIdValue(Long object, int index) {
                return object + "";
            }

            @Override
            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
            }
        });

        setNullValid(true);
    }

    public TypeSelect(String id, IModel<Long> model, Long... types) {
        this(id, null, model, types);
    }
}
