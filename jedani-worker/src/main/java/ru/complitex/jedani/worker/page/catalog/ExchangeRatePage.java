package ru.complitex.jedani.worker.page.catalog;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.template.PackageTextTemplate;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.Column;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.entity.Rate;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.page.resource.ApexChartsJsResourceReference;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 14.03.2019 21:33
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class ExchangeRatePage extends BasePage {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private String data;

    public ExchangeRatePage(PageParameters pageParameters) {
        ExchangeRate exchangeRate = domainService.getDomain(ExchangeRate.class, pageParameters.get("id").toLongObject());

        Rate rate = new Rate();

        rate.setParentId(exchangeRate.getObjectId());

        List<Rate> rates = domainService.getDomains(Rate.class, FilterWrapper.of(rate)
                .sort("date", rate.getOrCreateAttribute(Rate.DATE), true));

        data = rates.stream().map(r -> "[" + r.getDate().getTime() + ", " + r.getRate().toPlainString() + "]")
                .collect(Collectors.joining(","));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        add(new FormGroupTextField<>("name", Model.of(exchangeRate.getName())));
        add(new FormGroupTextField<>("code", Model.of(exchangeRate.getCode())));

        Rate filterRate = new Rate();

        filterRate.setParentId(exchangeRate.getObjectId());

        FilterWrapper<Rate> filterWrapper = FilterWrapper.of(filterRate)
                .sort("date", filterRate.getOrCreateAttribute(Rate.DATE));

        Provider<Rate> provider = new Provider<Rate>(filterWrapper) {
            @Override
            public List<Rate> getList() {
                return domainService.getDomains(Rate.class, filterWrapper);
            }

            @Override
            public Long getCount() {
                return domainService.getDomainsCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Rate>> form = new FilterForm<>("form", provider);
        add(form);



        List<IColumn<Rate, Sort>> columns = new ArrayList<>();

        Entity rateEntity = entityService.getEntity(Rate.ENTITY_VALUE);

        columns.add(new DomainColumn<>(rateEntity.getEntityAttribute(Rate.DATE)));
        columns.add(new DomainColumn<>(rateEntity.getEntityAttribute(Rate.RATE)));

        columns.add(new Column<Rate>(null) {
            @Override
            public Component newFilter(String componentId, Table<Rate> table) {
                return new LinkPanel(componentId, new BootstrapAjaxButton(LinkPanel.COMPONENT_ID, Buttons.Type.Link){
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        target.add(table.getBody());
                    }
                }.setIconType(GlyphIconType.search));
            }

            @Override
            public void populateItem(Item<ICellPopulator<Rate>> cellItem, String componentId, IModel<Rate> rowModel) {
                cellItem.add(new EmptyPanel(componentId));
            }

            @Override
            public String getCssClass() {
                return "domain-id-column";
            }
        });

        Table<Rate> table = new Table<>("table", columns, provider, 15, "exchangeRagePage");
        form.add(table);

        add(new AjaxLink<>("back") {
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
