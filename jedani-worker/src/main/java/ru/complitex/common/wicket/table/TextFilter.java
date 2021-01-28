package ru.complitex.common.wicket.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:22
 */
public class TextFilter<T> extends AbstractFilter<T> {
    private final TextField<T> textField;

    private Integer size;

    public TextFilter(String id, IModel<T> model) {
        super(id, model);

        setRenderBodyOnly(true);

        add(textField = new TextField<T>("filter", model){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                if (size != null){
                    tag.put("size", size);
                }

                if (getLabelModel() != null){
                    tag.put("placeholder", getLabelModel().getObject());

                }
            }
        });
    }

    public TextField<T> getTextField() {
        return textField;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public void onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        textField.add(OnChangeAjaxBehavior.onChange(onChange));
    }
}
