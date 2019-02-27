package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.TextDataFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.service.ExchangeRateService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 22:24
 */
public class ExchangeRateList extends DomainListModalPage<ExchangeRate> {
    @Inject
    private ExchangeRateService exchangeRateService;

    public ExchangeRateList() {
        super(ExchangeRate.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(ExchangeRate.NAME).setStringType(StringType.UPPER_UPPER_CASE));
        list.add(entity.getEntityAttribute(ExchangeRate.CODE));
        list.add(entity.getEntityAttribute(ExchangeRate.BASE_CURRENCY).withReference(Currency.ENTITY_NAME, Currency.NAME));
        list.add(entity.getEntityAttribute(ExchangeRate.COUNTER_CURRENCY).withReference(Currency.ENTITY_NAME, Currency.NAME));

        return list;
    }

    @Override
    protected List<EntityAttribute> getEditEntityAttributes(Entity entity) {
        entity.getEntityAttribute(ExchangeRate.CODE).setStringType(StringType.UPPER_UPPER_CASE);
        entity.getEntityAttribute(ExchangeRate.URI_XML).setStringType(StringType.DEFAULT);
        entity.getEntityAttribute(ExchangeRate.XPATH_NAME).setStringType(StringType.DEFAULT);
        entity.getEntityAttribute(ExchangeRate.XPATH_CODE).setStringType(StringType.DEFAULT);
        entity.getEntityAttribute(ExchangeRate.XPATH_DATE).setStringType(StringType.DEFAULT);
        entity.getEntityAttribute(ExchangeRate.XPATH_VALUE).setStringType(StringType.DEFAULT);

        return entity.getAttributes();
    }

    @Override
    protected void onAddColumns(List<IColumn<ExchangeRate, SortProperty>> columns) {
        columns.add(new AbstractDomainColumn<ExchangeRate>(new ResourceModel("date"), new SortProperty("date")) {
            @Override
            public void populateItem(Item<ICellPopulator<ExchangeRate>> cellItem, String componentId, IModel<ExchangeRate> rowModel) {
                ExchangeRate exchangeRate = rowModel.getObject();

                cellItem.add(new Label(componentId, exchangeRateService.getValue(exchangeRate.getUriXml(), exchangeRate.getXpathDate())));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.date"), form);
            }
        });

        columns.add(new AbstractDomainColumn<ExchangeRate>(new ResourceModel("value"), new SortProperty("value")) {
            @Override
            public void populateItem(Item<ICellPopulator<ExchangeRate>> cellItem, String componentId, IModel<ExchangeRate> rowModel) {
                ExchangeRate exchangeRate = rowModel.getObject();

                cellItem.add(new Label(componentId, exchangeRateService.getValue(exchangeRate.getUriXml(), exchangeRate.getXpathValue())));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.value"), form);
            }
        });
    }
}
