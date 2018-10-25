package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 22.10.2018 16:16
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ProductListPage extends DomainListPage<Product> {
    @Inject
    private EntityService entityService;

    @Inject
    private NameService nameService;

    @Inject
    private DomainService domainService;

    public ProductListPage() {
        super(Product.class, ProductEditPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Product.NOMENCLATURE_ID)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Product, SortProperty>> columns) {
        addStorageColumn(columns, entityService.getEntityAttribute(Product.ENTITY_NAME, Product.STORAGE_ID));
        addStorageColumn(columns, entityService.getEntityAttribute(Product.ENTITY_NAME, Product.STORAGE_INTO_ID));
    }

    private void addStorageColumn(List<IColumn<Product, SortProperty>> columns, EntityAttribute entityAttribute) {
        columns.add(new AbstractDomainColumn<Product>(Model.of(entityAttribute.getValue().getText()),
                new SortProperty(entityAttribute.getValueType().getKey(), entityAttribute)) {
            @Override
            public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                Domain storage = domainService.getDomainWithNumberValues(Storage.ENTITY_NAME,
                        rowModel.getObject().getNumber(entityAttribute.getEntityAttributeId()));

                cellItem.add(new Label(componentId, getStorageLabel(storage)));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });
    }

    private String getStorageLabel(Domain storage) {
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
