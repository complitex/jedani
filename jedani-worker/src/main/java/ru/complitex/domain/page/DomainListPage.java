package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.datatable.DomainParentColumn;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
public class DomainListPage<T extends Domain> extends BasePage{
    public static final String CURRENT_PAGE_ATTRIBUTE = "_PAGE";

    @Inject
    private EntityMapper entityMapper;

    @Inject
    private DomainMapper domainMapper;

    private String entityName;

    private Class<? extends Page> editPageClass;
    private FilterDataTable<T> table;

    public <P extends WebPage> DomainListPage(String entityName, String parentEntityName, Long parentEntityAttributeId,
                          Class<P> editPageClass) {
        this.entityName = entityName;
        this.editPageClass = editPageClass;

        Entity entity = entityMapper.getEntity(entityName);

        add(new Label("header", entity.getValue() != null ? entity.getValue().getText() : "[" + entityName + "]")
                .setVisible(isShowHeader()));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        DataProvider<T> dataProvider = new DataProvider<T>(getNewFilterWrapper()) {
            @Override
            public Iterator<? extends T> iterator(long first, long count) {
                FilterWrapper<T> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                List<T> list = getDomains(filterWrapper);

                onDataLoad(list);

                return list.iterator();
            }

            @Override
            public long size() {
                return getDomainsCount(getFilterState());
            }

        };

        FilterForm<FilterWrapper<T>> filterForm = new FilterForm<>("form", dataProvider);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        List<IColumn<T, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());

        if (parentEntityName != null){
            columns.add(new DomainParentColumn<T>(entityMapper.getEntity(parentEntityName), parentEntityAttributeId) {
                @Override
                protected Domain getDomain(Long objectId) {
                    return domainMapper.getDomain(parentEntityName, objectId);
                }
            });
        }

        List<Long> entityAttributeIds = getEntityAttributeIds();

        getEntityAttributes(entity).stream()
                .filter(a -> entityAttributeIds == null || entityAttributeIds.contains(a.getEntityAttributeId()))
                .forEach(a -> columns.add(new DomainColumn<>(a)));

        onAddColumns(columns);

        columns.add(new DomainActionColumn<>(editPageClass));

        table = new FilterDataTable<T>("table", columns, dataProvider, filterForm, 15){
            @Override
            protected Item<T> newRowItem(String id, int index, IModel<T> model) {
                Item<T> item = super.newRowItem(id, index, model);

                onRowItem(item);

                return item;
            }
        };
        table.setCurrentPage((Long) Optional.ofNullable(getSession().getAttribute(getClass().getName() +
                CURRENT_PAGE_ATTRIBUTE)).orElse(0L));
        filterForm.add(table);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(editPageClass, new PageParameters().add("new", ""));
            }
        });
    }

    protected void onRowItem(Item<T> item){
        item.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                getSession().setAttribute(DomainListPage.this.getClass().getName() + CURRENT_PAGE_ATTRIBUTE, table.getCurrentPage());

                setResponsePage(editPageClass, new PageParameters().add("id", item.getModelObject().getObjectId()));
            }
        });

        item.add(new CssClassNameAppender("pointer"));
    }

    @SuppressWarnings("unchecked")
    protected FilterWrapper<T> getNewFilterWrapper(){
        return (FilterWrapper<T>) FilterWrapper.of(new Domain(entityName)); //todo abstract
    }


    @SuppressWarnings("unchecked")
    protected List<T> getDomains(FilterWrapper<T> filterWrapper) {
        return (List<T>) domainMapper.getDomains(filterWrapper);
    }

    protected Long getDomainsCount(FilterWrapper<T> filterWrapper) {
        return domainMapper.getDomainsCount(filterWrapper);
    }

    public DomainListPage(String entityName, Class<? extends WebPage> editPageClass) {
        this(entityName, null, null, editPageClass);
    }

    protected List<EntityAttribute> getEntityAttributes(Entity entity){
        return entity.getAttributes();
    }

    protected boolean isShowHeader(){
        return true;
    }

    protected void onDataLoad(List<T> list){
    }

    protected void onAddColumns(List<IColumn<T, SortProperty>> columns){
    }

    protected List<Long> getEntityAttributeIds(){
        return null;
    }
}
