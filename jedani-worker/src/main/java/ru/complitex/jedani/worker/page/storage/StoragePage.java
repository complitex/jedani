package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.filter.BootstrapSelectFilter;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.DateFilter;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.component.WorkerAutoCompleteList;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.TransactionMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.StorageService;
import ru.complitex.jedani.worker.util.Workers;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.*;

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

    @Inject
    private TransactionMapper transactionMapper;

    @Inject
    private NameService nameService;

    public StoragePage(PageParameters pageParameters) {
        Long storageId = pageParameters.get("id").toOptionalLong();

        Storage storage;

        if (storageId != null) {
            storage = domainService.getDomain(Storage.class, storageId);
        } else {
            storage = new Storage();

            storage.setNumber(Storage.TYPE, StorageType.REAL);
        }

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form form = new Form<>("form");
        add(form);

        TextFieldFormGroup storageIdFormGroup = new TextFieldFormGroup<>("storageId", Model.of(storageId));
        storageIdFormGroup.setEnabled(false);
        storageIdFormGroup.setVisible(storageId != null);
        form.add(storageIdFormGroup);

        Component city, workers, worker;

        form.add(city = new DomainAutoCompleteFormGroup("city", City.ENTITY_NAME, City.NAME,
                new NumberAttributeModel(storage, Storage.CITY)){
            @Override
            public boolean isVisible() {
                return Objects.equals(storage.getNumber(Storage.TYPE), StorageType.REAL);
            }
        }.setRequired(true));

        form.add(workers = new FormGroupPanel("workers", new WorkerAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                Model.of(storage.getOrCreateAttribute(Storage.WORKERS))).setRequired(true)
                .setLabel(new ResourceModel("workers"))){
            @Override
            public boolean isVisible() {
                return Objects.equals(storage.getNumber(Storage.TYPE), StorageType.REAL);
            }
        });

        form.add(worker = new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                new PropertyModel<>(storage, "parentId")).setRequired(true)
                .setLabel(new ResourceModel("worker"))){
            @Override
            public boolean isVisible() {
                return Objects.equals(storage.getNumber(Storage.TYPE), StorageType.VIRTUAL);
            }
        });

        form.add(new FormGroupSelectPanel("storageType", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(storage, Storage.TYPE), Arrays.asList(StorageType.REAL, StorageType.VIRTUAL),
                new IChoiceRenderer<Long>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        switch (object.intValue()){
                            case (int) StorageType.REAL:
                                return getString("real");

                            case (int) StorageType.VIRTUAL:
                                return getString("virtual");
                        }

                        return null;
                    }

                    @Override
                    public String getIdValue(Long object, int index) {
                        return object + "";
                    }

                    @Override
                    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                        return Long.valueOf(id);
                    }
                }).setNullValid(false).add(OnChangeAjaxBehavior.onChange(target -> target.add(city, workers, worker))))
                .setVisible(storageId == null));

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
                storageService.accept(storageId, getModelObject());

                info(getString("info_accepted"));

                close(target);
                target.add(feedback, tables);
            }
        };
        acceptForm.add(acceptModal);

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

                Product product = domainService.getDomain(Product.class, getProduct().getObjectId());

                boolean gift = Objects.equals(transaction.getNumber(Transaction.TRANSFER_TYPE), TransferType.GIFT);

                Long tQty = transaction.getNumber(gift ? TransferType.GIFT : Transaction.QUANTITY);
                Long pQty = product.getNumber(gift ? TransferType.GIFT : Product.QUANTITY);

                if (tQty > pQty){
                    error(getString("error_quantity") + ": " + tQty + " > " + pQty);
                    target.add(getFeedback());

                    return;
                }

                if (tQty < 1){
                    error(getString("error_quantity") + ": " + tQty + " < 1 ");
                    target.add(getFeedback());

                    return;
                }

                switch (getTabIndexModel().getObject()){
                    case 0:
                        storageService.sell(product, transaction);
                        info(getString("info_sold"));

                        break;
                    case 1:
                        if (Objects.equals(transaction.getNumber(Transaction.RECIPIENT_TYPE), RecipientType.STORAGE)
                                && Objects.equals(transaction.getNumber(Transaction.STORAGE_TO), storageId)){
                            error(getString("error_same_storage"));
                            target.add(getFeedback());

                            return;
                        }

                        storageService.transfer(product, transaction);
                        info(getString("info_transferred"));

                        break;
                    case 2:
                        storageService.withdraw(product, transaction);
                        info(getString("info_withdrew"));

                        break;
                }

                close(target);
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
                storageService.receive(getTransaction());

                info(getString("info_received"));

                close(target);
                target.add(feedback, tables);
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

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.NOMENCLATURE)
                    .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)),
                    entityService, domainService));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.QUANTITY),
                    entityService, domainService));
            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.SENDING),
                    entityService, domainService));
            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.RECEIVING),
                    entityService, domainService));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.GIFT_QUANTITY),
                    entityService, domainService));
            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.GIFT_SENDING),
                    entityService, domainService));
            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.GIFT_RECEIVING),
                    entityService, domainService));

            productColumns.add(new DomainActionColumn<Product>(null, new PageParameters().add("storage_id", storageId)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    PageParameters pageParameters = new PageParameters().add("id", rowModel.getObject().getObjectId());
                    pageParameters.mergeWith(getEditPageParameters());

                    cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.LINK_COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            transferModal.open(rowModel.getObject(), target);
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
                        transferModal.open(model.getObject(), target);
                    }
                });

                rowItem.add(new CssClassNameAppender("pointer"));

                return rowItem;

            }
        };
        productTable.setVisible(storageId != null);
        productForm.add(productTable);

        //Transactions

        DataProvider<Transaction> transactionDataProvider = new DataProvider<Transaction>(FilterWrapper.of(
                new Transaction()).add("storageId", storageId)) {
            @Override
            public Iterator<? extends Transaction> iterator(long first, long count) {
                FilterWrapper<Transaction> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }

                return transactionMapper.getTransactions(filterWrapper).iterator();
            }

            @Override
            public long size() {
                return transactionMapper.getTransactionsCount(getFilterState());
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
                    return new DateFilter(componentId, new PropertyModel<>(form.getModel(),"object.startDate"), form);
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId,
                                         IModel<Transaction> rowModel) {
                    cellItem.add(new Label(componentId, DateFormatUtils.format(rowModel.getObject().getStartDate(),
                            "dd.MM.yyyy HH:mm:ss")));
                }
            });

            Entity transactionEntity = entityService.getEntity(Transaction.ENTITY_NAME);

            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.NOMENCLATURE)
                    .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.QUANTITY),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.STORAGE_FROM)
                    .setValueType(ValueType.NUMBER), entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.STORAGE_TO)
                    .setValueType(ValueType.NUMBER), entityService, domainService));

            transactionColumns.add(new AbstractDomainColumn<Transaction>(new ResourceModel("worker"),
                    new SortProperty("worker")) {
                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId,
                                         IModel<Transaction> rowModel) {
                    cellItem.add(new Label(componentId, Workers.getSimpleWorkerLabel(rowModel.getObject()
                                    .getNumber(Transaction.WORKER_TO), domainService, nameService)));
                }

                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    return new TextFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.worker"), form);
                }
            });

            transactionColumns.add(new AbstractDomainColumn<Transaction>(new ResourceModel("client"),
                    new SortProperty("client")) {
                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId, IModel<Transaction> rowModel) {
                    Transaction transaction = rowModel.getObject();

                    cellItem.add(new Label(componentId, nameService.getFio(transaction.getNumber(Transaction.LAST_NAME_TO),
                            transaction.getNumber(Transaction.FIRST_NAME_TO), transaction.getNumber(Transaction.MIDDLE_NAME_TO))));
                }

                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    return new TextFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.client"), form);
                }
            });

            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.SERIAL_NUMBER),
                    entityService, domainService));
            transactionColumns.add(new DomainColumn<>(transactionEntity.getEntityAttribute(Transaction.COMMENTS),
                    entityService, domainService));

            transactionColumns.add(new AbstractDomainColumn<Transaction>(transactionEntity
                    .getEntityAttribute(Transaction.TRANSFER_TYPE)) {
                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    Transaction transaction = (Transaction)((FilterWrapper)form.getModelObject()).getObject();

                    return new BootstrapSelectFilter<Long>(componentId, new NumberAttributeModel(transaction,
                            Transaction.TRANSFER_TYPE), form, Arrays.asList(TransferType.TRANSFER, TransferType.GIFT),
                            new IChoiceRenderer<Long>() {
                                @Override
                                public Object getDisplayValue(Long object) {
                                    if (object != null) {
                                        switch (object.intValue()){
                                            case 1:
                                                return getString("transfer");
                                            case 2:
                                                return getString("gift");
                                        }
                                    }

                                    return null;
                                }

                                @Override
                                public String getIdValue(Long object, int index) {
                                    return object + "";
                                }

                                @Override
                                public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                    return !id.isEmpty() ? Long.valueOf(id) : null;
                                }
                            }, false){
                        @Override
                        protected BootstrapSelect<Long> newDropDownChoice(String id, IModel<Long> model,
                                                                          IModel<? extends List<? extends Long>> choices,
                                                                          IChoiceRenderer<? super Long> renderer) {
                            return super.newDropDownChoice(id, model, choices, renderer)
                                    .with(new BootstrapSelectConfig().withNoneSelectedText(""));
                        }
                    };
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId,
                                         IModel<Transaction> rowModel) {
                    String resourceKey = null;

                    Long transferType = rowModel.getObject().getNumber(Transaction.TRANSFER_TYPE);

                    if (transferType != null) {
                        switch (transferType.intValue()){
                            case 1:
                                resourceKey = null;
                                break;
                            case 2:
                                resourceKey = "gift";
                                break;
                        }
                    }

                    cellItem.add(new Label(componentId, resourceKey != null ? new ResourceModel(resourceKey) : Model.of("")));
                }
            });

            transactionColumns.add(new AbstractDomainColumn<Transaction>(transactionEntity
                    .getEntityAttribute(Transaction.TYPE)) {
                @Override
                public Component getFilter(String componentId, FilterForm<?> form) {
                    Transaction transaction = (Transaction)((FilterWrapper)form.getModelObject()).getObject();

                    return new BootstrapSelectFilter<Long>(componentId, new NumberAttributeModel(transaction,
                            Transaction.TYPE), form, Arrays.asList(TransactionType.ACCEPT, TransactionType.SELL,
                            TransactionType.TRANSFER, TransactionType.WITHDRAW),
                            new IChoiceRenderer<Long>() {
                                @Override
                                public Object getDisplayValue(Long object) {
                                    if (object != null) {
                                        switch (object.intValue()){
                                            case 1:
                                                return getString("accept");
                                            case 2:
                                                return getString("sell");
                                            case 3:
                                                return getString("transfer");
                                            case 4:
                                                return getString("withdraw");
                                        }
                                    }

                                    return null;
                                }

                                @Override
                                public String getIdValue(Long object, int index) {
                                    return object + "";
                                }

                                @Override
                                public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                    return !id.isEmpty() ? Long.valueOf(id) : null;
                                }
                            }, false){
                        @Override
                        protected BootstrapSelect<Long> newDropDownChoice(String id, IModel<Long> model,
                                                                          IModel<? extends List<? extends Long>> choices,
                                                                          IChoiceRenderer<? super Long> renderer) {
                            return super.newDropDownChoice(id, model, choices, renderer)
                                    .with(new BootstrapSelectConfig().withNoneSelectedText(""));
                        }
                    };
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

            transactionColumns.add(new DomainActionColumn<Transaction>(null,
                    new PageParameters().add("storage_id", storageId)){
                @Override
                public void populateItem(Item<ICellPopulator<Transaction>> cellItem, String componentId, IModel<Transaction> rowModel) {
                    PageParameters pageParameters = new PageParameters().add("id", rowModel.getObject().getObjectId());
                    pageParameters.mergeWith(getEditPageParameters());

                    Transaction transaction = rowModel.getObject();

                    boolean receive = Objects.equals(transaction.getNumber(Transaction.TYPE), TransactionType.TRANSFER) &&
                            Objects.equals(transaction.getNumber(Transaction.STORAGE_TO), storageId) &&
                            transaction.getEndDate() == null;

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

                Transaction transaction = model.getObject();

                boolean receive = Objects.equals(transaction.getNumber(Transaction.TYPE), TransactionType.TRANSFER) &&
                        Objects.equals(transaction.getNumber(Transaction.STORAGE_TO), storageId) &&
                        transaction.getEndDate() == null;

                if (receive) {
                    rowItem.add(new AjaxEventBehavior("click") {
                        @Override
                        protected void onEvent(AjaxRequestTarget target) {
                            receiveModal.open(model.getObject(), target);
                        }
                    });

                    rowItem.add(new CssClassNameAppender("pointer"));
                }

                return rowItem;

            }
        };
        transactionDataTable.setVisible(storageId != null);
        transactionForm.add(transactionDataTable);

        //Action

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    storage.setUserId(getCurrentUser().getId());

                    domainService.save(storage);

                    getSession().info(getString("info_saved"));

                    if (storageId == null){
                        setResponsePage(StorageListPage.class);
                    }else{
                        target.add(feedback);
                    }
                } catch (Exception e) {
                    log.error("error save domain", e);

                    getSession().error("Ошибка сохранения " + e.getLocalizedMessage());

                    target.add(feedback);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        });

        form.add(new AjaxLink<Void>("accept") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                acceptModal.open(target);
            }
        }.setVisible(storageId != null));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(StorageListPage.class);
            }
        });
    }
}
