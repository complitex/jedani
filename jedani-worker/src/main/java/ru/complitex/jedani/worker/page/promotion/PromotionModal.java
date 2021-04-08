package ru.complitex.jedani.worker.page.promotion;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.model.ArrayListModel;
import ru.complitex.common.wicket.component.MoneyTextField;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.util.Wickets;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.model.*;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.component.NomenclatureAutoCompleteList;
import ru.complitex.jedani.worker.component.RuleTable;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.service.PromotionService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 20:01
 */
public class PromotionModal extends Modal<Promotion> {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private PromotionService promotionService;

    private final WebMarkupContainer container;

    private final FormGroupPanel nomenclatures;
    private final WebMarkupContainer nomenclatureContainer;

    private final Component remove;

    public PromotionModal(String markupId) {
        super(markupId, new Model<>(new Promotion()));

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
        size(Size.Large);

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        add(container);

        NotificationPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        container.add(new FormGroupDomainAutoComplete<>("country", Country.class, Country.NAME,
                new NumberAttributeModel(getModel(), Promotion.COUNTRY)){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(nomenclatures, nomenclatureContainer);
            }
        }.setRequired(true));

        container.add(new FormGroupBorder("name", new ResourceModel("name")){
            @Override
            protected boolean isRequired() {
                return true;
            }
        }.add(new TextArea<>("name", new TextValueModel(getModel(), Promotion.NAME,
                Locales.getSystemLocaleId())).setRequired(true)));


        container.add(new FormGroupDateTextField("begin", new DateAttributeModel(getModel(), Promotion.DATE_BEGIN)));
        container.add(new FormGroupDateTextField("end", new DateAttributeModel(getModel(), Promotion.DATE_END)));

        container.add(new FormGroupBorder("rate", new ResourceModel("rate"))
                .add(new MoneyTextField<>("rate", new TextAttributeModel(getModel(), Promotion.RATE,
                        StringType.DEFAULT))));

