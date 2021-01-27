package ru.complitex.common.wicket.table;

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
                IColumn<T, Sort> column = item.getModelObject();

                if (column instanceof Column) {
                    item.add(new Label("header", ((Column<T>) column).getDisplayModel()));
                } else {
                    item.add(new EmptyPanel("header"));
                }
            }
        };

        headers.setReuseItems(true);

        add(headers);
    }
}
