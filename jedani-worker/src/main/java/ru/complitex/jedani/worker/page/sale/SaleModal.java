package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.spinner.Spinner;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.spinner.SpinnerConfig;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.form.*;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.*;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.component.StorageAutoComplete;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.SaleException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.PriceService;
import ru.complitex.jedani.worker.service.RewardService;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:23
 */
public class SaleModal extends Modal<Sale> {
    private final static Logger log = LoggerFactory.getLogger(SaleModal.class);

    @Inject
    private SaleService saleService;

    @Inject
    private NameService nameService;

    @Inject
    private DomainService domainService;

    @Inject
    private StorageMapper storageMapper;

    @Inject
    private PriceService priceService;

    @Inject
    private RewardService rewardService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private WorkerService workerService;

    private final IModel<Sale> saleModel;
    private final IModel<List<SaleItem>> saleItemsModel;

    private final WebMarkupContainer container;
    private final NotificationPanel feedback;

    private final WebMarkupContainer fioContainer;

    private final FormGroupDomainAutoComplete<LastName> lastName;
    private final FormGroupDomainAutoComplete<FirstName> firstName;
    private final FormGroupDomainAutoComplete<MiddleName> middleName;

    private final Component feeWithdraw;

    private final ListView<SaleItem> saleItems;

    private Long defaultStorageId;

    private final Component saveButton;

    private boolean culinaryEnabled = false;

    public SaleModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        saleModel = Model.of(new Sale().setDate(periodMapper.getActualPeriod().getOperatingMonth()));
        saleItemsModel = Model.ofList(new ArrayList<>());

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");

