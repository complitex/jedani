package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.component.AjaxFormInfoBehavior;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.BooleanAttributeModel;
import ru.complitex.domain.model.DecimalAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.component.StorageAutoComplete;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.SaleException;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:23
 */
public class SaleModal extends Modal<Sale> {
    @Inject
    private SaleService saleService;

    @Inject
    private NameService nameService;

    @Inject
    private DomainService domainService;

    @Inject
    private StorageMapper storageMapper;

    private IModel<Sale> saleModel;
    private IModel<List<SaleItem>> mycookModel;
    private IModel<List<SaleItem>> baseAssortmentModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private DomainAutoCompleteFormGroup lastName, firstName, middleName;

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

        WebMarkupContainer fioContainer = new WebMarkupContainer("fioContainer");
        fioContainer.setOutputMarkupId(true);
        container.add(fioContainer);

        fioContainer.add(lastName = new DomainAutoCompleteFormGroup("lastName", LastName.ENTITY_NAME, LastName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(firstName = new DomainAutoCompleteFormGroup("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                new Model<>()).setInputRequired(true));
        fioContainer.add(middleName = new DomainAutoCompleteFormGroup("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                new Model<>()));

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

        FormGroupPanel sasRequest = new FormGroupPanel("sasRequest", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.SAS_REQUEST), new ResourceModel("sasRequestLabel"))){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getOrCreateAttribute(Sale.TYPE).getNumber(),
                        SaleType.BASE_ASSORTMENT);
            }
        };
        container.add(sasRequest);

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
                .add(OnChangeAjaxBehavior.onChange(target -> target.add(mycookContainer, baseAssortmentContainer, sasRequest)))));

        mycookContainer.add(new ListView<SaleItem>("mycooks", mycookModel) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                item.setOutputMarkupId(true);

                IModel<SaleItem> model = item.getModel();

                item.add(new Label("index", item.getIndex() + 1));

                TextField quantity = new TextField<>("quantity", NumberAttributeModel.of(model, SaleItem.QUANTITY), Long.class);
                quantity.setRequired(true).setOutputMarkupId(true).add(new AjaxFormInfoBehavior());
                item.add(quantity);

                SerializableConsumer<AjaxRequestTarget> onChange = newQuantityChange(model, quantity);

                item.add(new NomenclatureAutoComplete("nomenclature",
                        NumberAttributeModel.of(model, SaleItem.NOMENCLATURE), onChange){
                    @Override
                    protected Domain getFilterObject(String input) {
                        Domain domain = super.getFilterObject(input);

                        Attribute attribute = new Attribute(Nomenclature.TYPE);
                        attribute.setNumber(NomenclatureType.MYCOOK);
                        domain.getMap().put("attributes", Collections.singleton(attribute));

                        return domain;
                    }
                }.setRequired(true));


                item.add(new TextField<>("price", DecimalAttributeModel.of(model, SaleItem.PRICE), BigDecimal.class)
                        .setRequired(true)
                        .add(new AjaxFormInfoBehavior()));

                item.add(new BootstrapSelect<>("percentage",
                        NumberAttributeModel.of(model, SaleItem.INSTALLMENT_PERCENTAGE),
                        Arrays.asList(0L, 10L, 20L, 30L, 40L, 50L, 60L, 70L,80L, 90L, 100L))
                        .setRequired(true)
                        .add(new AjaxFormInfoBehavior()));

                item.add(new BootstrapSelect<>("months",
                        NumberAttributeModel.of(model, SaleItem.INSTALLMENT_MONTHS),
                        LongStream.range(0, 25).boxed().collect(Collectors.toList()))
                        .setRequired(true)
                        .add(new AjaxFormInfoBehavior()));

                item.add(newRealStorageAutoComplete("storage", model, onChange));

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
                mycookModel.getObject().add(newMycook());

                target.add(mycookContainer);
            }

