package ru.complitex.common.wicket.table;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.cdi.NonContextual;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 17:09
 */
public class Table<T extends Serializable> extends DataTable<T, Sort> implements IAjaxIndicatorAware {
    private AjaxIndicatorAppender ajaxIndicatorAppender;

    private boolean hideOnEmpty = false;

    private final Provider<T> provider;

    private final NavigationToolbar navigationToolbar;

    private final AbstractToolbar footer;

    public Table(String id, List<? extends IColumn<T, Sort>> columns, Provider<T> provider, long rowsPerPage, String tableKey) {
        super(id, columns, provider, rowsPerPage);

        setOutputMarkupId(true);

        getBottomToolbars().setOutputMarkupPlaceholderTag(true);
        getBottomToolbars().setOutputMarkupId(true);

        this.provider = provider;

        ajaxIndicatorAppender = getColumns().stream().filter(c -> c instanceof DomainActionColumn)
                .findAny()
                .map(c -> ((DomainActionColumn<?>) c).getAjaxIndicatorAppender())
                .orElse(null);

        addTopToolbar(new HeadersToolbar(this){
            @Override
            public boolean isVisible() {
                return !hideOnEmpty || getRowCount() > 0;
            }
        });

        IModel<Boolean> visibleModel = Model.of(false);

        footer = newFooter(this);

        if (footer != null) {
            addBottomToolbar(footer);
        }

        addBottomToolbar(navigationToolbar = new NavigationToolbar(this, tableKey){
            @Override
            public boolean isVisible() {
                return visibleModel.getObject() || getRowCount() > 5;
            }

            @Override
            protected Component newPagingLeft(String id) {
                Component component =  Table.this.newPagingLeft(id);

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

    protected Component newPagingLeft(String id){
        return null;
    }

    @Override
    protected WebMarkupContainer newBodyContainer(String id) {
        return (WebMarkupContainer) super.newBodyContainer(id).setOutputMarkupId(true);
    }

    public FilterWrapper<T> getFilterWrapper(){
        return provider.getFilterState();
    }

    public Provider<T> getProvider() {
        return provider;
    }

    public void update(AjaxRequestTarget target){
        target.add(getBody(), getBottomToolbars());
    }

    protected AbstractToolbar newFooter(Table<T> table) {
        return null;
    }

    protected boolean isRowClick() {
        return false;
    }

    protected void onRowClick(T object, AjaxRequestTarget target) {

    }

    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> model) {
        Item<T> item = super.newRowItem(id, index, model);

        if (isRowClick()) {
            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    onRowClick(item.getModelObject(), target);
                }
            });

            item.add(new CssClassNameAppender("pointer"));
        }

        return item;
    }


}
