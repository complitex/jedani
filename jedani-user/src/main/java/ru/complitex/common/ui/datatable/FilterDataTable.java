package ru.complitex.common.ui.datatable;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.form.TextField;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 17:09
 */
public class FilterDataTable<T> extends DataTable<T, String>{
    public FilterDataTable(String id, List<? extends IColumn<T, String>> iColumns, DataProvider<T> dataProvider,
                           FilterForm<T> filterForm, long rowsPerPage) {
        super(id, iColumns, dataProvider, rowsPerPage);

        addTopToolbar(new AjaxFallbackHeadersToolbar<>(this, dataProvider));
        addTopToolbar(new FilterToolbar(this, filterForm){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();

                visitChildren(TextField.class, (component, visit) ->
                        component.add(new AttributeModifier("class", "form-control")));
            }
        });
        addBottomToolbar(new BootstrapNavigationToolbar(this));
    }
}
