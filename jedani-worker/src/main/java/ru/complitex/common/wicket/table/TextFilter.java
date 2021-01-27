package ru.complitex.common.wicket.table;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:22
 */
public class TextFilter<T> extends Panel {
    private TextField<T> filter;

    private Integer size;

    public TextFilter(String id, IModel<T> model) {
        super(id);

        add(filter = new TextField<T>("filter", model){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                if (size != null){
                    tag.put("size", size);
                }
            }
        });
    }

    public TextField<T> getFilter() {
        return filter;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
