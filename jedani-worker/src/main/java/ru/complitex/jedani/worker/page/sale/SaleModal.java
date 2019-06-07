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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.*;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.BooleanAttributeModel;
import ru.complitex.domain.model.DecimalAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
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
    private IModel<List<SaleItem>> mycookModel;
    private IModel<List<SaleItem>> baseAssortmentModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private DomainAutoCompleteFormGroup lastName, firstName, middleName;

    private FormGroupTextField total;
    private FormGroupTextField payment;

    private Long defaultStorageId;

    private Component saveButton;

    public SaleModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        saleModel = Model.of(new Sale());
        mycookModel = Model.ofList(new ArrayList<>());
        baseAssortmentModel = Model.ofList(new ArrayList<>());

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
                new NumberAttributeModel(saleModel, Sale.SELLER_WORKER)).setRequired(true)){
            @Override
            public boolean isVisible() {
                return getBasePage().isAdmin() || getBasePage().isStructureAdmin();
            }
        });

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
                .add(OnChangeAjaxBehavior.onChange(target -> {
                    saleModel.getObject().setTotal(null);
                    saleModel.getObject().setInitialPayment(null);

                    target.add(container);
                }))));

        container.add(new FormGroupTextField<>("contract", new TextAttributeModel(saleModel, Sale.CONTRACT))
                .addValidator(new PatternValidator(MetaPattern.DIGITS){
                    @Override
                    protected IValidationError decorate(IValidationError error, IValidatable<String> validatable) {
                        ((ValidationError)error).setVariable("pattern", getString("error_digits_only"));

                        return error;
                    }
                }));

        container.add(new FormGroupPanel("sasRequest", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.SAS_REQUEST), new ResourceModel("sasRequestLabel"))));

        WebMarkupContainer fioContainer = new WebMarkupContainer("fioContainer");
        fioContainer.setOutputMarkupId(true);
        container.add(fioContainer);

        fioContainer.add(lastName = new DomainAutoCompleteFormGroup("lastName", LastName.ENTITY_NAME, LastName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(firstName = new DomainAutoCompleteFormGroup("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(middleName = new DomainAutoCompleteFormGroup("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                new Model<>()));

        container.add(new FormGroupPanel("storage", new StorageAutoComplete(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(saleModel, Sale.STORAGE), t ->
                container.visitChildren(NomenclatureAutoComplete.class, (c, v) -> {
                    if (c.isVisibleInHierarchy()){
                        ((NomenclatureAutoComplete)c).getOnChange().accept(t);
                    }
                })).setRequired(true)));

        container.add(new DomainAutoCompleteFormGroup("promotion", Promotion.ENTITY_NAME, Promotion.NAME,
                NumberAttributeModel.of(saleModel, Sale.PROMOTION)));

        FormGroupBorder months = new FormGroupBorder("months");
        container.add(months);

        months.add(new Spinner<>("input", NumberAttributeModel.of(saleModel, Sale.INSTALLMENT_MONTHS),
                new SpinnerConfig().withVerticalbuttons(true).withMin(0).withMax(24)).setType(Long.class));

        WebMarkupContainer mycookContainer = new WebMarkupContainer("mycookContainer"){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getOrCreateAttribute(Sale.TYPE).getNumber(),
                        SaleType.MYCOOK);
            }
        };
        mycookContainer.setOutputMarkupId(true);
        mycookContainer.setOutputMarkupPlaceholderTag(true);
        container.add(mycookContainer);

        mycookContainer.add(new ListView<SaleItem>("mycooks", mycookModel) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                item.setOutputMarkupId(true);

                IModel<SaleItem> model = item.getModel();

                item.add(new Label("index", item.getIndex() + 1));

                TextField quantity = new TextField<>("quantity", NumberAttributeModel.of(model, SaleItem.QUANTITY), Long.class);
                quantity.setRequired(true).setOutputMarkupId(true).add(new AjaxFormInfoBehavior());
                item.add(quantity);

                TextField price = new TextField<>("price", new LoadableDetachableModel<BigDecimal>() {
                    @Override
                    protected BigDecimal load() {
                        return priceService.getPrice(saleModel.getObject().getStorageId(),
                                model.getObject().getNomenclatureId(), Dates.currentDate(), model.getObject().getPrice());
                    }
                }, BigDecimal.class);
                price.setOutputMarkupId(true);
                price.setEnabled(false);
                item.add(price);

                SerializableConsumer<AjaxRequestTarget> onChange = newSaleItemOnChange(model, quantity, price);

                item.add(new NomenclatureAutoComplete("nomenclature", NumberAttributeModel.of(model,
                        SaleItem.NOMENCLATURE), onChange){
                    @Override
                    protected Domain getFilterObject(String input) {
                        Domain domain = super.getFilterObject(input);

                        Attribute attribute = new Attribute(Nomenclature.TYPE);
                        attribute.setNumber(NomenclatureType.MYCOOK);
                        domain.put("attributes", Collections.singleton(attribute));

                        return domain;
                    }
                }.setRequired(true));

                item.add(new TextField<>("total", DecimalAttributeModel.of(model, SaleItem.TOTAL), BigDecimal.class)
                        .setRequired(true)
                        .add(new AjaxFormInfoBehavior(){
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                super.onUpdate(target);

                                if (!isError()){
                                    updateTotal(target);
                                }
                            }
                        }));

                item.add(new BootstrapAjaxLink<SaleItem>("remove", Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        mycookModel.getObject().remove(item.getIndex());

                        target.add(mycookContainer);
                    }

                    @Override
                    public boolean isVisible() {
                        return container.isEnabled();
                    }
                }.setIconType(GlyphIconType.remove));
            }
        }.setReuseItems(true));

        mycookContainer.add(new AjaxLink<SaleItem>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                mycookModel.getObject().add(new SaleItem());

                target.add(mycookContainer);
            }

            @Override
            public boolean isVisible() {
                return container.isEnabled();
            }
        });

        WebMarkupContainer baseAssortmentContainer = new WebMarkupContainer("baseAssortmentContainer"){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getOrCreateAttribute(Sale.TYPE).getNumber(),
                        SaleType.BASE_ASSORTMENT);
            }
        };
        baseAssortmentContainer.setOutputMarkupId(true);
        baseAssortmentContainer.setOutputMarkupPlaceholderTag(true);
        container.add(baseAssortmentContainer);

        baseAssortmentContainer.add(new ListView<SaleItem>("baseAssortments", baseAssortmentModel) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                IModel<SaleItem> model = item.getModel();

                item.add(new Label("index", item.getIndex() + 1));

                TextField quantity = new TextField<>("quantity", NumberAttributeModel.of(model, SaleItem.QUANTITY), Long.class);
                quantity.setRequired(true).setOutputMarkupId(true).add(new AjaxFormInfoBehavior());
                item.add(quantity);

                SerializableConsumer<AjaxRequestTarget> onChange = newSaleItemOnChange(model, quantity);

                item.add(new NomenclatureAutoComplete("nomenclature",
                        NumberAttributeModel.of(model, SaleItem.NOMENCLATURE), onChange){
                    @Override
                    protected Domain getFilterObject(String input) {
                        Domain domain = super.getFilterObject(input);

                        Attribute attribute = new Attribute(Nomenclature.TYPE);
                        attribute.setNumber(NomenclatureType.MYCOOK);
                        domain.put("notAttributes", Collections.singleton(attribute));

                        return domain;
                    }
                }.setRequired(true));

                item.add(new TextField<>("total", DecimalAttributeModel.of(model, SaleItem.TOTAL), BigDecimal.class)
                        .setRequired(true)
                        .add(new AjaxFormInfoBehavior(){
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                super.onUpdate(target);

                                if (!isError()){
                                    updateTotal(target);
                                }
                            }
                        }));

                item.add(new BootstrapAjaxLink<SaleItem>("remove", Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        baseAssortmentModel.getObject().remove(item.getIndex());

                        target.add(baseAssortmentContainer);
                    }

                    @Override
                    public boolean isVisible() {
                        return container.isEnabled();
                    }
                }.setIconType(GlyphIconType.remove));
            }
        }.setReuseItems(true));

        baseAssortmentContainer.add(new AjaxLink<SaleItem>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                baseAssortmentModel.getObject().add(new SaleItem());

                target.add(baseAssortmentContainer);
            }

            @Override
            public boolean isVisible() {
                return container.isEnabled();
            }
        });

        container.add(total = new FormGroupTextField<>("sum", new DecimalAttributeModel(saleModel, Sale.TOTAL), BigDecimal.class));
        container.add(payment = new FormGroupTextField<>("payment", DecimalAttributeModel.of(saleModel, Sale.INITIAL_PAYMENT), BigDecimal.class));

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
                SaleModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    private SerializableConsumer<AjaxRequestTarget> newSaleItemOnChange(IModel<SaleItem> model, TextField quantity, Component... update) {
        return t -> {
            Long storageId = saleModel.getObject().getStorageId();
            Long nomenclatureId = model.getObject().getNomenclatureId();

            if (storageId != null && nomenclatureId != null) {
                List<Product> products = domainService.getDomains(Product.class,
                        FilterWrapper.of((Product) new Product()
                                .setParentId(storageId)
                                .setNumber(Product.NOMENCLATURE, nomenclatureId)));

                t.appendJavaScript("$('#" + quantity.getMarkupId() + "').attr('placeholder', '" +
                        (!products.isEmpty() ? products.get(0).getAvailableQuantity() : "0") + "')");
            }

            if (update != null){
                t.add(update);
            }
        };
    }

    private void updateTotal(AjaxRequestTarget target){
        try {
            Sale sale = saleModel.getObject();

            List<SaleItem> saleItems = saleModel.getObject().getType() == SaleType.MYCOOK
                    ? mycookModel.getObject()
                    : baseAssortmentModel.getObject();

            sale.setTotal(saleItems.stream().map(SaleItem::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));

            sale.setInitialPayment(sale.getTotal().divide(BigDecimal.TEN, 2, BigDecimal.ROUND_HALF_EVEN));

            target.add(total, payment);
        } catch (Exception e) {
            log.error("update total error", e);
        }
    }

    private void open(AjaxRequestTarget target){
        container.setVisible(true);
        target.add(container, saveButton);

        show(target);
    }

    private void close(AjaxRequestTarget target){
        super.close(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());

        onUpdate(target);
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

        mycookModel.setObject(new ArrayList<>());
        baseAssortmentModel.setObject(new ArrayList<>());

        mycookModel.getObject().add(new SaleItem());
        baseAssortmentModel.getObject().add(new SaleItem());

        container.setEnabled(true);

        open(target);
    }

    void view(SaleItem saleItem, AjaxRequestTarget target){
        Sale sale = domainService.getDomain(Sale.class, saleItem.getParentId());

        saleModel.setObject(sale);

        lastName.setObjectId(sale.getBuyerLastName());
        firstName.setObjectId(sale.getBuyerFirstName());
        middleName.setObjectId(sale.getBuyerMiddleName());

        List<SaleItem> saleItems = domainService.getDomains(SaleItem.class, FilterWrapper.of((SaleItem) new SaleItem()
                .setParentId(saleItem.getParentId())));

        if (sale.getType().equals(SaleType.MYCOOK)){
            mycookModel.setObject(saleItems);
        }else if (sale.getType().equals(SaleType.BASE_ASSORTMENT)){
            baseAssortmentModel.setObject(saleItems);
        }

        container.setEnabled(false);

        open(target);
    }

    private void save(AjaxRequestTarget target){
        Sale sale = saleModel.getObject();

        sale.setBuyerLastName(nameService.getOrCreateLastName(lastName.getInput(), lastName.getObjectId()));
        sale.setBuyerFirstName(nameService.getOrCreateFirstName(firstName.getInput(), firstName.getObjectId()));
        sale.setBuyerMiddleName(nameService.getOrCreateMiddleName(middleName.getInput(), middleName.getObjectId()));

        Long type = sale.getType();

        List<SaleItem> saleItems = null;

        if (Objects.equals(type, SaleType.MYCOOK)){
            saleItems = mycookModel.getObject();
        }else if (Objects.equals(type, SaleType.BASE_ASSORTMENT)){
            saleItems = baseAssortmentModel.getObject();
        }

        if (saleItems == null || saleItems.isEmpty()){
            return;
        }

        saleItems.forEach(s -> {
            if (!saleService.validateQuantity(sale, s)){
                Nomenclature n = domainService.getDomain(Nomenclature.class, s.getNomenclatureId());

                getSession().warn(Attributes.capitalize(n.getTextValue(Nomenclature.NAME)) + ": " +
                        getString("warn_quantity"));
            }
        });

        try {
            saleService.sale(sale, saleItems);

            getSession().success(getString("info_sold"));

            close(target);
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
