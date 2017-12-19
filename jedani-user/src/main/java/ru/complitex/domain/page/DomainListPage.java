package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.ui.datatable.DataProvider;
import ru.complitex.common.ui.datatable.FilterDataTable;
import ru.complitex.domain.component.DomainColumn;
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
    private EntityMapper entityMapper;

    @Inject
    private DomainMapper domainMapper;

    public DomainListPage(String entityName) {
        Entity entity = entityMapper.getEntity(entityName);

        add(new Label("header", entity.getValue().getText()));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        DataProvider<Domain> dataProvider = new DataProvider<Domain>(FilterWrapper.of(new Domain(entityName))) {
            @Override
            public Iterator<? extends Domain> iterator(long first, long count) {
                return domainMapper.getDomains(getFilterState().limit(first, count)).iterator();
            }

            @Override
            public long size() {
                return domainMapper.getDomainsCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Domain>> filterForm = new FilterForm<>("form", dataProvider);
        add(filterForm);

        List<IColumn<Domain, String>> columns = new ArrayList<>();

        entity.getAttributes().forEach(a -> columns.add(new DomainColumn(a)));

        FilterDataTable<Domain> table = new FilterDataTable<>("table", columns, dataProvider, filterForm, 10);
        filterForm.add(table);


        //todo action column, id column, parent column

    }
}