package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.FilterDataProvider;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.common.wicket.form.*;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.model.AttributeModel;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.NomenclatureAutoCompleteList;
import ru.complitex.jedani.worker.component.TypeSelect;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.service.SaleDecisionService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SaleDecisionModal extends Modal<SaleDecision> {
    @Inject
    private SaleDecisionService saleDecisionService;

    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    private WebMarkupContainer container;

    private FormGroupPanel nomenclatures;
    private WebMarkupContainer nomenclatureContainer;

    public SaleDecisionModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
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

        container.add(new FormGroupDomainAutoComplete("country", Country.ENTITY_NAME, Country.NAME,
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

        container.add(new FormGroupSelectPanel("nomenclatureType", new TypeSelect(FormGroupSelectPanel.COMPONENT_ID,
                NumberAttributeModel.of(getModel(), SaleDecision.NOMENCLATURE_TYPE),
                NomenclatureType.MYCOOK, NomenclatureType.BASE_ASSORTMENT)
                .add(new CssClassNameAppender("form-control"))
                .add(OnChangeAjaxBehavior.onChange(t -> {
                    t.add(nomenclatures, nomenclatureContainer);
                })))) ;


        container.add(nomenclatures = new FormGroupPanel("nomenclatures",
                new NomenclatureAutoCompleteList(FormGroupPanel.COMPONENT_ID,
                        Nomenclature.ENTITY_NAME, new AttributeModel(getModel(), SaleDecision.NOMENCLATURES)){
                    @Override
                    protected Nomenclature getFilterObject(String input) {
                        Nomenclature nomenclature = super.getFilterObject(input);

                        nomenclature.setType(getModelObject().getNomenclatureType());
                        nomenclature.setNumber(Nomenclature.COUNTRIES, getModelObject().getCountryId());

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

        //Nomenclature filter table

        FilterWrapper<Nomenclature> filterWrapper = FilterWrapper.of(new Nomenclature());

        FilterDataProvider<Nomenclature> filterDataProvider = new FilterDataProvider<Nomenclature>(filterWrapper) {
            @Override
            public List<Nomenclature> getList() {
                return domainService.getDomains(Nomenclature.class, getFilterState());
            }

            @Override
            public Long getCount() {
                Nomenclature nomenclature = getFilterState().getObject();

                nomenclature.setType(getModelObject().getNomenclatureType());
                nomenclature.setNumber(Nomenclature.COUNTRIES, getModelObject().getCountryId());

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

        FilterDataForm<FilterWrapper<Nomenclature>> filterDataForm = new FilterDataForm<>("nomenclatureForm", filterDataProvider);
        filterDataForm.setOutputMarkupId(true);
        nomenclatureContainer.add(filterDataForm);

        List<IColumn<Nomenclature, SortProperty>> columns = new ArrayList<>();

        Entity entity = entityService.getEntity(Nomenclature.class);

        columns.add(new DomainIdColumn<>());
        columns.add(new DomainColumn<>(entity.getEntityAttribute(Nomenclature.CODE)));
        columns.add(new DomainColumn<>(entity.getEntityAttribute(Nomenclature.NAME)));
        columns.add(new DomainActionColumn<Nomenclature>(){
            @Override
            public void populateItem(Item<ICellPopulator<Nomenclature>> cellItem, String componentId, IModel<Nomenclature> rowModel) {
                cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Nomenclature>(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().addNomenclatureId(rowModel.getObject().getObjectId());

                        target.add(nomenclatures, nomenclatureContainer.get("nomenclatureForm:nomenclatureTable"));
                    }
                }.setIconType(GlyphIconType.plus)));
            }
        });

        FilterDataTable<Nomenclature> filterDataTable = new FilterDataTable<>("nomenclatureTable", columns,
                filterDataProvider, filterDataForm, 5, "saleDecisionNomenclatureTable");
        filterDataForm.add(filterDataTable);

        //Conditions

        ListView<RuleCondition> conditions = new ListView<RuleCondition>("conditions",
                new PropertyModel<>(getModel(), "rules.0.conditions")) {
            @Override
            protected void populateItem(ListItem<RuleCondition> item) {
                item.add(new AjaxSelectLabel<RuleConditionType>("type",
                        new IModel<RuleConditionType>() {
                            @Override
                            public RuleConditionType getObject() {
                                return RuleConditionType.getValue(item.getModelObject().getType());
                            }

                            @Override
                            public void setObject(RuleConditionType object) {
                                if (object != null) {
                                    item.getModelObject().setType(object.getId());
                                    item.getModelObject().setValueType(object.getValueType().getId());
                                }
                            }
                        },
                        Arrays.asList(RuleConditionType.values()),
                        new IChoiceRenderer<RuleConditionType>() {
                            @Override
                            public Object getDisplayValue(RuleConditionType object) {
                                return getString(object.name());
                            }

                            @Override
                            public String getIdValue(RuleConditionType object, int index) {
                                return object.getId() + "";
                            }

                            @Override
                            public RuleConditionType getObject(String id, IModel<? extends List<? extends RuleConditionType>> choices) {
                                return StringUtils.isNumeric(id) ? RuleConditionType.getValue(Long.valueOf(id)) : null;
                            }
                        }){
                    @Override
                    protected void onSelect(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().updateCondition(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().updateCondition(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().removeCondition((long) item.getIndex());

                        target.add(container);
                    }
                });
            }
        };
        conditions.setReuseItems(false);
        container.add(conditions);

        container.add(new AjaxLink<RuleCondition>("addCondition") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SaleDecisionModal.this.getModelObject().addCondition();

                target.add(container);
            }
        });

        ListView<RuleAction> actions = new ListView<RuleAction>("actions",
                new PropertyModel<>(getModel(), "rules.0.actions")) {
            @Override
            protected void populateItem(ListItem<RuleAction> item) {
                item.add(new AjaxSelectLabel<RuleActionType>("type",
                        new IModel<RuleActionType>() {
                            @Override
                            public RuleActionType getObject() {
                                return RuleActionType.getValue(item.getModelObject().getType());
                            }

                            @Override
                            public void setObject(RuleActionType object) {
                                if (object != null) {
                                    item.getModelObject().setType(object.getId());
                                    item.getModelObject().setValueType(object.getValueType().getId());
                                }
                            }
                        },
                        Arrays.asList(RuleActionType.values()),
                        new IChoiceRenderer<RuleActionType>() {
                            @Override
                            public Object getDisplayValue(RuleActionType object) {
                                return getString(object.name());
                            }

                            @Override
                            public String getIdValue(RuleActionType object, int index) {
                                return object.getId() + "";
                            }

                            @Override
                            public RuleActionType getObject(String id, IModel<? extends List<? extends RuleActionType>> choices) {
                                return StringUtils.isNumeric(id) ? RuleActionType.getValue(Long.valueOf(id)) : null;
                            }
                        }){
                    @Override
                    protected void onSelect(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().updateAction(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().updateAction(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().removeAction((long) item.getIndex());

                        target.add(container);
                    }
                });
            }
        };
        actions.setReuseItems(false);
        container.add(actions);

        container.add(new AjaxLink<RuleCondition>("addAction") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SaleDecisionModal.this.getModelObject().addAction();

                target.add(container);
            }
        });

        ListView<Rule> rules = new ListView<Rule>("rules",
                new PropertyModel<>(getModel(), "rules")) {
            @Override
            protected void populateItem(ListItem<Rule> item) {
                item.add(new Label("id", item.getIndex() + 1));

                item.add(new ListView<RuleCondition>("ruleConditions",
                        new PropertyModel<>(item.getModel(), "conditions")) {
                    @Override
                    protected void populateItem(ListItem<RuleCondition> item) {
                        item.add(newComparator(item.getModel(), RuleCondition.VALUE_TYPE, RuleCondition.COMPARATOR));
                        item.add(newValue(item.getModel(), RuleCondition.VALUE_TYPE, RuleCondition.CONDITION));
                    }
                });

                item.add(new ListView<RuleAction>("ruleActions",
                        new PropertyModel<>(item.getModel(), "actions")) {
                    @Override
                    protected void populateItem(ListItem<RuleAction> item) {
                        item.add(newComparator(item.getModel(), RuleAction.VALUE_TYPE, RuleAction.COMPARATOR));
                        item.add(newValue(item.getModel(), RuleAction.VALUE_TYPE, RuleAction.ACTION));
                    }
                });

                item.add(new AjaxLink<Rule>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        SaleDecisionModal.this.getModelObject().getRules().remove(item.getModelObject());

                        target.add(container);
                    }

                    @Override
                    public boolean isVisible() {
                        return SaleDecisionModal.this.getModelObject().getRules().size() > 1;
                    }
                });
            }
        };
        container.add(rules);

        container.add(new AjaxLink<RuleCondition>("addRule") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SaleDecisionModal.this.getModelObject().addRule();

                target.add(container);
            }
        });

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



    private Component newComparator(IModel<? extends Domain> domainModel, Long valueTypeAttributeId, Long comparatorAttributeId){
        DropDownChoice comparator = new DropDownChoice<RuleConditionComparator>("comparator",
                new IModel<RuleConditionComparator>(){
                    @Override
                    public RuleConditionComparator getObject() {
                        return RuleConditionComparator.getValue(domainModel.getObject().getNumber(comparatorAttributeId));
                    }

                    @Override
                    public void setObject(RuleConditionComparator object) {
                        domainModel.getObject().setNumber(comparatorAttributeId, object != null ? object.getId() : null);
                    }
                },
                Arrays.asList(RuleConditionComparator.values()),
                new IChoiceRenderer<RuleConditionComparator>() {
                    @Override
                    public Object getDisplayValue(RuleConditionComparator object) {
                        if (object != null){
                            return object.getText();
                        }

                        return null;
                    }

                    @Override
                    public String getIdValue(RuleConditionComparator object, int index) {
                        return object.getId() + "";
                    }

                    @Override
                    public RuleConditionComparator getObject(String id, IModel<? extends List<? extends RuleConditionComparator>> choices) {
                        return StringUtils.isNumeric(id) ? RuleConditionComparator.getValue(Long.valueOf(id)) : null;
                    }
                }){
            @Override
            public boolean isVisible() {
                return !Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.BOOLEAN.getId());
            }
        };
        comparator.setOutputMarkupId(true);
        comparator.add(OnChangeAjaxBehavior.onChange(t -> {}));

        return comparator;
    }

    private Component newValue(IModel<? extends Domain> domainModel, Long valueTypeAttributeId, Long valueAttributeId){
        if (Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.BOOLEAN.getId())){
            return new BootstrapCheckbox("value", new IModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return Objects.equals(domainModel.getObject().getNumber(valueAttributeId), 1L);
                }

                @Override
                public void setObject(Boolean object) {
                    domainModel.getObject().setNumber(valueAttributeId, object ? 1L : 0L);
                }
            }){
                @Override
                protected CheckBox newCheckBox(String id, IModel<Boolean> model) {
                    CheckBox checkBox =  super.newCheckBox(id, model);
                    checkBox.add(OnChangeAjaxBehavior.onChange(t -> {}));

                    return checkBox;
                }
            };
        }else if (Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.DECIMAL.getId())){
            return new TextField<BigDecimal>("value", new IModel<BigDecimal>() {
                @Override
                public BigDecimal getObject() {
                    return domainModel.getObject().getDecimal(valueAttributeId);
                }

                @Override
                public void setObject(BigDecimal object) {
                    domainModel.getObject().setDecimal(valueAttributeId, object);
                }
            }, BigDecimal.class){
                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);

                    tag.put("style", "min-width: 42px");
                }
            }
                    .add(new AjaxFormInfoBehavior());
        }else if (Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.NUMBER.getId())){
            return new TextField<Long>("value", new IModel<Long>() {
                @Override
                public Long getObject() {
                    return domainModel.getObject().getNumber(valueAttributeId);
                }

                @Override
                public void setObject(Long object) {
                    domainModel.getObject().setNumber(valueAttributeId, object);
                }
            }, Long.class){
                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);

                    tag.put("style", "min-width: 33px");
                }
            }
                    .add(new AjaxFormInfoBehavior());
        }else if (Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.DATE.getId())){
            return new DateTextField("value", new IModel<Date>() {
                @Override
                public Date getObject() {
                    return domainModel.getObject().getDate(valueAttributeId);
                }

                @Override
                public void setObject(Date object) {
                    domainModel.getObject().setDate(valueAttributeId, object);
                }
            },new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true)){
                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);

                    tag.put("style", "min-width: 85px");
                }
            }
                    .add(new AjaxFormInfoBehavior());
        }else{
            return new EmptyPanel("value");
        }
    }

    public void add(AjaxRequestTarget target){
        SaleDecision saleDecision = new SaleDecision();
        saleDecision.addRule();
        saleDecision.addCondition();
        saleDecision.addAction();

        setModelObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    public void edit(SaleDecision saleDecision,  AjaxRequestTarget target){
        saleDecisionService.loadRules(saleDecision);

        setModelObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    private void cancel(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());
    }

    private void save(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);

        saleDecisionService.save(getModelObject());

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());

        getSession().success(getString("info_sale_decision_saved"));

        onUpdate(target);
    }

    protected void onUpdate(AjaxRequestTarget target){

    }
}
