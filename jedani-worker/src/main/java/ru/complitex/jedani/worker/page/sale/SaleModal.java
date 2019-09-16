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
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
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
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.PriceService;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

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

    private IModel<Sale> saleModel;
    private IModel<List<SaleItem>> saleItemsModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private FormGroupDomainAutoComplete lastName, firstName, middleName;

    private ListView<SaleItem> saleItems;

    private Long defaultStorageId;

    private Component saveButton;

    public SaleModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
        size(Size.Large);

        saleModel = Model.of(new Sale());
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
                new NumberAttributeModel(saleModel, Sale.SELLER_WORKER)){
            @Override
            public boolean isVisible() {
                return getBasePage().isAdmin() || getBasePage().isStructureAdmin();
            }
        }.setRequired(true)));

        container.add(new FormGroupDateTextField("saleDate", DateAttributeModel.of(saleModel, Sale.DATE)){
            @Override
            public boolean isVisible() {
                return getBasePage().isAdmin() || getBasePage().isStructureAdmin();
            }
        }.setRequired(true).onUpdate(this::updatePrices));

        container.add(new FormGroupPanel("sasRequest", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.SAS_REQUEST), new ResourceModel("sasRequestLabel"))));

        container.add(new FormGroupSelectPanel("saleType", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(saleModel, Sale.TYPE),
                Arrays.asList(SaleType.MYCOOK, SaleType.BASE_ASSORTMENT),
                new IChoiceRenderer<Long>() {
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

                    target.add(container);
                }))));

        container.add(new FormGroupTextField<>("contract", new TextAttributeModel(saleModel, Sale.CONTRACT))
                .setRequired(true)
                .addValidator(new PatternValidator(MetaPattern.DIGITS){
                    @Override
                    protected IValidationError decorate(IValidationError error, IValidatable<String> validatable) {
                        ((ValidationError)error).setVariable("pattern", getString("error_digits_only"));

                        return error;
                    }
                }));

        WebMarkupContainer fioContainer = new WebMarkupContainer("fioContainer");
        fioContainer.setOutputMarkupId(true);
        container.add(fioContainer);

        fioContainer.add(lastName = new FormGroupDomainAutoComplete("lastName", LastName.ENTITY_NAME, LastName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(firstName = new FormGroupDomainAutoComplete("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(middleName = new FormGroupDomainAutoComplete("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                new Model<>()));

        container.add(new FormGroupPanel("storage", new StorageAutoComplete(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(saleModel, Sale.STORAGE), t -> {
            updatePrices(t);

            container.visitChildren(NomenclatureAutoComplete.class, (c, v) -> {
                if (c.isVisibleInHierarchy()){
                    ((NomenclatureAutoComplete)c).getOnChange().accept(t);
                }
            });
        }).setRequired(true)));

        container.add(new FormGroupDomainAutoComplete("promotion", Promotion.ENTITY_NAME, Promotion.NAME,
                NumberAttributeModel.of(saleModel, Sale.PROMOTION))); //todo promotion storage filter

        FormGroupBorder months = new FormGroupBorder("months");
        container.add(months);

        months.add(new Spinner<>("input", NumberAttributeModel.of(saleModel, Sale.INSTALLMENT_MONTHS),
                new SpinnerConfig().withVerticalbuttons(true).withMin(0).withMax(24)).setType(Long.class)
                .add(AjaxFormComponentUpdatingBehavior.onUpdate("change", this::updatePrices)));

        saleItems = new ListView<SaleItem>("saleItems", saleItemsModel) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                item.setOutputMarkupId(true);

                IModel<SaleItem> model = item.getModel();

                item.add(new Label("index", item.getIndex() + 1));

                TextField basePrice = new TextField<>("basePrice", DecimalAttributeModel.of(model, SaleItem.BASE_PRICE), BigDecimal.class);
                basePrice.setOutputMarkupId(true);
                basePrice.setEnabled(false);
                item.add(basePrice);

                TextField price = new TextField<BigDecimal>("price", DecimalAttributeModel.of(model, SaleItem.PRICE), BigDecimal.class){
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);

                        SaleDecision saleDecision = domainService.getDomain(SaleDecision.class, model.getObject().getSaleDecisionId());

                        String title = (saleDecision != null ? saleDecision.getName() : getString("basePrice")) +
                                (model.getObject().getPointPrice() != null ? ", " + getString("pointRate") + ": " +
                                model.getObject().getPointPrice().toPlainString() : "");

                        tag.put("title",  title);
                    }
                };
                price.setOutputMarkupId(true);
                price.setEnabled(false);
                item.add(price);

                TextField quantity = new TextField<>("quantity", NumberAttributeModel.of(model, SaleItem.QUANTITY), Long.class);
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
                        SaleItem.NOMENCLATURE), target -> {
                    updateQuantity(model, target, quantity);
                    updatePrices(target);
                }){
                    @Override
                    protected Domain getFilterObject(String input) {
                        Domain domain = super.getFilterObject(input);

                        Attribute attribute = new Attribute(Nomenclature.TYPE);
                        if (saleModel.getObject().getType().equals(SaleType.MYCOOK)) {
                            attribute.setNumber(NomenclatureType.MYCOOK);
                        }else{
                            attribute.setNumber(NomenclatureType.BASE_ASSORTMENT);
                        }
                        domain.put(Domain.FILTER_ATTRIBUTES, Collections.singleton(attribute));

                        return domain;
                    }
                }.setRequired(true));

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
        container.add(new FormGroupTextField<>("payment", DecimalAttributeModel.of(saleModel, Sale.INITIAL_PAYMENT), BigDecimal.class));

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
                return container.isEnabled();
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

    private void updateQuantity(IModel<SaleItem> model, AjaxRequestTarget target, TextField quantity){
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

        saleItemsModel.getObject().forEach(si -> {
            si.setBasePrice(priceService.getBasePrice(sale.getStorageId(), si.getNomenclatureId(), sale.getDate()));
        });
    }

    private void updatePrices(){
        BigDecimal basePricesTotal = saleItemsModel.getObject().stream()
                .map(si -> si.getQuantity() != null && si.getBasePrice() != null
                        ? si.getBasePrice().multiply(new BigDecimal(si.getQuantity()))
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Sale sale = saleModel.getObject();

        saleItemsModel.getObject().forEach(si -> {
            SaleDecision saleDecision = priceService.getSaleDecision(sale.getStorageId(), si.getNomenclatureId(),
                    sale.getDate(), basePricesTotal, sale.getInstallmentMonths());

            si.setSaleDecisionId(saleDecision != null ? saleDecision.getObjectId() : null);

            si.setPrice(priceService.getPrice(saleDecision, sale.getDate(), si.getBasePrice(), basePricesTotal,
                    sale.getInstallmentMonths()));

            BigDecimal pointPrice = priceService.getPointPrice(sale.getStorageId(), si.getNomenclatureId(), sale.getDate());

            si.setPointPrice(priceService.getPointPrice(saleDecision, sale.getDate(), pointPrice, basePricesTotal,
                    sale.getInstallmentMonths()));
        });
    }

    private void updateTotal(){
        Sale sale = saleModel.getObject();
        List<SaleItem> saleItems = saleItemsModel.getObject();

        sale.setTotal(BigDecimal.ZERO);
        sale.setTotalLocal(BigDecimal.ZERO);
        sale.setInitialPayment(BigDecimal.ZERO);

        if (saleItems.stream().noneMatch(si -> si.getQuantity() == null || si.getPrice() == null)) {
            sale.setTotal(saleItems.stream().map(si -> si.getPrice().multiply(new BigDecimal(si.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (saleItems.stream().noneMatch(si -> si.getPrice() == null || si.getQuantity() == null || si.getPointPrice() == null)){
            sale.setTotalLocal(saleItems.stream().map(si -> si.getPrice().multiply(new BigDecimal(si.getQuantity()))
                    .multiply(si.getPointPrice())
                    .setScale(2, RoundingMode.HALF_EVEN))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        if (sale.getTotal() != null && sale.getInstallmentMonths() >= 0) {
            sale.setInitialPayment(sale.getTotal().divide(BigDecimal.valueOf(1 + sale.getInstallmentMonths()), 2,
                    BigDecimal.ROUND_HALF_EVEN));
        }
    }

    private void updatePrices(AjaxRequestTarget target){
        try {
            updateBasePrices();
            updatePrices();
            updateTotal();

            saleItems.stream().forEach(c -> {
                target.add(c.get("price"), c.get("basePrice"));
            });

            container.get("total").modelChanged();
            container.get("totalLocal").modelChanged();
            container.get("payment").modelChanged();

            target.add(container.get("total"), container.get("totalLocal"), container.get("payment"));
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

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());

        if (update) {
            onUpdate(target);
        }
    }

    void sale(AjaxRequestTarget target){
        Long sellerWorkerId = getBasePage().getCurrentWorker().getObjectId();

        List<Long> regions = domainService.getNumberValues(Worker.ENTITY_NAME, sellerWorkerId, Worker.REGIONS);

        if (!regions.isEmpty()){
            List<Storage> storages = storageMapper.getStorages(FilterWrapper.of(new Storage())
                    .put("regions", regions.stream().map(Object::toString).collect(Collectors.joining(","))));

            if (!storages.isEmpty()){
                defaultStorageId = storages.get(0).getObjectId();
            }
        }

        Sale sale = new Sale();
        sale.setSellerWorkerId(sellerWorkerId);
        sale.setType(SaleType.MYCOOK);
        sale.setDate(new Date());
        sale.setInstallmentMonths(0L);
        sale.setStorageId(defaultStorageId);

        saleModel.setObject(sale);

        lastName.setObjectId(null);
        firstName.setObjectId(null);
        middleName.setObjectId(null);

        saleItemsModel.setObject(new ArrayList<>());
        saleItemsModel.getObject().add(new SaleItem());

        container.setEnabled(true);

        open(target);
    }

    void view(SaleItem saleItem, AjaxRequestTarget target, boolean edit){
        Sale sale = domainService.getDomain(Sale.class, saleItem.getParentId());

        saleModel.setObject(sale);

        lastName.setObjectId(sale.getBuyerLastName());
        firstName.setObjectId(sale.getBuyerFirstName());
        middleName.setObjectId(sale.getBuyerMiddleName());

        List<SaleItem> saleItems = domainService.getDomains(SaleItem.class, FilterWrapper.of((SaleItem) new SaleItem()
                .setParentId(saleItem.getParentId())));

        saleItemsModel.setObject(saleItems);

        container.setEnabled(edit);

        open(target);
    }

    private void save(AjaxRequestTarget target){
        Sale sale = saleModel.getObject();

        sale.setBuyerLastName(nameService.getOrCreateLastName(lastName.getInput(), lastName.getObjectId()));
        sale.setBuyerFirstName(nameService.getOrCreateFirstName(firstName.getInput(), firstName.getObjectId()));
        sale.setBuyerMiddleName(nameService.getOrCreateMiddleName(middleName.getInput(), middleName.getObjectId()));

        List<SaleItem> saleItems = saleItemsModel.getObject();

        if (saleItems == null || saleItems.isEmpty()){
            return;
        }

        saleItems.forEach(s -> {
            if (!saleService.validateQuantity(sale, s)){
                Nomenclature n = domainService.getDomain(Nomenclature.class, s.getNomenclatureId());

                getSession().warn(getString("warn_quantity") + ": " +
                        Attributes.capitalize(n.getTextValue(Nomenclature.NAME)));
            }
        });

        try {
            saleService.sale(sale, saleItems);

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