            @Override
            public boolean isVisible() {
                return container.isEnabled();
            }
        });

        baseAssortmentContainer.add(new ListView<SaleItem>("baseAssortments", baseAssortmentModel) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                IModel<SaleItem> model = item.getModel();

                item.add(new Label("index", item.getIndex() + 1));

                TextField quantity = new TextField<>("quantity", NumberAttributeModel.of(model, SaleItem.QUANTITY), Long.class);
                quantity.setRequired(true).setOutputMarkupId(true).add(new AjaxFormInfoBehavior());
                item.add(quantity);

                SerializableConsumer<AjaxRequestTarget> onChange = newQuantityChange(model, quantity);

                item.add(new NomenclatureAutoComplete("nomenclature",
                        NumberAttributeModel.of(model, SaleItem.NOMENCLATURE), onChange){
                    @Override
                    protected Domain getFilterObject(String input) {
                        Domain domain = super.getFilterObject(input);

                        Attribute attribute = new Attribute(Nomenclature.TYPE);
                        attribute.setNumber(NomenclatureType.MYCOOK);
                        domain.getMap().put("notAttributes", Collections.singleton(attribute));

                        return domain;
                    }
                }.setRequired(true));

                item.add(new TextField<>("price", DecimalAttributeModel.of(model, SaleItem.PRICE), BigDecimal.class)
                        .setRequired(true)
                        .add(new AjaxFormInfoBehavior()));

                item.add(newRealStorageAutoComplete("storage", model, onChange));

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
                baseAssortmentModel.getObject().add(newBaseAssortment());

                target.add(baseAssortmentContainer);
            }

            @Override
            public boolean isVisible() {
                return container.isEnabled();
            }
        });

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

    private SerializableConsumer<AjaxRequestTarget> newQuantityChange(IModel<SaleItem> model, TextField quantity) {
        return t -> {
            Long storageId = model.getObject().getNumber(SaleItem.STORAGE);
            Long nomenclatureId = model.getObject().getNumber(SaleItem.NOMENCLATURE);

            if (storageId != null && nomenclatureId != null) {
                List<Product> products = domainService.getDomains(Product.class,
                        FilterWrapper.of((Product) new Product()
                                .setParentId(storageId)
                                .setNumber(Product.NOMENCLATURE, nomenclatureId)));

                t.appendJavaScript("$('#" + quantity.getMarkupId() + "').attr('placeholder', '" +
                        (!products.isEmpty() ? products.get(0).getAvailableQuantity() : "0") + "')");
            }
        };
    }

    private SaleItem newMycook() {
        SaleItem saleItem = new SaleItem();

        saleItem.setNumber(SaleItem.INSTALLMENT_PERCENTAGE, 100L);
        saleItem.setNumber(SaleItem.INSTALLMENT_MONTHS, 0L);
        saleItem.setNumber(SaleItem.STORAGE, defaultStorageId);

        return saleItem;
    }

    private SaleItem newBaseAssortment() {
        SaleItem saleItem = new SaleItem();

        saleItem.setNumber(SaleItem.INSTALLMENT_PERCENTAGE, 100L);
        saleItem.setNumber(SaleItem.INSTALLMENT_MONTHS, 0L);
        saleItem.setNumber(SaleItem.STORAGE, defaultStorageId);

        return saleItem;
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

    void sale(Long sellerWorkerId, AjaxRequestTarget target){
        List<Long> regions = domainService.getNumberValues(Worker.ENTITY_NAME, sellerWorkerId, Worker.REGIONS);

        if (!regions.isEmpty()){
            List<Storage> storages = storageMapper.getStorages(FilterWrapper.of(new Storage())
                    .add("regions", regions.stream().map(Object::toString).collect(Collectors.joining(","))));

            if (!storages.isEmpty()){
                defaultStorageId = storages.get(0).getObjectId();
            }
        }

        Sale sale = new Sale();
        sale.setNumber(Sale.SELLER_WORKER, sellerWorkerId);
        sale.setNumber(Sale.TYPE, SaleType.MYCOOK);
        sale.setDate(Sale.DATE, new Date());

        saleModel.setObject(sale);

        lastName.setObjectId(null);
        firstName.setObjectId(null);
        middleName.setObjectId(null);

        mycookModel.setObject(new ArrayList<>());
        baseAssortmentModel.setObject(new ArrayList<>());

        mycookModel.getObject().add(newMycook());
        baseAssortmentModel.getObject().add(newBaseAssortment());

        container.setEnabled(true);

        open(target);
    }

    void edit(){

    }

    void view(SaleItem saleItem, AjaxRequestTarget target){
        Sale sale = domainService.getDomain(Sale.class, saleItem.getParentId());

        saleModel.setObject(sale);

        lastName.setObjectId(sale.getBuyerLastName());
        firstName.setObjectId(sale.getBuyerFirstName());;
        middleName.setObjectId(sale.getBuyerMiddleName());

        List<SaleItem> saleItems = domainService.getDomains(SaleItem.class, FilterWrapper.of((SaleItem) new SaleItem()
                .setParentId(saleItem.getParentId())));

        if (sale.getNumber(Sale.TYPE).equals(SaleType.MYCOOK)){
            mycookModel.setObject(saleItems);
        }else if (sale.getNumber(Sale.TYPE).equals(SaleType.BASE_ASSORTMENT)){
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

        Long type = sale.getNumber(Sale.TYPE);

        List<SaleItem> saleItems = null;

        if (Objects.equals(type, SaleType.MYCOOK)){
            saleItems = mycookModel.getObject();
        }else if (Objects.equals(type, SaleType.BASE_ASSORTMENT)){
            saleItems = baseAssortmentModel.getObject();
        }

        if (saleItems == null || saleItems.isEmpty()){
            return;
        }

        //todo validate quantity

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

    private FormComponent<Long> newRealStorageAutoComplete(String markupId, IModel<SaleItem> model, SerializableConsumer<AjaxRequestTarget> onChange) {
        return new StorageAutoComplete(markupId, NumberAttributeModel.of(model, SaleItem.STORAGE), onChange){
            @Override
            protected Domain getFilterObject(String input) {
                Domain domain = super.getFilterObject(input);

                Attribute attribute = new Attribute(Storage.TYPE);
                attribute.setNumber(StorageType.REAL);

                domain.getMap().put("attributes", Collections.singleton(attribute));

                return domain;
            }
        }.setRequired(true);
    }
}
