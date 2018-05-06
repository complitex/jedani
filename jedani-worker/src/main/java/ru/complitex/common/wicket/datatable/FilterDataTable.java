package ru.complitex.common.wicket.datatable;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.form.TextField;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 17:09
 */
public class FilterDataTable<T extends Serializable> extends DataTable<T, SortProperty>{
    public FilterDataTable(String id, List<? extends IColumn<T, SortProperty>> columns, DataProvider<T> dataProvider,
                           FilterForm<FilterWrapper<T>> filterForm, long rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);

        addTopToolbar(new AjaxFallbackHeadersToolbar<SortProperty>(this, dataProvider){
            @Override
            public boolean isVisible() {
                return FilterDataTable.this.getRowCount() > 0;
            }
        });
        addTopToolbar(new FilterToolbar(this, filterForm){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();

                visitChildren(TextField.class, (component, visit) ->
                        component.add(new AttributeModifier("class", "form-control")));
            }

            @Override
            public boolean isVisible() {
                return FilterDataTable.this.getRowCount() > 0;
            }
        });
        addBottomToolbar(new BootstrapNavigationToolbar(this));
    }
}