        container.add(nomenclatures = new FormGroupPanel("nomenclatures", new NomenclatureAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                Nomenclature.ENTITY_NAME, new AttributeModel(getModel(), Promotion.NOMENCLATURES)){
            @Override
            protected Nomenclature getFilterObject(String input) {
                Nomenclature nomenclature = super.getFilterObject(input);

                List<Attribute> filterAttributes = new ArrayList<>();

                filterAttributes.add(nomenclature.getOrCreateAttribute(Nomenclature.COUNTRIES)
                        .setNumber(getModelObject().getCountryId()));

                nomenclature.put(Domain.FILTER_ATTRIBUTES, filterAttributes);

                List<Long> nomenclatureIds = getModelObject().getNomenclatureIds().stream()
                        .filter(Objects::nonNull).collect(Collectors.toList());

                if (!nomenclatureIds.isEmpty()) {
                    nomenclature.put(Domain.FILTER_EXCLUDE_OBJECT_IDS, nomenclatureIds);
                }

                return nomenclature;
            }
        }));

        FilterWrapper<Nomenclature> filterWrapper = FilterWrapper.of(new Nomenclature());

        Provider<Nomenclature> filterDataProvider = new Provider<>(filterWrapper) {
            @Override
            public List<Nomenclature> getList() {
                return domainService.getDomains(Nomenclature.class, getFilterState());
            }

            @Override
            public Long getCount() {
                Nomenclature nomenclature = getFilterState().getObject();

                List<Attribute> filterAttributes = new ArrayList<>();

                filterAttributes.add(nomenclature.getOrCreateAttribute(Nomenclature.COUNTRIES)
                        .setNumber(getModelObject().getCountryId()));

                nomenclature.put(Domain.FILTER_ATTRIBUTES, filterAttributes);

                List<Long> nomenclatureIds = getModelObject().getNomenclatureIds().stream()
                        .filter(Objects::nonNull).collect(Collectors.toList());

                if (!nomenclatureIds.isEmpty()) {
                    nomenclature.put(Domain.FILTER_EXCLUDE_OBJECT_IDS, nomenclatureIds);
                }

                return domainService.getDomainsCount(getFilterState());
            }
        };

        nomenclatureContainer = new WebMarkupContainer("nomenclatureContainer"){
            @Override
            public boolean isVisible() {
                return getModelObject().getCountryId() != null;
            }
        };
        nomenclatureContainer.setOutputMarkupId(true);
        nomenclatureContainer.setOutputMarkupPlaceholderTag(true);
        container.add(nomenclatureContainer);

        FilterForm<FilterWrapper<Nomenclature>> filterForm = new FilterForm<>("nomenclatureForm", filterDataProvider);
        filterForm.setOutputMarkupId(true);
        nomenclatureContainer.add(filterForm);

        List<IColumn<Nomenclature, Sort>> columns = new ArrayList<>();

        Entity entity = entityService.getEntity(Nomenclature.class);

        columns.add(new DomainIdColumn<>());
        columns.add(new DomainColumn<>(entity.getEntityAttribute(Nomenclature.CODE)));
        columns.add(new DomainColumn<>(entity.getEntityAttribute(Nomenclature.NAME)));
        columns.add(new DomainActionColumn<>() {
            @Override
            public void populateItem(Item<ICellPopulator<Nomenclature>> cellItem, String componentId, IModel<Nomenclature> rowModel) {
                cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Nomenclature>(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        PromotionModal.this.getModelObject().addNomenclatureId(rowModel.getObject().getObjectId());

                        target.add(nomenclatures, nomenclatureContainer.get("nomenclatureForm:nomenclatureTable"));
                    }
                }.setIconType(GlyphIconType.plus)));
            }
        });

        Table<Nomenclature> table = new Table<>("nomenclatureTable", columns,
                filterDataProvider, 15, "saleDecisionNomenclatureTable");
        filterForm.add(table);

        container.add(new RuleTable("ruleTable", new PropertyModel<>(getModel(), "rules"),
                new ArrayListModel<>(PromotionConditionType.values()),
                new ArrayListModel<>(PromotionActionType.values())));

        addButton(new IndicatingAjaxButton(Modal.BUTTON_MARKUP_ID, new ResourceModel("save")) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                PromotionModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                container.visitChildren(((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(Wickets.getAjaxParent(object));
                    }
                }));
            }
        }.add(AttributeModifier.append("class", "btn btn-primary")));

        addButton(remove = new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PromotionModal.this.remove(target);
            }

            @Override
            public boolean isVisible() {
                return PromotionModal.this.getModelObject().getObjectId() != null;
            }
        }.setLabel(new ResourceModel("remove")).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true));


        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PromotionModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    void create(AjaxRequestTarget target){
        Promotion promotion = new Promotion();

        Rule rule = new Rule();

        rule.addCondition();
        rule.addAction();

        promotion.getRules().add(rule);

        setModelObject(promotion);

        open(target);
    }

    void edit(Promotion promotion, AjaxRequestTarget target){
        promotionService.loadRules(promotion);

        setModelObject(promotion);

        open(target);
    }

    private void open(AjaxRequestTarget target){
        container.setVisible(true);
        target.add(container, remove);
        appendShowDialogJavaScript(target);
    }

    private void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent<?>)c).clearInput());
    }

    private void save(AjaxRequestTarget target){
        Promotion promotion = getModelObject();

        promotionService.save(promotion);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent<?>) c).clearInput());

        success(getString("info_promotion_saved"));

        close(target);
        onUpdate(target);
    }

    private void remove(AjaxRequestTarget target) {
        Promotion promotion = PromotionModal.this.getModelObject();

        promotion.setStatus(Status.ARCHIVE);

        domainService.save(promotion);

        success(getString("info_promotion_removed"));

        close(target);
        onUpdate(target);
    }

    protected void onUpdate(AjaxRequestTarget target){
    }
}
