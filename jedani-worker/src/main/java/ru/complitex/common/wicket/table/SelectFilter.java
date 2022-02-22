package ru.complitex.common.wicket.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.jedani.worker.component.TypeSelect;

/**
 * @author Ivanov Anatoliy
 */
public class SelectFilter extends AbstractFilter<Long> {
    private final TypeSelect typeSelect;

    public SelectFilter(String id, String key, IModel<Long> model, Long... types) {
        super(id, model);

        add(typeSelect = new TypeSelect("select", key, model, types));
    }

    public SelectFilter(String id, IModel<Long> model, Long... types) {
        this(id, null, model, types);
    }

    @Override
    public AbstractFilter<Long> onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        typeSelect.add(OnChangeAjaxBehavior.onChange(onChange));

        return this;
    }
}
