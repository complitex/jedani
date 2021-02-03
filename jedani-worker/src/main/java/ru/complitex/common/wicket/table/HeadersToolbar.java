package ru.complitex.common.wicket.table;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
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

                    if (filter instanceof AbstractFilter) {
                        ((AbstractFilter<?>)filter).setLabelModel(column.getDisplayModel());

                        ((AbstractFilter<?>) filter).onChange(t -> t.add(table.getBody()));
                    }

                    WebMarkupContainer container = new WebMarkupContainer("container");

                    container.setVisible(filter.getDefaultModelObject() != null);
                    item.add(container);
                    item.add(new AttributeAppender("class", LoadableDetachableModel
                            .of(() -> container.isVisible() ? "filter" : null)));
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
                    link.add(new Label("label", column.getDisplayModel()));
                    item.add(link);

                    WebMarkupContainer ascending = new WebMarkupContainer("ascending"){
                        @Override
                        protected void onComponentTag(ComponentTag tag) {
                            super.onComponentTag(tag);

                            ISortState<Sort> state = table.getProvider().getSortState();

                            SortOrder order = state.getPropertySortOrder(column.getSortProperty());

                            switch (order) {
                                case NONE:
                                    tag.put("class", "glyphicon glyphicon-sort");
                                    break;
                                case ASCENDING:
                                    tag.put("class", "glyphicon glyphicon-sort-by-attributes");
                                    break;
                                case DESCENDING:
                                    tag.put("class", "glyphicon glyphicon-sort-by-attributes-alt");
                                    break;
                            }
                        }
                    };

                    ascending.setOutputMarkupId(true);

                    AjaxLink<?> sort = new AjaxLink<>("sort") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            ISortState<Sort> state = table.getProvider().getSortState();

                            Sort sort = column.getSortProperty();

                            SortOrder order = state.getPropertySortOrder(sort);

                            switch (order) {
                                case NONE:
                                case DESCENDING:
                                    state.setPropertySortOrder(sort, SortOrder.ASCENDING);
                                    break;
                                case ASCENDING:
                                    state.setPropertySortOrder(sort, SortOrder.DESCENDING);
                                    break;
                            }

                            table.setCurrentPage(0);

                            target.add(ascending, table.getBody());
                        }
                    };

                    sort.add(ascending);
                    container.add(sort);

                    container.add(new AjaxLink<>("clear") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            container.setVisible(false);

                            if (filter.getDefaultModelObject() != null){
                                filter.setDefaultModelObject(null);
                                target.add(table.getBody());
                            }

                            target.add(item);
                        }
                    });
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
