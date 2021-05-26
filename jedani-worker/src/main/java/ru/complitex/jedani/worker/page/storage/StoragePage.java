package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
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
import ru.complitex.common.entity.Sort;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.panel.SelectPanel;
import ru.complitex.common.wicket.table.*;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.component.WorkerAutoCompleteList;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.mapper.TransferMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.jedani.worker.util.Storages;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 14:58
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class StoragePage extends BasePage {
    private final Logger log = LoggerFactory.getLogger(StoragePage.class);

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private StorageMapper storageMapper;

    @Inject
    private TransferMapper transferMapper;

    @Inject
    private NameService nameService;

    @Inject
    private WorkerService workerService;

    public StoragePage(PageParameters pageParameters) {
        super(pageParameters);

        Long storageId = pageParameters.get("id").toOptionalLong();

        boolean backToStorageList = pageParameters.get("nsl").isNull();

        Storage storage;

        boolean edit;

        if (storageId != null) {
            storage = domainService.getDomain(Storage.class, storageId);

            Long currentWorkerId = getCurrentWorker().getObjectId();

            boolean worker = Objects.equals(currentWorkerId, storage.getParentId());

            boolean workers = storage.getNumberValues(Storage.WORKERS).stream()
                    .anyMatch(id -> Objects.equals(id, currentWorkerId));

            edit = isAdmin() || worker || workers;

            if (!isAdmin()){
                boolean storages = storageMapper.getStorages(new FilterWrapper<>(new Storage())
                        .put(Storage.FILTER_CURRENT_WORKER, getCurrentWorker().getObjectId())
                        .put(Storage.FILTER_CITY, getCurrentWorker().getCityId()))
                        .stream().filter(s -> s.getCityId() != null)
                        .anyMatch(s -> Objects.equals(s.getObjectId(), storageId));

                if (!storages && !worker) {
                    throw new UnauthorizedInstantiationException(StoragePage.class);
                }
            }
        } else {
            storage = new Storage();

            storage.setType(StorageType.REAL);

            edit = isAdmin();
        }

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        Form<?> form = new Form<>("form");
        add(form);

        FormGroupTextField<?> storageIdFormGroup = new FormGroupTextField<>("storageId", Model.of(storageId));
        storageIdFormGroup.setEnabled(false);
        storageIdFormGroup.setVisible(storageId != null);
        form.add(storageIdFormGroup);

        Component city, workers, worker;

        form.add(city = new FormGroupDomainAutoComplete<>("city", City.class, City.NAME,
                new NumberAttributeModel(storage, Storage.CITY)){
            @Override
            public boolean isVisible() {
                return Objects.equals(storage.getType(), StorageType.REAL);
            }
        }.setRequired(true).setEnabled(edit));

        form.add(workers = new FormGroupPanel("workers", new WorkerAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                Model.of(storage.getOrCreateAttribute(Storage.WORKERS)))
                .setRequired(true)
                .setLabel(new ResourceModel("workers"))
                .setEnabled(edit)){
            @Override
            public boolean isVisible() {
                return Objects.equals(storage.getType(), StorageType.REAL);
            }
        });

        form.add(worker = new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                new PropertyModel<>(storage, "parentId"))
                .setRequired(true)
                .setLabel(new ResourceModel("worker"))
                .setEnabled(storageId == null)){
            @Override
            public boolean isVisible() {
                return Objects.equals(storage.getType(), StorageType.PERSONAL);
            }
        });

        form.add(new FormGroupSelectPanel("storageType", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(storage, Storage.TYPE), Arrays.asList(StorageType.REAL, StorageType.PERSONAL),
                new IChoiceRenderer<>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        switch (object.intValue()){
                            case (int) StorageType.REAL:
                                return getString("real");

                            case (int) StorageType.PERSONAL:
                                return getString("personal");
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
                .setVisible(storageId == null && isAdmin()));

        WebMarkupContainer tables = new WebMarkupContainer("tables");
        tables.setOutputMarkupId(true);
        form.add(tables);

        //Accept Modal

        Form<Transfer> acceptForm = new Form<>("acceptForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        add(acceptForm);

        AcceptModal acceptModal = new AcceptModal("acceptModal", storageId, t -> t.add(feedback, tables));
        acceptForm.add(acceptModal);

        //Transfer Modal

        Form<Transfer> transferModalForm = new Form<>("transferModalForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        add(transferModalForm);

        TransferModal transferModal = new TransferModal("transferModal", storageId, t -> t.add(feedback, tables));
        transferModalForm.add(transferModal);

        //Receive Modal

        Form<Transfer> receiveForm = new Form<>("receiveForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        add(receiveForm);

        ReceiveModal receiveModal = new ReceiveModal("receiveModal", storageId, t -> t.add(feedback, tables));
        receiveForm.add(receiveModal);

        //Reserve Modal

        Form<Transfer> reserveForm = new Form<>("reserveForm"){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        add(reserveForm);

        ReserveModal reserveModal = new ReserveModal("reserveModal");
        reserveForm.add(reserveModal);

        //Products

        Provider<Product> productProvider = new Provider<>(FilterWrapper.<Product>of(
                new Product(){{setParentId(storageId);}}).sort("id", false)) {
            @Override
            public List<Product> getList() {
                return  domainService.getDomains(Product.class, getFilterState());
            }

            @Override
            public Long getCount() {
                return domainService.getDomainsCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Product>> productForm = new FilterForm<>("productForm", productProvider){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        tables.add(productForm);

        List<IColumn<Product, Sort>> productColumns = new ArrayList<>();

        if (storageId != null) {
            productColumns.add(new DomainIdColumn<>());

            Entity productEntity = entityService.getEntity(Product.ENTITY_NAME);

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.NOMENCLATURE)
                    .withReferences(Nomenclature.class, Nomenclature.CODE, Nomenclature.NAME)));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.QUANTITY)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    super.populateItem(cellItem, componentId, rowModel);

                    Product product = rowModel.getObject();

                    if (edit && product.getNumber(Product.QUANTITY, 0L) > 0) {
                        cellItem.add(new CssClassNameAppender("pointer"));

                        cellItem.add(new AjaxEventBehavior("click") {
                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                transferModal.open(rowModel.getObject(), TransferRelocationType.RELOCATION, target);
                            }
                        });
                    }
                }
            });
            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.SENDING_QUANTITY)));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.RECEIVING_QUANTITY)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    super.populateItem(cellItem, componentId, rowModel);

                    Product product = rowModel.getObject();

                    if (edit && product.getNumber(Product.RECEIVING_QUANTITY, 0L) > 0){
                        cellItem.add(new CssClassNameAppender("pointer"));

                        cellItem.add(new AjaxEventBehavior("click") {
                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                receiveModal.open(product, TransferRelocationType.RELOCATION, target);
                            }
                        });
                    }
                }
            });

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.GIFT_QUANTITY)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    super.populateItem(cellItem, componentId, rowModel);

                    Product product = rowModel.getObject();

                    if (edit && product.getNumber(Product.GIFT_QUANTITY, 0L) > 0) {
                        cellItem.add(new CssClassNameAppender("pointer"));

                        cellItem.add(new AjaxEventBehavior("click") {
                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                transferModal.open(product, TransferRelocationType.GIFT, target);
                            }
                        });
                    }
                }
            });
            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.GIFT_SENDING_QUANTITY)));

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.GIFT_RECEIVING_QUANTITY)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    super.populateItem(cellItem, componentId, rowModel);

                    Product product = rowModel.getObject();

                    if (edit && product.getNumber(Product.GIFT_RECEIVING_QUANTITY, 0L) > 0){
                        cellItem.add(new CssClassNameAppender("pointer"));

                        cellItem.add(new AjaxEventBehavior("click") {
                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                receiveModal.open(product, TransferRelocationType.GIFT, target);
                            }
                        });
                    }
                }
            });

            productColumns.add(new DomainColumn<>(productEntity.getEntityAttribute(Product.RESERVE_QUANTITY)){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    super.populateItem(cellItem, componentId, rowModel);

                    Product product = rowModel.getObject();

                    if (edit && product.getNumber(Product.RESERVE_QUANTITY, 0L) > 0){
                        cellItem.add(new CssClassNameAppender("pointer"));

                        cellItem.add(new AjaxEventBehavior("click") {
                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                reserveModal.open(product, target);
                            }
                        });
                    }
                }
            });

            productColumns.add(new DomainActionColumn<>(){
                @Override
                public void populateItem(Item<ICellPopulator<Product>> cellItem, String componentId, IModel<Product> rowModel) {
                    cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            transferModal.open(rowModel.getObject(), null, target);
                        }
                    }.setIconType(GlyphIconType.share)).setVisible(edit));
                }
            });
        }

        Table<Product> productTable = new Table<>("table", productColumns, productProvider, 15, "storagePageProduct"){
            @Override
            public boolean isVisible() {
                return storageId != null;
            }
        };
        productTable.setVisible(storageId != null);
        productForm.add(productTable);

        //Transfers

        WebMarkupContainer transferHeader = new WebMarkupContainer("transferHeader");
        transferHeader.setVisible(storageId != null && edit);
        tables.add(transferHeader);

        Provider<Transfer> transferProvider = new Provider<>(FilterWrapper.of(
                new Transfer()).put("storageId", storageId).sort("id", null, false)) {

            @Override
            public List<Transfer> getList() {
                return transferMapper.getTransfers(getFilterState());
            }

            @Override
            public Long getCount() {
                return transferMapper.getTransfersCount(getFilterState());
            }
        };

        FilterForm<FilterWrapper<Transfer>> transferForm = new FilterForm<>("transferForm", transferProvider){
            @Override
            protected boolean wantSubmitOnParentFormSubmit() {
                return false;
            }
        };
        tables.add(transferForm);

        List<IColumn<Transfer, Sort>> transferColumns = new ArrayList<>();

        if (storageId != null && edit) {
            transferColumns.add(new DomainIdColumn<>());

            transferColumns.add(new AbstractDomainColumn<>(new ResourceModel("startDate"),
                    new Sort("startDate")) {
                @Override
                public Component newFilter(String componentId, Table<Transfer> table) {
                    return new DateFilter(componentId, new PropertyModel<>(transferForm.getModel(),"object.startDate"));
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId,
                                         IModel<Transfer> rowModel) {
                    cellItem.add(new DateTimeLabel(componentId,rowModel.getObject().getStartDate()));
                }
            });

            transferColumns.add(new AbstractDomainColumn<>("date", this) {
                @Override
                public Component newFilter(String componentId, Table<Transfer> table) {
                    return new DateFilter(componentId, new PropertyModel<>(transferForm.getModel(), "map.date"));
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId, IModel<Transfer> rowModel) {
                    cellItem.add(new Label(componentId, Dates.getDateText(rowModel.getObject().getDate())));
                }
            });

            Entity transferEntity = entityService.getEntity(Transfer.ENTITY_NAME);

            transferColumns.add(new DomainColumn<>(transferEntity.getEntityAttribute(Transfer.NOMENCLATURE)
                    .withReferences(Nomenclature.class, Nomenclature.CODE, Nomenclature.NAME)));

            transferColumns.add(new DomainColumn<>(transferEntity.getEntityAttribute(Transfer.QUANTITY)));
            transferColumns.add(new DomainColumn<>(transferEntity.getEntityAttribute(Transfer.STORAGE_FROM)){
                @Override
                protected String displayEntity(EntityAttribute entityAttribute, Long objectId) {
                    return Storages.getStorageLabel(objectId, domainService, nameService);
                }
            });
            transferColumns.add(new DomainColumn<>(transferEntity.getEntityAttribute(Transfer.STORAGE_TO)){
                @Override
                protected String displayEntity(EntityAttribute entityAttribute, Long objectId) {
                    return Storages.getStorageLabel(objectId, domainService, nameService);
                }
            });

            transferColumns.add(new AbstractDomainColumn<>(new ResourceModel("worker"),
                    new Sort("worker")) {
                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId,
                                         IModel<Transfer> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getSimpleWorkerLabel(rowModel.getObject().getWorkerIdTo())));
                }

                @Override
                public Component newFilter(String componentId, Table<Transfer> table) {
                    return new TextFilter<>(componentId, new PropertyModel<>(transferForm.getModel(), "map.worker"));
                }
            });

            transferColumns.add(new AbstractDomainColumn<>(new ResourceModel("client"),
                    new Sort("client")) {
                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId, IModel<Transfer> rowModel) {
                    Transfer transfer = rowModel.getObject();

                    cellItem.add(new Label(componentId, nameService.getFio(transfer.getLastNameIdTo(),
                            transfer.getFirstNameIdTo(), transfer.getMiddleNameIdTo())));
                }

                @Override
                public Component newFilter(String componentId, Table<Transfer> table) {
                    return new TextFilter<>(componentId, new PropertyModel<>(transferForm.getModel(), "map.client"));
                }
            });

            transferColumns.add(new AbstractDomainColumn<>(transferEntity
                    .getEntityAttribute(Transfer.RELOCATION_TYPE)) {
                @Override
                public Component newFilter(String componentId, Table<Transfer> table) {
                    Transfer transfer = (Transfer)((FilterWrapper<?>)transferForm.getModelObject()).getObject();

                    return new SelectPanel(componentId, new BootstrapSelect<>(SelectPanel.SELECT_COMPONENT_ID,
                            new NumberAttributeModel(transfer,
                                    Transfer.RELOCATION_TYPE), Arrays.asList(TransferRelocationType.RELOCATION, TransferRelocationType.GIFT),
                            new IChoiceRenderer<>() {
                                @Override
                                public Object getDisplayValue(Long object) {
                                    if (object != null) {
                                        switch (object.intValue()){
                                            case 1:
                                                return getString("relocation");
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
                                    return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                                }
                            }).with(new BootstrapSelectConfig().withNoneSelectedText("")));
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId,
                                         IModel<Transfer> rowModel) {
                    String resourceKey = null;

                    Long relocationType = rowModel.getObject().getRelocationType();

                    if (relocationType != null) {
                        switch (relocationType.intValue()){
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

            transferColumns.add(new AbstractDomainColumn<>("type", this) {
                @Override
                public Component newFilter(String componentId, Table<Transfer> table) {
                    Transfer transfer = (Transfer)((FilterWrapper<?>)transferForm.getModelObject()).getObject();

                    return new SelectPanel(componentId, new BootstrapSelect<>(SelectPanel.SELECT_COMPONENT_ID,
                            new NumberAttributeModel(transfer,
                                    Transfer.TYPE), Arrays.asList(TransferType.ACCEPT, TransferType.SELL,
                            TransferType.RELOCATION, TransferType.WITHDRAW, TransferType.RESERVE),
                            new IChoiceRenderer<>() {
                                @Override
                                public Object getDisplayValue(Long object) {
                                    if (object != null) {
                                        switch (object.intValue()){
                                            case 1:
                                                return getString("accept");
                                            case 2:
                                                return getString("sell");
                                            case 3:
                                                return getString("relocation");
                                            case 4:
                                                return getString("withdraw");
                                            case 5:
                                                return getString("reserve");
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
                                    return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                                }
                            }).with(new BootstrapSelectConfig().withNoneSelectedText("")));
                }

                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId,
                                         IModel<Transfer> rowModel) {
                    String resourceKey = null;

                    Long transferType = rowModel.getObject().getType();

                    if (transferType != null) {
                        switch (transferType.intValue()){
                            case 1:
                                resourceKey = "accept";
                                break;
                            case 2:
                                resourceKey = "sell";
                                break;
                            case 3:
                                resourceKey = "relocation";
                                break;
                            case 4:
                                resourceKey = "withdraw";
                                break;
                            case 5:
                                resourceKey = "reserve";
                                break;
                        }
                    }

                    cellItem.add(new Label(componentId, resourceKey != null ? new ResourceModel(resourceKey) : Model.of("")));
                }
            });

            transferColumns.add(new DomainActionColumn<>(){
                @Override
                public void populateItem(Item<ICellPopulator<Transfer>> cellItem, String componentId, IModel<Transfer> rowModel) {
                    Transfer transfer = rowModel.getObject();

                    boolean receive = Objects.equals(transfer.getType(), TransferType.RELOCATION) &&
                            Objects.equals(transfer.getStorageIdTo(), storageId) &&
                            transfer.getEndDate() == null;

                    cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.COMPONENT_ID,
                            Buttons.Type.Link) {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            receiveModal.open(rowModel.getObject(), target);
                        }
                    }.setIconType(GlyphIconType.check)).setVisible(receive));
                }
            });
        }

        Table<Transfer> transferDataTable = new Table<>("table", transferColumns, transferProvider, 15,
                "storagePageTransfer"){
            @Override
            public boolean isVisible() {
                return storageId != null;
            }

            @Override
            protected Item<Transfer> newRowItem(String id, int index, final IModel<Transfer> model) {
                Item<Transfer> rowItem = super.newRowItem(id, index, model);

                Transfer transfer = model.getObject();

                boolean receive = Objects.equals(transfer.getType(), TransferType.RELOCATION) &&
                        Objects.equals(transfer.getStorageIdTo(), storageId) &&
                        transfer.getEndDate() == null;

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
        transferDataTable.setVisible(storageId != null && edit);
        transferForm.add(transferDataTable);

        //Action

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    storage.setUserId(getCurrentUser().getId());

                    domainService.save(storage);

                    getSession().success(getString("info_saved"));

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
        }.setVisible(edit));

        form.add(new AjaxLink<Void>("accept") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                acceptModal.open(target);
            }
        }.setVisible(storageId != null && edit));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(StorageListPage.class);
            }
        }.setVisible(backToStorageList));
    }
}
