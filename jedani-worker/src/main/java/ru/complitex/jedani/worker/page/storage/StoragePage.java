package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
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
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
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
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.StorageService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    private StorageService storageService;

    public StoragePage(PageParameters pageParameters) {
        Long storageId = pageParameters.get("id").toOptionalLong();

        Storage storage = storageId != null ? domainService.getDomain(Storage.class, storageId) : new Storage();

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form form = new Form<>("form");
        add(form);

        TextFieldFormGroup storageIdFormGroup = new TextFieldFormGroup<>("storageId", Model.of(storageId));
        storageIdFormGroup.getTextField().setEnabled(false);
        storageIdFormGroup.setVisible(storageId != null);
        form.add(storageIdFormGroup);

        form.add(new DomainAutoCompleteFormGroup("city", City.ENTITY_NAME, City.NAME,
                new NumberAttributeModel(storage, Storage.CITY_ID), true));

        form.add(new FormGroupPanel("workers", new WorkerAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                Model.of(storage.getOrCreateAttribute(Storage.WORKER_IDS)))));

        WebMarkupContainer tables = new WebMarkupContainer("tables");
        tables.setOutputMarkupId(true);
        form.add(tables);

        //Accept Modal

        Form acceptForm = new Form<Transaction>("acceptForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(acceptForm);

        AcceptModal acceptModal = new AcceptModal("acceptModal") {
            @Override
            void action(AjaxRequestTarget target) {
                Transaction transaction = getModelObject();

                transaction.setNumber(Transaction.STORAGE_ID_TO, storageId);
                transaction.setNumber(Transaction.TYPE, TransactionType.ACCEPT);

                try {
                    storageService.accept(transaction);

                    info(getString("info_accepted"));
                } catch (Exception e) {
                    error(getString("error_accepted"));
                } finally {
                    target.add(feedback, tables);
                }
            }
        };
        acceptForm.add(acceptModal);

        form.add(new AjaxLink<Void>("accept") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                acceptModal.open(target);
            }
        });

        //Transfer Modal

        Form transferForm = new Form<Transaction>("transferForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(transferForm);

        TransferModal transferModal = new TransferModal("transferModal") {
            @Override
            void action(AjaxRequestTarget target) {
                Transaction transaction = getModelObject();

                transaction.setNumber(Transaction.STORAGE_ID_TO, storageId);

                switch (getTabIndexModel().getObject()){
                    case 0:
                        try {
                            if (transaction.getNumber(Transaction.WORKER_ID_TO) == null){
                                getFeedback().error(getString("error_empty_worker"));

                                target.add(getFeedback());

                                return;
                            }

                            storageService.sell(transaction);

                            info(getString("info_sold"));
                        } catch (Exception e) {
                            log.error("error sell ", e);

                            error(getString("error_sell"));
                        }

                        break;
                    case 1:
                        try {
                            storageService.transfer(transaction);

                            info(getString("info_transferred"));
                        } catch (Exception e) {
                            log.error("error transfer ", e);

                            error(getString("error_transfer"));
                        }

                        break;
                    case 2:
                        try {
                            storageService.transfer(transaction);

                            info(getString("info_withdrew"));
                        } catch (Exception e) {
                            log.error("error withdraw ", e);

                            error(getString("error_withdraw"));
                        }

                        break;
                }

                appendCloseDialogJavaScript(target);
                target.add(feedback, tables);
            }
        };
        transferForm.add(transferModal);

        //Receive Modal

        Form receiveForm = new Form<Transaction>("receiveForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        form.add(receiveForm);

        ReceiveModal receiveModal = new ReceiveModal("receiveModal") {
            @Override
            void action(AjaxRequestTarget target) {
                try {
                    storageService.receive(getTransaction());

                    info(getString("info_received"));
                } catch (Exception e) {
                    error(getString("error_received"));
                } finally {
                    target.add(feedback, tables);
                }
            }
        };
        receiveForm.add(receiveModal);

        //Products

        DataProvider<Product> productDataProvider = new DataProvider<Product>(FilterWrapper.of(
                new Product(){{setParentId(storageId);}})) {
            @Override
            public Iterator<? extends Product> iterator(long first, long count) {
                FilterWrapper<Product> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
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

        FilterForm<FilterWrapper<Product>> productForm = new FilterForm<FilterWrapper<Product>>("productForm",
                productDataProvider){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        tables.add(productForm);

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
                            transferModal.open(rowModel.getObject().getObjectId(), target);
                        }
                    }.setIconType(GlyphIconType.share)));
                }
            });
        }

        FilterDataTable<Product> productTable = new FilterDataTable<Product>("table", productColumns, productDataProvider,
                productForm, 3){
            @Override
            public boolean isVisible() {
                return storageId != null;
            }

            @Override
            protected Item<Product> newRowItem(String id, int index, final IModel<Product> model) {
                Item<Product> rowItem = super.newRowItem(id, index, model);

                rowItem.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        transferModal.open(model.getObject().getObjectId(), target);
                    }
                });

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
        productTable.setVisible(storageId != null);
        productTable.setHideOnEmpty(true);
        productForm.add(productTable);

        //Transactions

        DataProvider<Transaction> transactionDataProvider = new DataProvider<Transaction>(FilterWrapper.of(
                new Transaction()).sort("id", null, false)) {
            @Override
            public Iterator<? extends Transaction> iterator(long first, long count) {
                FilterWrapper<Transaction> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                return domainService.getDomains(Transaction.class, filterWrapper).iterator();
            }

            @Override
            public long size() {
                return domainService.getDomainsCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Transaction>> transactionForm = new FilterForm<FilterWrapper<Transaction>>(
                "transactionForm", transactionDataProvider){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        tables.add(transactionForm);

        List<IColumn<Transaction, SortProperty>> transactionColumns = new ArrayList<>();

        if (storageId != null) {
            transactionColumns.add(new DomainIdColumn<>());

            transactionColumns.add(new AbstractDomainColumn<Transaction>(new ResourceModel("startDate"),
                    new SortProperty("startDate")) {
                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    return new TextFilter<>(componentId, Model.of(""), form);
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId,
                                         IModel<Transaction> rowModel) {
                    cellItem.add(new Label(componentId, DateFormatUtils.format(rowModel.getObject().getStartDate(),
                            "dd.MM.yyyy HH:mm:ss")));
                }
            });

            Entity transactionEntity = entityService.getEntity(Transaction.ENTITY_NAME);

            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.NOMENCLATURE_ID)
                    .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.QUANTITY),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.STORAGE_ID_FROM),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.STORAGE_ID_TO),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.WORKER_ID_TO),
                    entityService, domainService));

            transactionColumns.add(new AbstractDomainColumn<Transaction>(Model.of(transactionEntity
                    .getEntityAttribute(Transaction.TYPE).getValue().getText()),
                    new SortProperty("type")) {
                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    return new TextFilter<>(componentId, Model.of(""), form);
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId,
                                         IModel<Transaction> rowModel) {
                    String resourceKey = null;

                    Long transactionType = rowModel.getObject().getNumber(Transaction.TYPE);

                    if (transactionType != null) {
                        switch (transactionType.intValue()){
                            case 1:
                                resourceKey = "accept";
                                break;
                            case 2:
                                resourceKey = "sell";
                                break;
                            case 3:
                                resourceKey = "transfer";
                                break;
                            case 4:
                                resourceKey = "withdraw";
                                break;
                        }
                    }

                    cellItem.add(new Label(componentId, resourceKey != null ? new ResourceModel(resourceKey) : Model.of("")));
                }
            });

            transactionColumns.add(new AbstractDomainColumn<Transaction>(Model.of(transactionEntity
                    .getEntityAttribute(Transaction.TRANSFER_TYPE).getValue().getText()),
                    new SortProperty("transferType")) {
                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    return new TextFilter<>(componentId, Model.of(""), form);
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId,
                                         IModel<Transaction> rowModel) {
                    String resourceKey = null;

                    Long transferType = rowModel.getObject().getNumber(Transaction.TRANSFER_TYPE);

                    if (transferType != null) {
                        switch (transferType.intValue()){
                            case 1:
                                resourceKey = "transfer";
                                break;
                            case 2:
                                resourceKey = "gift";
                                break;
                        }
                    }

                    cellItem.add(new Label(componentId, resourceKey != null ? new ResourceModel(resourceKey) : Model.of("")));
                }
            });

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

                    Transaction transaction = rowModel.getObject();

                    boolean receive = Objects.equals(transaction.getNumber(Transaction.TYPE), TransactionType.TRANSFER) &&
                            Objects.equals(transaction.getNumber(Transaction.STORAGE_ID_TO), storageId);

                    cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.LINK_COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            receiveModal.open(rowModel.getObject(), target);
                        }
                    }.setIconType(GlyphIconType.check)).setVisible(receive));
                }
            });
        }

        FilterDataTable<Transaction> transactionDataTable = new FilterDataTable<Transaction>("table", transactionColumns,
                transactionDataProvider, transactionForm, 5){
            @Override
            public boolean isVisible() {
                return storageId != null;
            }

            @Override
            protected Item<Transaction> newRowItem(String id, int index, final IModel<Transaction> model) {
                Item<Transaction> rowItem = super.newRowItem(id, index, model);

                rowItem.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        receiveModal.open(model.getObject(), target);
                    }
                });


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
