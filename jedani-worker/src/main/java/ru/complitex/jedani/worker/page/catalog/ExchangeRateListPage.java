package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.ExchangeRateService;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 22:24
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class ExchangeRateListPage extends DomainListModalPage<ExchangeRate> {
    @Inject
    private ExchangeRateService exchangeRateService;

    public ExchangeRateListPage() {
        super(ExchangeRate.class);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(ExchangeRate.NAME).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.CODE).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.BASE_CURRENCY).withReference(Currency.ENTITY_NAME, Currency.NAME));
        list.add(entity.getEntityAttribute(ExchangeRate.COUNTER_CURRENCY).withReference(Currency.ENTITY_NAME, Currency.NAME));

        return list;
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected List<EntityAttribute> getEditEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();
        
        list.add(entity.getEntityAttribute(ExchangeRate.NAME).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.CODE).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.BASE_CURRENCY).withReference(Currency.ENTITY_NAME, Currency.NAME));
        list.add(entity.getEntityAttribute(ExchangeRate.COUNTER_CURRENCY).withReference(Currency.ENTITY_NAME, Currency.NAME));
        
        list.add(entity.getEntityAttribute(ExchangeRate.URI_XML).setStringType(StringType.DEFAULT));
        
        list.add(entity.getEntityAttribute(ExchangeRate.URI_DATE_PARAM).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.URI_DATE_FORMAT).setStringType(StringType.DEFAULT));
        
        list.add(entity.getEntityAttribute(ExchangeRate.XPATH_NAME).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.XPATH_CODE).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.XPATH_DATE).setStringType(StringType.DEFAULT));
        list.add(entity.getEntityAttribute(ExchangeRate.XPATH_VALUE).setStringType(StringType.DEFAULT));        

        return list;
    }

    @Override
    protected List<ExchangeRate> getDomains(FilterWrapper<ExchangeRate> filterWrapper) {
        List<ExchangeRate> list = super.getDomains(filterWrapper);

        list.forEach(r -> {
            String[] values = exchangeRateService.getValue(r.getUriXml(), r.getUriDateParam(), r.getUriDateFormat(),
                    LocalDate.now(), r.getXpathDate(), r.getXpathValue());

            if (values != null) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

                if (values[0].contains(".")){
                    dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                }else if (values[0].contains("/")){
                    dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                }

                r.getMap().put("date", LocalDate.parse(values[0], dateTimeFormatter));
                r.getMap().put("value", values[1].replace(",", "."));
            }
        });

        return list;
    }

    @Override
    protected void onInitColumns(List<IColumn<ExchangeRate, SortProperty>> columns) {
        columns.add(new AbstractDomainColumn<ExchangeRate>(new ResourceModel("date"), new SortProperty("date")) {
            @Override
            public void populateItem(Item<ICellPopulator<ExchangeRate>> cellItem, String componentId, IModel<ExchangeRate> rowModel) {
                LocalDate date = (LocalDate) rowModel.getObject().getMap().get("date");

                cellItem.add(new Label(componentId, date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : null));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.date"), form);
            }
        });

        columns.add(new AbstractDomainColumn<ExchangeRate>(new ResourceModel("value"), new SortProperty("value")) {
            @Override
            public void populateItem(Item<ICellPopulator<ExchangeRate>> cellItem, String componentId, IModel<ExchangeRate> rowModel) {
                cellItem.add(new Label(componentId, (Serializable) rowModel.getObject().getMap().get("value")));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.value"), form);
            }
        });
    }

    @Override
    protected void onRowClick(ExchangeRate object, AjaxRequestTarget target) {
        setResponsePage(ExchangeRatePage.class, new PageParameters().add("id", object.getObjectId()));
    }

    @Override
    protected boolean isEditEnabled() {
        return isAdmin();
    }

    @Override
    protected boolean isCreateEnabled() {
        return isAdmin();
    }
}
