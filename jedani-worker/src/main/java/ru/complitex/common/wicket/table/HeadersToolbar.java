package ru.complitex.common.wicket.table;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import ru.complitex.common.entity.Sort;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Ivanov Anatoliy
 */
public class HeadersToolbar extends AbstractToolbar {
    public <T extends Serializable> HeadersToolbar(Table<T> table) {
        super(table);

        ListView<IColumn<T, Sort>> headers = new ListView<>("headers", new ArrayList<>(table.getColumns())) {
            @Override
            protected void populateItem(ListItem<IColumn<T, Sort>> item) {
                if (item.getModelObject() instanceof Column) {
                    Column<T> column = (Column<T>) item.getModelObject();

                    AjaxLink<String> link =  new AjaxLink<>("link") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {

                        }

                        @Override
                        public boolean isVisible() {
                            return super.isVisible();
                        }
                    };

                    link.add(new Label("label", column.getDisplayModel()));

                    item.add(link);

                    item.add(new EmptyPanel("input"));
                } else {
                    item.add(new EmptyPanel("link"));
                    item.add(new EmptyPanel("input"));
                }
            }
        };

        headers.setReuseItems(true);

        add(headers);
    }
}
