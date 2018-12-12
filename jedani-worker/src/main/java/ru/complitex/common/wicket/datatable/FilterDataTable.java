package ru.complitex.common.wicket.datatable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.TextField;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.datatable.DomainActionColumn;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 17:09
 */
public class FilterDataTable<T extends Serializable> extends DataTable<T, SortProperty> implements IAjaxIndicatorAware {
    private AjaxIndicatorAppender ajaxIndicatorAppender;

    private boolean hideOnEmpty = false;

    public FilterDataTable(String id, List<? extends IColumn<T, SortProperty>> columns, DataProvider<T> dataProvider,
                           FilterDataForm<FilterWrapper<T>> filterDataForm, long rowsPerPage, String tableKey) {
        super(id, columns, dataProvider, rowsPerPage);

        ajaxIndicatorAppender = getColumns().stream().filter(c -> c instanceof DomainActionColumn)
                .findAny()
                .map(c -> ((DomainActionColumn) c).getAjaxIndicatorAppender())
                .orElse(null);

        addTopToolbar(new AjaxFallbackHeadersToolbar<SortProperty>(this, dataProvider){
            @Override
            public boolean isVisible() {
                return !hideOnEmpty || FilterDataTable.this.getRowCount() > 0;
            }
        });

        addTopToolbar(new FilterDataToolbar(this, filterDataForm){
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();

                visitChildren(TextField.class, (component, visit) ->
                        component.add(new AttributeModifier("class", "form-control")));
            }

            @Override
            public boolean isVisible() {
                return !hideOnEmpty || FilterDataTable.this.getRowCount() > 0;
            }
        });

        addBottomToolbar(new NavigationToolbar(this, tableKey));
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return ajaxIndicatorAppender != null ? ajaxIndicatorAppender.getMarkupId() : "none";
    }

    public AjaxIndicatorAppender getAjaxIndicatorAppender() {
        return ajaxIndicatorAppender;
    }

    public void setAjaxIndicatorAppender(AjaxIndicatorAppender ajaxIndicatorAppender) {
        this.ajaxIndicatorAppender = ajaxIndicatorAppender;
    }

    public boolean isHideOnEmpty() {
        return hideOnEmpty;
    }

    public void setHideOnEmpty(boolean hideOnEmpty) {
        this.hideOnEmpty = hideOnEmpty;
    }
}
