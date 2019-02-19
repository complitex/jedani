package ru.complitex.jedani.worker.component;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.02.2019 21:52
 */
public class TypeSelect extends BootstrapSelect<Long> {
    public TypeSelect(String id, IModel<Long> model, Long... types) {
        super(id, model, Arrays.asList(types));

        setChoiceRenderer(new IChoiceRenderer<Long>() {
            @Override
            public Object getDisplayValue(Long object) {
                return getString(id + "." + object);
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

        with(new BootstrapSelectConfig().withNoneSelectedText(""));
    }
}
