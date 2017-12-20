package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.domain.component.DomainActionColumn;
import ru.complitex.domain.component.DomainColumn;
import ru.complitex.domain.component.DomainIdColumn;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.mapper.EntityMapper;
import ru.complitex.jedani.user.page.BasePage;

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
    private transient EntityMapper entityMapper;

    @Inject
    private transient DomainMapper domainMapper;

    public DomainListPage(String entityName, Class<? extends DomainEditPage> editPageClass) {
        Entity entity = entityMapper.getEntity(entityName);

        add(new Label("header", entity.getValue() != null ? entity.getValue().getText() : "[" + entityName + "]"));

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

                return domainMapper.getDomains(filterWrapper).iterator();
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
        entity.getAttributes().forEach(a -> columns.add(new DomainColumn(a)));
        columns.add(new DomainActionColumn(editPageClass));

        FilterDataTable<Domain> table = new FilterDataTable<>("table", columns, dataProvider, filterForm, 10);
        filterForm.add(table);
    }
}
