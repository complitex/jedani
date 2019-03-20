package ru.complitex.jedani.worker.page.catalog;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.template.PackageTextTemplate;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.*;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.page.resource.ApexChartsJsResourceReference;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.ExchangeRateService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 14.03.2019 21:33
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ExchangeRatePage extends BasePage {
    @Inject
    private DomainService domainService;

    @Inject
    private ExchangeRateService exchangeRateService;

    private String data;

    public ExchangeRatePage(PageParameters pageParameters) {
        ExchangeRate exchangeRate = domainService.getDomain(ExchangeRate.class, pageParameters.get("id").toLongObject());

        Map<Date, Attribute> exchangeRateMap = exchangeRateService.loadValues(exchangeRate);

        List<Attribute> exchangeRateList = new ArrayList<>(exchangeRateMap.values());
        exchangeRateList.sort(Comparator.comparing(Attribute::getStartDate));

        data = exchangeRateList.stream().map(a -> "[" + a.getStartDate().getTime() + ", " + a.getText() + "]")
                .collect(Collectors.joining(","));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        add(new TextFieldFormGroup<>("name", Model.of(exchangeRate.getName())));
        add(new TextFieldFormGroup<>("code", Model.of(exchangeRate.getCode())));

        DataProvider<Attribute> dataProvider = new DataProvider<Attribute>(FilterWrapper.of(
                new Attribute(exchangeRate.getEntityName(), exchangeRate.getObjectId()))) {
            @Override
            public Iterator<? extends Attribute> iterator(long first, long count) {
                FilterWrapper<Attribute> filterWrapper = getFilterState();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);

                return exchangeRateService.getExchangeRateHistories(filterWrapper).iterator();
            }

            @Override
            public long size() {
                return exchangeRateService.getExchangeRateHistoriesCount(getFilterState());
            }
        };

        FilterDataForm<FilterWrapper<Attribute>> form =new FilterDataForm<>("form", dataProvider);
        add(form);

        List<IColumn<Attribute, SortProperty>> columns = new ArrayList<>();

        columns.add(new AbstractFilterColumn<Attribute>(new ResourceModel("date"), new SortProperty("date")) {
            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, Model.of(""), form);
            }

            @Override
            public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getStartDate()));
            }
        });

        columns.add(new AbstractFilterColumn<Attribute>(new ResourceModel("value"), new SortProperty("value")) {
            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, Model.of(""), form);
            }

            @Override
            public void populateItem(Item<ICellPopulator<Attribute>> cellItem, String componentId, IModel<Attribute> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getText()));
            }
        });

        FilterDataTable<Attribute> table = new FilterDataTable<>("table", columns, dataProvider, form, 10, "exchangeRagePage");
        form.add(table);

        add(new AjaxLink<Object>("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ExchangeRateListPage.class);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(ApexChartsJsResourceReference.INSTANCE));

        response.render(OnDomReadyHeaderItem.forScript(new PackageTextTemplate(getClass(),"ExchangeRatePage.js",
                "text/javascript", "UTF-8")
                .asString(new HashMap<String, String>(){{
                    put("data", data);
                }})));
    }
}
