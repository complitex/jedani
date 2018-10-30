package ru.complitex.jedani.worker.util;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.name.service.NameService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 25.10.2018 18:07
 */
public class ProductUtil {
    public static void addStorageColumn(List<IColumn<Product, SortProperty>> columns, EntityAttribute entityAttribute,
                                  DomainService domainService, NameService nameService) {
        columns.add(new AbstractDomainColumn<Product>(Model.of(entityAttribute.getValue().getText()),
                new SortProperty(entityAttribute.getValueType().getKey(), entityAttribute)) {
            @Override
            public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                Domain storage = domainService.getDomain(Storage.class,
                        rowModel.getObject().getNumber(entityAttribute.getEntityAttributeId()));

                cellItem.add(new Label(componentId, getStorageLabel(storage, domainService, nameService)));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });
    }

    public static String getStorageLabel(Domain storage, DomainService domainService, NameService nameService) {
        String label = "";

        if (storage != null){
            Domain city = domainService.getDomain(City.ENTITY_NAME, storage.getNumber(Storage.CITY_ID));

            if (city != null){
                label += Attributes.capitalize(city.getValueText(City.NAME));
            }

            String workers = storage.getOrCreateAttribute(Storage.WORKER_IDS).getNumberValues().stream()
                    .map(id -> domainService.getDomain(Worker.ENTITY_NAME, id))
                    .map(w -> w.getText(Worker.J_ID) + " " +
                            nameService.getLastName(w.getNumber(Worker.LAST_NAME)) + " " +
                            nameService.getFirstName(w.getNumber(Worker.FIRST_NAME)) + " " +
                            nameService.getMiddleName(w.getNumber(Worker.MIDDLE_NAME)))
                    .collect(Collectors.joining(", "));

            label += " " + workers;
        }

        return label;
    }
}
