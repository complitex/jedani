package ru.complitex.common.wicket.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:42
 */
public class AbstractFilter<T> extends GenericPanel<T> {
    private IModel<String> labelModel;

    public AbstractFilter(String id, IModel<T> model) {
        super(id, model);
    }

    public AbstractFilter<T> onChange(SerializableConsumer<AjaxRequestTarget> onChange){
        return this;
    }

    public IModel<String> getLabelModel() {
        return labelModel;
    }

    public void setLabelModel(IModel<String> labelModel) {
        this.labelModel = labelModel;
    }
}
