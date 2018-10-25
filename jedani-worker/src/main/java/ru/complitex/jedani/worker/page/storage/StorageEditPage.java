package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.WorkerAutoCompleteList;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.util.ProductUtil;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:48
 */
public class StorageEditPage extends DomainEditPage<Storage> {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private NameService nameService;

    public StorageEditPage(PageParameters parameters) {
        super(Storage.class, parameters, StorageListPage.class);

        Long id = parameters.get("id").toOptionalLong();

        WebMarkupContainer productContainer = new WebMarkupContainer("productContainer");
        productContainer.setVisible(id != null);
        getForm().add(productContainer);

        productContainer.add(new Label("productCount", new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return domainService.getDomainsCount(FilterWrapper.of(new Product()
                        .setNumber(Product.STORAGE_ID, id)
                        .setNumber(Product.STORAGE_INTO_ID, -1L)));
            }
        }));

        productContainer.add(new Label("productIntoCount", new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return domainService.getDomainsCount(FilterWrapper.of(new Product()
                        .setNumber(Product.STORAGE_INTO_ID, id)));
            }
        }));

        productContainer.add(new Label("productFromCount", new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return domainService.getDomainsCount(FilterWrapper.of(new Product()
                        .setNumber(Product.STORAGE_ID, id)
                        .setNumber(Product.STORAGE_INTO_ID, -2L)));
            }
        }));


        List<IColumn<Product, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());

        columns.add(new DomainColumn<>(entityService.getEntityAttribute(Product.ENTITY_NAME, Product.NOMENCLATURE_ID)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)),
                entityService, domainService));

        ProductUtil.addStorageColumn(columns, entityService.getEntityAttribute(Product.ENTITY_NAME, Product.STORAGE_INTO_ID),
                domainService, nameService);

        columns.add(new DomainActionColumn<>(ProductEditPage.class));


        Product product = new Product();
//        product.setNumber(Product.STORAGE_ID, id);
//        product.setNumber(Product.STORAGE_INTO_ID, id);

        DataProvider<Product> dataProvider = new DataProvider<Product>(FilterWrapper.of(product).setFilter("search")) {
            @Override
            public Iterator<Product> iterator(long first, long count) {
                FilterWrapper<Product> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null) {
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                return domainService.getDomains(Product.class, filterWrapper).iterator();
            }

            @Override
            public long size() {
                return domainService.getDomainsCount(getFilterState());
            }
        };


        FilterForm<FilterWrapper<Product>> filterForm = new FilterForm<>("form", dataProvider);
        getForm().stream().forEach(filterForm::add);
        getForm().replaceWith(filterForm);


        FilterDataTable<Product> table = new FilterDataTable<Product>("table", columns, dataProvider, filterForm, 7){
            @Override
            public boolean isVisible() {
                return id != null;
            }

            @Override
            protected Item<Product> newRowItem(String id, int index, final IModel<Product> model) {
                Item<Product> rowItem = super.newRowItem(id, index, model);

                rowItem.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        setResponsePage(ProductEditPage.class, new PageParameters().add("id", model.getObject().getObjectId()));
                    }
                });

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
        productContainer.add(table);
    }

    @Override
    protected void onAttribute(Attribute attribute) {
        if (Objects.equals(attribute.getEntityAttributeId(), Storage.CITY_ID)){
            attribute.getEntityAttribute()
                    .setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME));
        }
    }

    @Override
    protected Component getComponent(Attribute attribute) {
        if (Objects.equals(attribute.getEntityAttributeId(), Storage.WORKER_IDS)){
            return new WorkerAutoCompleteList(COMPONENT_WICKET_ID, Model.of(attribute));
        }

        return null;
    }
}
