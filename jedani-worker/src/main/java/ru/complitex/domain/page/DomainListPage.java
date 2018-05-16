package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
public class DomainListPage extends BasePage{
    @Inject
    private EntityMapper entityMapper;

    @Inject
    private DomainMapper domainMapper;

    public DomainListPage(String entityName, String parentEntityName, Long parentEntityAttributeId,
                          Class<? extends WebPage> editPageClass) {
        Entity entity = entityMapper.getEntity(entityName);

        add(new Label("header", entity.getValue() != null ? entity.getValue().getText() : "[" + entityName + "]")
                .setVisible(isShowHeader()));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        DataProvider<Domain> dataProvider = new DataProvider<Domain>(FilterWrapper.of(new Domain(entityName))) {
            @Override
            public Iterator<? extends Domain> iterator(long first, long count) {
                FilterWrapper<Domain> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                List<Domain> list =  domainMapper.getDomains(filterWrapper);

                onDataLoad(list);

                return list.iterator();
            }

            @Override
            public long size() {
                return domainMapper.getDomainsCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Domain>> filterForm = new FilterForm<>("form", dataProvider);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        List<IColumn<Domain, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn());

        if (parentEntityName != null){
            columns.add(new DomainParentColumn(entityMapper.getEntity(parentEntityName), parentEntityAttributeId) {
                @Override
                protected Domain getDomain(Long objectId) {
                    return domainMapper.getDomain(parentEntityName, objectId);
                }
            });
        }

        getEntityAttributes(entity).forEach(a -> columns.add(new DomainColumn(a)));

        onAddColumns(columns);

        columns.add(new DomainActionColumn(editPageClass));

        FilterDataTable<Domain> table = new FilterDataTable<>("table", columns, dataProvider, filterForm, 10);
        filterForm.add(table);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(editPageClass, new PageParameters().add("new", ""));
            }
        });
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

    protected void onDataLoad(List<Domain> list){
    }

    protected void onAddColumns(List<IColumn<Domain, SortProperty>> columns){
    }
}
