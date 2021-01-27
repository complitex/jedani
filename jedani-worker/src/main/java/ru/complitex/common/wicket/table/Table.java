package ru.complitex.common.wicket.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 17:09
 */
public class Table<T extends Serializable> extends DataTable<T, SortProperty> implements IAjaxIndicatorAware {
    private AjaxIndicatorAppender ajaxIndicatorAppender;

    private boolean hideOnEmpty = false;

    public Table(String id, List<? extends IColumn<T, SortProperty>> columns, Provider<T> provider,
                 FilterForm<FilterWrapper<T>> filterForm, long rowsPerPage, String tableKey) {
        super(id, columns, provider, rowsPerPage);

        ajaxIndicatorAppender = getColumns().stream().filter(c -> c instanceof DomainActionColumn)
                .findAny()
                .map(c -> ((DomainActionColumn<?>) c).getAjaxIndicatorAppender())
                .orElse(null);

        addTopToolbar(new AjaxFallbackHeadersToolbar<SortProperty>(this, provider){
            @Override
            public boolean isVisible() {
                return !hideOnEmpty || getRowCount() > 0;
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
                return !hideOnEmpty || getRowCount() > 0;
            }
        });

        IModel<Boolean> visibleModel = Model.of(false);

        addBottomToolbar(new NavigationToolbar(this, tableKey){
            @Override
            public boolean isVisible() {
                return visibleModel.getObject() || getRowCount() > 5;
            }

            @Override
            protected Component getPagingLeft(String id) {
                Component component =  Table.this.getPagingLeft(id);

                visibleModel.setObject(component != null);

                return component;
            }
        });
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        try {
            return ajaxIndicatorAppender != null ? ajaxIndicatorAppender.getMarkupId() : "none";
        } catch (Exception e) {
            return "none";
        }
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

    @Override
    protected void onInitialize() {
        super.onInitialize();

        getColumns().forEach(c -> {
            if (c instanceof DomainColumn){
                NonContextual.of(DomainColumn.class).inject((DomainColumn<?>) c);
            }
        });
    }

    protected Component getPagingLeft(String id){
        return null;
    }
}