        container.setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true)
                .setVisible(false);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        container.add(new FormGroupPanel("sellerWorker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(saleModel, Sale.SELLER_WORKER)).setRequired(true)));

        container.add(new FormGroupDateTextField("saleDate", DateAttributeModel.of(saleModel, Sale.DATE))
                .setRequired(true).onUpdate(this::updatePrices));

        container.add(new FormGroupPanel("sasRequest", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.SAS_REQUEST), new ResourceModel("sasRequest")){
            @Override
            protected CheckBox newCheckBox(String id, IModel<Boolean> model) {
                CheckBox checkBox = super.newCheckBox(id, model);

                checkBox.setOutputMarkupId(true);

                return checkBox;
            }
        }){
            @Override
            protected IModel<String> getLabelModel(String id) {
                return Model.of("");
            }
        });

        container.add(new FormGroupPanel("forYourself", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.FOR_YOURSELF), new ResourceModel("forYourself")){
            @Override
            protected CheckBox newCheckBox(String id, IModel<Boolean> model) {
                CheckBox checkBox = super.newCheckBox(id, model);

                checkBox.setOutputMarkupId(true);

                checkBox.add(OnChangeAjaxBehavior.onChange(t -> {
                    Sale sale = saleModel.getObject();

                    sale.setBuyerLastName(null);
                    sale.setBuyerFirstName(null);
                    sale.setBuyerMiddleName(null);

                    t.add(fioContainer);
                }));

                return checkBox;
            }
        }){
            @Override
            protected IModel<String> getLabelModel(String id) {
                return Model.of("");
            }
        });

        container.add(feeWithdraw = new FormGroupPanel("feeWithdraw", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.FEE_WITHDRAW), new ResourceModel("feeWithdraw")){
            @Override
            protected CheckBox newCheckBox(String id, IModel<Boolean> model) {
                CheckBox checkBox = super.newCheckBox(id, model);

                checkBox.setOutputMarkupId(true);

                checkBox.add(OnChangeAjaxBehavior.onChange(SaleModal.this::updatePrices));

                return checkBox;
            }
        }){
            @Override
            protected IModel<String> getLabelModel(String id) {
                return Model.of("");
            }

            @Override
            public boolean isVisible() {
                return saleModel.getObject().getType() == SaleType.MYCOOK && saleModel.getObject().getInstallmentMonths() == 0;
            }
        });

        container.add(new FormGroupSelectPanel("saleType", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(saleModel, Sale.TYPE),
                Arrays.asList(SaleType.MYCOOK, SaleType.BASE_ASSORTMENT),
                new IChoiceRenderer<>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        switch (object.intValue()){
                            case (int) SaleType.MYCOOK:
                                return getString("mycook");

                            case (int) SaleType.BASE_ASSORTMENT:
                                return getString("baseAssortment");

                            default:
                                return null;
                        }
                    }

                    @Override
                    public String getIdValue(Long object, int index) {
                        return object + "";
                    }

                    @Override
                    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                        return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                    }
                })
                .with(new BootstrapSelectConfig().withNoneSelectedText(""))
                .setNullValid(false)
                .setRequired(true)
                .add(OnChangeAjaxBehavior.onChange(target -> {
                    saleItemsModel.getObject().clear();
                    saleItemsModel.getObject().add(new SaleItem());

                    updateTotal();
                    updateInitialPayment();

                    target.add(container);
                }))));

        container.add(new FormGroupTextField<>("contractNumber", new TextAttributeModel(saleModel, Sale.CONTRACT))
                .setRequired(true)
                .addValidator(new PatternValidator(MetaPattern.DIGITS){
                    @Override
                    protected IValidationError decorate(IValidationError error, IValidatable<String> validatable) {
                        ((ValidationError)error).setVariable("pattern", getString("error_digits_only"));

                        return error;
                    }
                }));

        fioContainer = new WebMarkupContainer("fioContainer"){
            @Override
            public boolean isVisible() {
                return !saleModel.getObject().isForYourself();
            }
        };
        fioContainer.setOutputMarkupPlaceholderTag(true);
        fioContainer.setOutputMarkupId(true);
        container.add(fioContainer);

        fioContainer.add(lastName = new FormGroupDomainAutoComplete<>("lastName", LastName.class, LastName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(firstName = new FormGroupDomainAutoComplete<>("firstName", FirstName.class, FirstName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(middleName = new FormGroupDomainAutoComplete<>("middleName", MiddleName.class, MiddleName.NAME,
                new Model<>()));

        container.add(new FormGroupPanel("storage", new StorageAutoComplete(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(saleModel, Sale.STORAGE)).onChange(t -> {
            updatePrices(t);

            container.visitChildren(NomenclatureAutoComplete.class, (c, v) -> {
                if (c.isVisibleInHierarchy()){
                    ((NomenclatureAutoComplete)c).getOnChange().accept(t);
                }
            });
        }).setRequired(true)));

        container.add(new FormGroupDomainAutoComplete<>("promotion", Promotion.class, Promotion.NAME,
                NumberAttributeModel.of(saleModel, Sale.PROMOTION)));

        FormGroupBorder months = new FormGroupBorder("months");
        container.add(months);

        months.add(new Spinner<>("input", NumberAttributeModel.of(saleModel, Sale.INSTALLMENT_MONTHS),
                new SpinnerConfig().withVerticalbuttons(true).withMin(0).withMax(24)).setType(Long.class)
                .add(OnChangeAjaxBehavior.onChange(target -> {
                    target.add(feeWithdraw);

                    updatePrices(target);
                })));

        container.add(new FormGroupPanel("managerMycookBonusWorker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(saleModel, Sale.MK_MANAGER_BONUS_WORKER))){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getType(), SaleType.MYCOOK);
            }
        });

        container.add(new FormGroupPanel("culinaryWorkshopWorker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(saleModel, Sale.CULINARY_WORKER))){
            @Override
            public boolean isEnabledInHierarchy() {
                Sale sale = saleModel.getObject();

                return Objects.equals(sale.getType(), SaleType.MYCOOK) &&
                        (sale.getCulinaryWorkerId() != null || culinaryEnabled);
            }

            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getType(), SaleType.MYCOOK);
            }
        });

        saleItems = new ListView<>("saleItems", saleItemsModel) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                item.setOutputMarkupId(true);

                IModel<SaleItem> model = item.getModel();

                item.add(new Label("index", item.getIndex() + 1));

                TextField<BigDecimal> basePrice = new TextField<>("basePrice", DecimalAttributeModel.of(model, SaleItem.BASE_PRICE), BigDecimal.class);
                basePrice.setOutputMarkupId(true);
                basePrice.setEnabled(false);
                basePrice.setRequired(true);
                item.add(basePrice);

                TextField<BigDecimal> price = new TextField<>("price", DecimalAttributeModel.of(model, SaleItem.PRICE), BigDecimal.class){
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);

                        SaleDecision saleDecision = domainService.getDomain(SaleDecision.class, model.getObject().getSaleDecisionId());

                        String title = (saleDecision != null ? saleDecision.getName() + "; " : "") +
                                (model.getObject().getRate() != null ? getString("rate") + ": " +
                                        model.getObject().getRate().toPlainString() : "");

                        tag.put("title",  title);
                    }

                    @Override
                    protected String getModelValue() {
                        return model.getObject().getQuantity() != null ? super.getModelValue() : null;
                    }
                };
                price.setOutputMarkupId(true);
                price.setEnabled(false);
                price.setRequired(true);
                item.add(price);

                TextField<Long> quantity = new TextField<>("quantity", NumberAttributeModel.of(model, SaleItem.QUANTITY), Long.class);
                quantity.setRequired(true)
                        .setOutputMarkupId(true)
                        .add(new AjaxFormInfoBehavior(){
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                super.onUpdate(target);

                                updatePrices(target);
                            }
                        });
                item.add(quantity);

                item.add(new NomenclatureAutoComplete("nomenclature", NumberAttributeModel.of(model,
                        SaleItem.NOMENCLATURE)){
                    @Override
                    protected Nomenclature getFilterObject(String input) {
                        Nomenclature nomenclature = super.getFilterObject(input);

                        Attribute attribute = new Attribute(Nomenclature.TYPE);
                        if (saleModel.getObject().getType().equals(SaleType.MYCOOK)) {
                            attribute.setNumber(NomenclatureType.MYCOOK);
                        }else{
                            attribute.setNumber(NomenclatureType.BASE_ASSORTMENT);
                        }
                        nomenclature.put(Domain.FILTER_ATTRIBUTES, Collections.singleton(attribute));

                        return nomenclature;
                    }
                }.onChange(target -> {
                    updateQuantity(model, target, quantity);
                    updatePrices(target);
                }).setRequired(true));

                item.add(new BootstrapAjaxLink<SaleItem>("update", Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        updatePrices(target);
                    }

                    @Override
                    public boolean isVisible() {
                        return container.isEnabled();
                    }
                }.setIconType(GlyphIconType.refresh));

                item.add(new BootstrapAjaxLink<SaleItem>("remove", Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        saleItemsModel.getObject().remove(item.getIndex());

                        target.add(container);
                    }

                    @Override
                    public boolean isVisible() {
                        return container.isEnabled();
                    }
                }.setIconType(GlyphIconType.remove));
            }
        };
        saleItems.setReuseItems(true);
        container.add(saleItems);

        container.add(new AjaxLink<SaleItem>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleItemsModel.getObject().add(new SaleItem());

                target.add(container);
            }

            @Override
            public boolean isVisible() {
                return container.isEnabled();
            }
        });

        container.add(new FormGroupTextField<>("total", DecimalAttributeModel.of(saleModel, Sale.TOTAL), BigDecimal.class)
                .setEnabled(false));
        container.add(new FormGroupTextField<>("totalLocal", DecimalAttributeModel.of(saleModel, Sale.TOTAL_LOCAL), BigDecimal.class)
                .setEnabled(false));
        container.add(new FormGroupTextField<>("payment", DecimalAttributeModel.of(saleModel, Sale.INITIAL_PAYMENT), BigDecimal.class){
            @Override
            public boolean isEnabled() {
                return !saleModel.getObject().isFeeWithdraw();
            }
        }.onUpdate(t -> updatePrices(t, false)));

        addButton(saveButton = new IndicatingAjaxButton(Modal.BUTTON_MARKUP_ID, new ResourceModel("save")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                SaleModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }

            @Override
            public boolean isVisible() {
                return container.isEnabled() || culinaryEnabled;
            }
        }.setOutputMarkupPlaceholderTag(true)
                .add(AttributeModifier.append("class", "btn btn-primary")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SaleModal.this.close(target, false);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    private void updateQuantity(IModel<SaleItem> model, AjaxRequestTarget target, Component quantity){
        Long storageId = saleModel.getObject().getStorageId();
        Long nomenclatureId = model.getObject().getNomenclatureId();

        if (storageId != null && nomenclatureId != null) {
            List<Product> products = domainService.getDomains(Product.class,
                    FilterWrapper.of((Product) new Product()
                            .setParentId(storageId)
                            .setNumber(Product.NOMENCLATURE, nomenclatureId)));

            target.appendJavaScript("$('#" + quantity.getMarkupId() + "').attr('placeholder', '" +
                    (!products.isEmpty() ? products.get(0).getAvailableQuantity() : "0") + "')");
        }
    }

    private void updateBasePrices(){
        Sale sale = saleModel.getObject();

        saleItemsModel.getObject().forEach(si -> si.setBasePrice(priceService.getBasePrice(sale.getStorageId(),
                si.getNomenclatureId(), sale.getDate())));
    }

    private void updatePrices(boolean updateInitialPayment){
        BigDecimal basePricesTotal = saleItemsModel.getObject().stream()
                .map(si -> si.getQuantity() != null && si.getBasePrice() != null
                        ? si.getBasePrice().multiply(new BigDecimal(si.getQuantity()))
                        : ZERO)
                .reduce(ZERO, BigDecimal::add);

        Sale sale = saleModel.getObject();

        if (sale.getType() != SaleType.MYCOOK || sale.getInstallmentMonths() != 0){
            sale.setFeeWithdraw(null);
        }

        saleItemsModel.getObject().forEach(si -> {
            SaleDecision saleDecision = priceService.getSaleDecision(sale.getStorageId(), si.getNomenclatureId(),
                    sale.getDate(), basePricesTotal, sale.getInstallmentMonths(), sale.isForYourself(), si.getQuantity(), null);

            BigDecimal price = priceService.getPrice(saleDecision, sale.getDate(), si.getBasePrice(), basePricesTotal,
                    sale.getInstallmentMonths(), sale.isForYourself(), si.getQuantity(), null);

            if (sale.isFeeWithdraw()){
                price = price.subtract(rewardService.getPersonalRewardPoint(sale, saleItemsModel.getObject()));
            }

            si.setPrice(price);
        });

        updateTotal();

        if (updateInitialPayment){
            updateInitialPayment();
        }

        Long paymentPercent = saleService.getPaymentPercent(sale).longValue();

        saleItemsModel.getObject().forEach(si -> {
            SaleDecision saleDecision = priceService.getSaleDecision(sale.getStorageId(), si.getNomenclatureId(),
                    sale.getDate(), basePricesTotal, sale.getInstallmentMonths(), sale.isForYourself(), si.getQuantity(), paymentPercent);

            si.setSaleDecisionId(saleDecision != null ? saleDecision.getObjectId() : null);

            BigDecimal price = priceService.getPrice(saleDecision, sale.getDate(), si.getBasePrice(), basePricesTotal,
                    sale.getInstallmentMonths(), sale.isForYourself(), si.getQuantity(), paymentPercent);

            if (sale.isFeeWithdraw()){
                price = price.subtract(rewardService.getPersonalRewardPoint(sale, saleItemsModel.getObject()));
            }

            si.setPrice(price);

            BigDecimal rate = priceService.getRate(sale.getStorageId(), sale.getDate());

            si.setRate(priceService.getRate(saleDecision, sale.getDate(), rate, basePricesTotal,
                    sale.getInstallmentMonths(), sale.isForYourself(), si.getQuantity(), paymentPercent));
        });

        updateTotal();

        if (updateInitialPayment){
            updateInitialPayment();
        }
    }

    private void updateTotal(){
        Sale sale = saleModel.getObject();
        List<SaleItem> saleItems = saleItemsModel.getObject();

        if (saleItems.stream().noneMatch(si -> si.getQuantity() == null || si.getPrice() == null)) {
            sale.setTotal(saleItems.stream().map(si -> si.getPrice().multiply(new BigDecimal(si.getQuantity())))
                    .reduce(ZERO, BigDecimal::add));
        }

        if (saleItems.stream().noneMatch(si -> si.getPrice() == null || si.getQuantity() == null || si.getRate() == null)){
            sale.setTotalLocal(saleItems.stream().map(si -> {
                if (sale.isFeeWithdraw() && sale.getSellerWorkerId() != null && sale.getDate() != null) {
                    BigDecimal reward = rewardService.getPersonalRewardPoint(sale, saleItemsModel.getObject());

                    BigDecimal price = si.getPrice().add(reward);

                    BigDecimal ratio = ZERO;

                    List<Ratio> ratios = domainService.getDomains(Ratio.class, FilterWrapper.of((Ratio) new Ratio()
                            .setCountryId(workerService.getCountryId(sale.getSellerWorkerId()))
                            .setBegin(sale.getDate())
                            .setEnd(sale.getDate())
                            .setFilter(Ratio.BEGIN, Attribute.FILTER_BEFORE_OR_EQUAL_DATE)
                            .setFilter(Ratio.END, Attribute.FILTER_AFTER_DATE)));

                    if (!ratios.isEmpty()) {
                        ratio = ratios.get(0).getValue();
                    }

                    BigDecimal feeWithdraw = reward.multiply(si.getRate()).multiply(BigDecimal.valueOf(100).subtract(ratio)).multiply(price)
                            .divide(si.getBasePrice().multiply(BigDecimal.valueOf(100)), 2, HALF_EVEN);

                    BigDecimal totalLocal =  price.multiply(new BigDecimal(si.getQuantity())).multiply(si.getRate()).setScale(2, HALF_EVEN);

                    return totalLocal.subtract(feeWithdraw);
                } else {
                    return si.getPrice().multiply(new BigDecimal(si.getQuantity()))
                            .multiply(si.getRate())
                            .setScale(2, HALF_EVEN);
                }
            }).reduce(ZERO, BigDecimal::add));
        }
    }

    private void updateInitialPayment(){
        Sale sale = saleModel.getObject();

        if (sale.getTotal() != null && sale.getInstallmentMonths() >= 0) {
            sale.setInitialPayment(sale.getTotal().divide(BigDecimal.valueOf(1 + sale.getInstallmentMonths()), 2,
                    HALF_EVEN));
        }
    }

    private void updatePrices(AjaxRequestTarget target){
        updatePrices(target, true);
    }

    private void updatePrices(AjaxRequestTarget target, boolean updateInitialPayment){
        try {
            updateBasePrices();
            updatePrices(updateInitialPayment);

            saleItems.stream().forEach(c -> target.add(c.get("price"), c.get("basePrice")));

            container.get("total").modelChanged();
            container.get("totalLocal").modelChanged();
            container.get("payment").modelChanged();

            target.add(container.get("total"), container.get("totalLocal"));

            if (updateInitialPayment){
                target.add(container.get("payment"));
            }
        } catch (Exception e) {
            log.error("error update prices", e);
        }
    }

    private void open(AjaxRequestTarget target){
        container.setVisible(true);
        target.add(container, saveButton);

        show(target);
    }

    private void close(AjaxRequestTarget target, boolean update){
        super.close(target);

        if (update) {
            onUpdate(target);
        }
    }

    void create(AjaxRequestTarget target){
        Long sellerWorkerId = getBasePage().getCurrentWorker().getObjectId();

        Long cityId = domainService.getNumber(City.ENTITY_NAME, sellerWorkerId, Worker.CITY);

        City city = domainService.getDomain(City.class, cityId);

        if (city != null){
            List<Storage> storages = storageMapper.getStorages(FilterWrapper.of(new Storage())
                    .put(Storage.FILTER_REGION, city.getParentId()));

            if (!storages.isEmpty()){
                defaultStorageId = storages.get(0).getObjectId();
            }
        }

        Sale sale = new Sale();

        sale.setSellerWorkerId(sellerWorkerId);
        sale.setType(SaleType.MYCOOK);
        sale.setDate(periodMapper.getActualOperatingMonth());
        sale.setInstallmentMonths(0L);
        sale.setStorageId(defaultStorageId);
        sale.setSaleStatus(SaleStatus.CREATED);

        saleModel.setObject(sale);

        lastName.setObjectId(null);
        firstName.setObjectId(null);
        middleName.setObjectId(null);

        saleItemsModel.setObject(new ArrayList<>());
        saleItemsModel.getObject().add(new SaleItem());

        container.setEnabled(true);

        open(target);
    }

    void edit(Long  saleId, AjaxRequestTarget target, boolean edit){
        Sale sale = saleService.getSale(saleId);

        saleModel.setObject(sale);

        lastName.setObjectId(sale.getBuyerLastName());
        firstName.setObjectId(sale.getBuyerFirstName());
        middleName.setObjectId(sale.getBuyerMiddleName());

        saleItemsModel.setObject(saleService.getSaleItems(sale.getObjectId()));

        container.setEnabled(edit && sale.getPeriodId() != null && periodMapper.getPeriod(sale.getPeriodId()).getCloseTimestamp() == null);

        culinaryEnabled = sale.getSaleStatus() == SaleStatus.PAID && sale.getCulinaryWorkerId() == null;

        open(target);
    }

    private void save(AjaxRequestTarget target){
        Sale sale = saleModel.getObject();

        List<Sale> sales = domainService.getDomains(Sale.class, FilterWrapper.of(new Sale().setContract(sale.getContract())));

        if (sales.stream().anyMatch(s -> !s.getObjectId().equals(sale.getObjectId()))){
            error(getString("error_sale_contract_exists"));

            target.add(feedback);

            return;
        }

        if (!sale.isForYourself() ){
            if (container.isEnabled()) {
                sale.setBuyerLastName(nameService.getOrCreateLastName(lastName.getInput(), lastName.getObjectId()));
                sale.setBuyerFirstName(nameService.getOrCreateFirstName(firstName.getInput(), firstName.getObjectId()));
                sale.setBuyerMiddleName(nameService.getOrCreateMiddleName(middleName.getInput(), middleName.getObjectId()));
            }
        }else{
            sale.setBuyerLastName(null);
            sale.setBuyerFirstName(null);
            sale.setBuyerMiddleName(null);
        }

        List<SaleItem> saleItems = saleItemsModel.getObject();

        if (saleItems == null || saleItems.isEmpty()){
            return;
        }

        for (SaleItem s : saleItems) {
            if (s.getPrice() == null) {
                error(getString("error_price"));

                target.add(feedback);

                return;
            }

            if (!saleService.validateQuantity(sale, s)) {
                Nomenclature n = domainService.getDomain(Nomenclature.class, s.getNomenclatureId());

                getSession().warn(getString("warn_quantity") + ": " +
                        Attributes.capitalize(n.getTextValue(Nomenclature.NAME)));
            }
        }

        try {
            sale.setPersonalRewardPoint(rewardService.getPersonalRewardPoint(sale, saleItems));

            if (saleService.isMkSaleItems(saleItems)) {
                sale.setMkManagerBonusRewardPoint(rewardService.getMkManagerBonusRewardPoint(sale, saleItems));
                sale.setCulinaryRewardPoint(rewardService.getCulinaryRewardPoint(sale, saleItems));
            }

            if (sale.getObjectId() == null){
                sale.setSaleStatus(SaleStatus.CREATED);
            }

            saleService.save(sale, saleItems);

            if (sale.isFeeWithdraw()) {
                rewardService.calculateSaleReward(sale, saleItems, RewardStatus.WITHDRAWN);
            }

            rewardService.calculateCulinaryReward(sale);

            getSession().success(getString("info_sold"));

            close(target, true);
        } catch (SaleException e) {
            error(getString("error_sell") + " " + e.getMessage());

            target.add(feedback);
        }
    }

    protected void onUpdate(AjaxRequestTarget target){

    }

    private BasePage getBasePage(){
        return (BasePage) getPage();
    }
}
