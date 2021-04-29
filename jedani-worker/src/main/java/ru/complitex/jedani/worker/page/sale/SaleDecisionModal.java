package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.model.ArrayListModel;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.model.AttributeModel;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;
import ru.complitex.jedani.worker.component.NomenclatureAutoCompleteList;
import ru.complitex.jedani.worker.component.RuleTable;
import ru.complitex.jedani.worker.component.TypeSelect;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.service.SaleDecisionService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 25.05.2019 18:33
 */
public class SaleDecisionModal extends Modal<SaleDecision> {
    @Inject
    private SaleDecisionService saleDecisionService;

    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    private final WebMarkupContainer container;

    private final FormGroupPanel nomenclatures;
    private final WebMarkupContainer nomenclatureContainer;

    public SaleDecisionModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        header(new ResourceModel("header"));

        setModel(Model.of(new SaleDecision()));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        NotificationPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        container.add(new FormGroupTextField<>("name", new TextAttributeModel(getModel(), SaleDecision.NAME))
                .setRequired(true)
                .onUpdate(t -> {}));

        container.add(new FormGroupDomainAutoComplete<>("country", Country.class, Country.NAME,
                NumberAttributeModel.of(getModel(), SaleDecision.COUNTRY)){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(nomenclatures, nomenclatureContainer);
            }
        }.setRequired(true));

        container.add(new FormGroupDateTextField("begin", DateAttributeModel.of(getModel(), SaleDecision.DATE_BEGIN))
                .setRequired(true)
                .onUpdate(t -> {}));

        container.add(new FormGroupDateTextField("end", DateAttributeModel.of(getModel(), SaleDecision.DATE_END))
                .setRequired(true)
                .onUpdate(t -> {}));

        nomenclatureContainer = new WebMarkupContainer("nomenclatureContainer"){
            @Override
            public boolean isVisible() {
                return getModelObject().getCountryId() != null;
            }
        };
        nomenclatureContainer.setOutputMarkupId(true);
        nomenclatureContainer.setOutputMarkupPlaceholderTag(true);
        container.add(nomenclatureContainer);

        container.add(nomenclatures = new FormGroupPanel("nomenclatures",
                new NomenclatureAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                        Nomenclature.ENTITY_NAME, new AttributeModel(getModel(), SaleDecision.NOMENCLATURES)){
                    @Override
                    protected Nomenclature getFilterObject(String input) {
                        Nomenclature nomenclature = super.getFilterObject(input);

                        List<Attribute> filterAttributes = new ArrayList<>();

                        filterAttributes.add(nomenclature.getOrCreateAttribute(Nomenclature.TYPE)
                                        .setNumber(getModelObject().getNomenclatureType()));
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

                    @Override
                    public void onUpdate(AjaxRequestTarget target) {
                        target.add(nomenclatureContainer.get("nomenclatureForm:nomenclatureTable"));
                    }
                }){
            @Override
            public boolean isVisible() {
                return getModelObject().getCountryId() != null;
            }
        });

        container.add(new FormGroupSelectPanel("nomenclatureType", new TypeSelect(FormGroupSelectPanel.COMPONENT_ID,
                NumberAttributeModel.of(getModel(), SaleDecision.NOMENCLATURE_TYPE),
                NomenclatureType.MYCOOK, NomenclatureType.BASE_ASSORTMENT)
                .add(new CssClassNameAppender("form-control"))
                .add(OnChangeAjaxBehavior.onChange(t -> t.add(nomenclatures, nomenclatureContainer))))) ;

        //Nomenclature filter table

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

                filterAttributes.add(nomenclature.getOrCreateAttribute(Nomenclature.TYPE)
                        .setNumber(getModelObject().getNomenclatureType()));
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
                cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Nomenclature>(LinkPanel.COMPONENT_ID, Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().addNomenclatureId(rowModel.getObject().getObjectId());

                        target.add(nomenclatures, nomenclatureContainer.get("nomenclatureForm:nomenclatureTable"));
                    }
                }.setIconType(GlyphIconType.plus)));
            }
        });

        Table<Nomenclature> table = new Table<>("nomenclatureTable", columns,
                filterDataProvider, 15, "saleDecisionNomenclatureTable");
        filterForm.add(table);

        container.add(new RuleTable("ruleTable", new PropertyModel<>(getModel(), "rules"),
                new ArrayListModel<>(SaleDecisionConditionType.values()),
                new ArrayListModel<>(SaleDecisionActionType.values())));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                SaleDecisionModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SaleDecisionModal.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }


    public void add(AjaxRequestTarget target){
        SaleDecision saleDecision = new SaleDecision();

        Rule rule = new Rule();

        rule.addCondition();
        rule.addAction();

        saleDecision.getRules().add(rule);


        setModelObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    public void edit(SaleDecision saleDecision,  AjaxRequestTarget target){
        saleDecision = Domains.copy(saleDecision);

        saleDecisionService.loadRules(saleDecision);

        setModelObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    private void cancel(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent<?>) c).clearInput());
    }

    private void save(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);

        saleDecisionService.save(getModelObject());

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent<?>) c).clearInput());

        getSession().success(getString("info_sale_decision_saved"));

        onUpdate(target);
    }

    protected void onUpdate(AjaxRequestTarget target){

    }
}
