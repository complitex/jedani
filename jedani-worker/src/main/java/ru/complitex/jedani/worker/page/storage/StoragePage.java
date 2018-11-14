package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.component.form.FormGroupPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.WorkerAutoCompleteList;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Transaction;
import ru.complitex.jedani.worker.mapper.ProductMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 14:58
 */
public class StoragePage extends BasePage {
    private Logger log = LoggerFactory.getLogger(StoragePage.class);

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private NameService nameService;

    @Inject
    private ProductMapper productMapper;

    public StoragePage(PageParameters pageParameters) {
        Long storageId = pageParameters.get("id").toOptionalLong();

        Storage storage = storageId != null ? domainService.getDomain(Storage.class, storageId) : new Storage();

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);


        Form form = new Form<>("form");
        add(form);

        form.add(new DomainAutoCompleteFormGroup("city", City.ENTITY_NAME, City.NAME,
                new NumberAttributeModel(storage, Storage.CITY_ID), true));

        form.add(new FormGroupPanel("workers", new WorkerAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                Model.of(storage.getOrCreateAttribute(Storage.WORKER_IDS)))));


        form.add(new TextFieldFormGroup<>("productCount",  new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return domainService.getDomainsCount(FilterWrapper.of(new Product()));
            }
        }).setEnabled(false).setVisible(storageId != null));

        form.add(new TextFieldFormGroup<>("productIntoCount",  new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return domainService.getDomainsCount(FilterWrapper.of(new Product()));
            }
        }).setEnabled(false).setVisible(storageId != null));

        form.add(new TextFieldFormGroup<>("productFromCount",  new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return domainService.getDomainsCount(FilterWrapper.of(new Product()));
            }
        }).setEnabled(false).setVisible(storageId != null));


        //Accept Modal

        Form acceptForm = new Form<Transaction>("acceptForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(acceptForm);

        StorageAcceptModal acceptModal = new StorageAcceptModal("acceptModal", storageId);
        acceptModal.addAjaxUpdate(feedback);
        acceptForm.add(acceptModal);

        form.add(new AjaxLink<Void>("accept") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                acceptModal.open(target);
            }
        });


        //Send Modal

        Form transferForm = new Form<Transaction>("transferForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(transferForm);

        StorageTransferModal transferModal = new StorageTransferModal("transferModal");
        transferModal.addAjaxUpdate(feedback);
        transferForm.add(transferModal);


        //Receive Modal

        Form receiveForm = new Form<Transaction>("receiveForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(receiveForm);

        //Products

        DataProvider<Product> productDataProvider = new DataProvider<Product>(FilterWrapper.of(
                new Product()).add("storageId", storageId)) {
            @Override
            public Iterator<? extends Product> iterator(long first, long count) {
                return productMapper.getProducts(getFilterState().limit(first, count)).iterator();
            }

            @Override
            public long size() {
                return productMapper.getProductsCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Product>> productForm = new FilterForm<FilterWrapper<Product>>("productForm",
                productDataProvider){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(productForm);

        List<IColumn<Product, SortProperty>> productColumns = new ArrayList<>();

        if (storageId != null) {
            productColumns.add(new DomainIdColumn<>());

            Entity productEntity = entityService.getEntity(Product.ENTITY_NAME);

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.NOMENCLATURE_ID)
                    .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)),
                    entityService, domainService));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.QUANTITY),
                    entityService, domainService));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.SENT),
                    entityService, domainService));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.RECEIVED),
                    entityService, domainService));


            productColumns.add(new DomainActionColumn<Product>(StorageProductPage.class,
                    new PageParameters().add("storage_id", storageId)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    PageParameters pageParameters = new PageParameters().add("id", rowModel.getObject().getObjectId());
                    pageParameters.mergeWith(getEditPageParameters());

                    cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.LINK_COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            transferModal.open(1L, target);
                        }
                    }.setIconType(GlyphIconType.share)));
                }
            });
        }

        FilterDataTable<Product> productTable = new FilterDataTable<Product>("table", productColumns, productDataProvider,
                productForm, 7){
            @Override
            public boolean isVisible() {
                return storageId != null;
            }

            @Override
            protected Item<Product> newRowItem(String id, int index, final IModel<Product> model) {
                Item<Product> rowItem = super.newRowItem(id, index, model);

//                rowItem.add(new AjaxEventBehavior("click") {
//                    @Override
//                    protected void onEvent(AjaxRequestTarget target) {
//                        setResponsePage(StorageProductPage.class, new PageParameters()
//                                .add("id", model.getObject().getObjectId())
//                                .add("storage_id", storageId));
//                    }
//                });

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
        productTable.setVisible(storageId != null);
        productTable.setHideOnEmpty(true);
        productForm.add(productTable);

        //Transactions

        DataProvider<Transaction> transactionDataProvider = new DataProvider<Transaction>(FilterWrapper.of(
                new Transaction())) {
            @Override
            public Iterator<? extends Transaction> iterator(long first, long count) {
                return Collections.singletonList(new Transaction(){{setObjectId(2018L);}}).iterator();
            }

            @Override
            public long size() {
                return 1;
            }
        };

        FilterForm<FilterWrapper<Transaction>> transactionForm = new FilterForm<FilterWrapper<Transaction>>(
                "transactionForm", transactionDataProvider){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(transactionForm);

        List<IColumn<Transaction, SortProperty>> transactionColumns = new ArrayList<>();

        if (storageId != null) {
            transactionColumns.add(new DomainIdColumn<>());

            Entity transactionEntity = entityService.getEntity(Transaction.ENTITY_NAME);

            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.NOMENCLATURE_ID),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.QUANTITY),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.STORAGE_ID_FROM),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.STORAGE_ID_TO),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.WORKER_ID_TO),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.TYPE),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.SERIAL_NUMBER),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.COMMENTS),
                    entityService, domainService));

            transactionColumns.add(new DomainActionColumn<Transaction>(null,
                    new PageParameters().add("storage_id", storageId)){
                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId, IModel<Transaction> rowModel) {
                    PageParameters pageParameters = new PageParameters().add("id", rowModel.getObject().getObjectId());
                    pageParameters.mergeWith(getEditPageParameters());

                    cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.LINK_COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {

                        }
                    }.setIconType(GlyphIconType.check)));
                }
            });
        }

        FilterDataTable<Transaction> transactionDataTable = new FilterDataTable<Transaction>("table", transactionColumns,
                transactionDataProvider, transactionForm, 7){
            @Override
            public boolean isVisible() {
                return storageId != null;
            }

            @Override
            protected Item<Transaction> newRowItem(String id, int index, final IModel<Transaction> model) {
                Item<Transaction> rowItem = super.newRowItem(id, index, model);

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
        transactionDataTable.setVisible(storageId != null);
        transactionDataTable.setHideOnEmpty(true);
        transactionForm.add(transactionDataTable);

        //Action

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    storage.setUserId(getCurrentUser().getId());

                    domainService.save(storage);

                    getSession().info(getString("info_saved"));

                    target.add(feedback);
                } catch (Exception e) {
                    log.error("error save domain", e);

                    getSession().error("Ошибка сохранения " + e.getLocalizedMessage());

                    target.add(feedback);
                }
            }
        });

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(StorageListPage.class);
            }
        });




    }
}
