package ru.complitex.common.wicket.table;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IStyledColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.NoFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.Sort;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 19:07
 */
public class FilterToolbar extends AbstractToolbar {
    private static final String FILTER_ID = "filter";

    public <T extends Serializable, F> FilterToolbar(Table<T> table, FilterForm<F> form) {
        super(table);

        Args.notNull(table, "table");

        IModel<List<IColumn<T, Sort>>> model = (IModel<List<IColumn<T, Sort>>>) () -> new LinkedList<>(table.getColumns());

        ListView<IColumn<T, Sort>> filters = new ListView<IColumn<T, Sort>>("filters", model) {

            @Override
            protected void populateItem(ListItem<IColumn<T, Sort>> item) {
                final IColumn<T, Sort> col = item.getModelObject();
                item.setRenderBodyOnly(true);

                Component filter = null;

                if (col instanceof Column) {
                    Column<T> filteredCol = (Column<T>) col;
                    filter = filteredCol.newFilter(FILTER_ID, table);
                }

                if (filter == null) {
                    filter = new NoFilter(FILTER_ID);
                } else {
                    if (!filter.getId().equals(FILTER_ID)) {
                        throw new IllegalStateException(
                                "filter component returned  with an invalid component id. invalid component id [" +
                                        filter.getId() +
                                        "] required component id [" +
                                        getId() +
                                        "] generating column [" + col.toString() + "] ");
                    }
                }

                if (col instanceof IStyledColumn) {
                    filter.add(new Behavior() {
                        @Override
                        public void onComponentTag(final Component component, final ComponentTag tag) {
                            String className = ((IStyledColumn<?, Sort>) col).getCssClass();
                            if (!Strings.isEmpty(className)) {
                                tag.append("class", className, " ");
                            }
                        }
                    });
                }

                item.add(filter);
            }
        };
        filters.setReuseItems(true);

        add(filters);
    }

    @Override
    protected void onBeforeRender() {
        if (findParent(FilterForm.class) == null) {
            throw new IllegalStateException("FilterToolbar must be contained within a FilterDataForm");
        }
        super.onBeforeRender();
    }

}
