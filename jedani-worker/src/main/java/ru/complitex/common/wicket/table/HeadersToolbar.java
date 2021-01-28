package ru.complitex.common.wicket.table;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.LoadableDetachableModel;
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
                    item.setOutputMarkupId(true);


                    Column<T> column = (Column<T>) item.getModelObject();

                    Component filter = column.newFilter("filter", table);

                    WebMarkupContainer container = new WebMarkupContainer("container");
                    container.setVisible(filter.getDefaultModelObject() != null);

                    item.add(container);

                    container.add(filter);


                    AjaxLink<String> link =  new AjaxLink<>("link") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            container.setVisible(true);

                            target.add(item);
                        }

                        @Override
                        public boolean isVisible() {
                            return !container.isVisible();
                        }
                    };

                    link.setOutputMarkupId(true);
                    link.setOutputMarkupPlaceholderTag(true);

                    item.add(link);

                    link.add(new Label("label", column.getDisplayModel()));


                    if (filter instanceof AbstractFilter) {  // todo
                        ((AbstractFilter<?>)filter).setLabelModel(column.getDisplayModel());

                        ((AbstractFilter<?>) filter).onChange(target -> {
                            if (filter.getDefaultModelObject() == null){
                                container.setVisible(false);

                                target.add(item);
                            }
                        });
                    }

                    item.add(new AttributeAppender("class", LoadableDetachableModel
                            .of(() -> container.isVisible() ? "filter" : null)));
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
