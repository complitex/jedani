package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.AjaxSelectLabel;
import ru.complitex.common.wicket.form.DateTextFieldFormGroup;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.service.SaleDecisionService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SaleDecisionModal extends Modal<SaleDecision> {
    @Inject
    private SaleDecisionService saleDecisionService;

    private IModel<SaleDecision> saleDecisionModel;

    private WebMarkupContainer container;

    public SaleDecisionModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        header(new ResourceModel("header"));

        saleDecisionModel = Model.of(new SaleDecision());

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        NotificationPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);


        container.add(new FormGroupTextField<>("name", new TextAttributeModel(saleDecisionModel, SaleDecision.NAME))
                .setRequired(true)
                .onUpdate(t -> {}));
        container.add(new DateTextFieldFormGroup("begin", DateAttributeModel.of(saleDecisionModel, SaleDecision.DATE_BEGIN))
                .setRequired(true)
                .onUpdate(t -> {}));
        container.add(new DateTextFieldFormGroup("end", DateAttributeModel.of(saleDecisionModel, SaleDecision.DATE_END))
                .setRequired(true)
                .onUpdate(t -> {}));

        ListView<RuleCondition> conditions = new ListView<RuleCondition>("conditions",
                new PropertyModel<>(saleDecisionModel, "rules.0.conditions")) {
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
                        saleDecisionModel.getObject().updateCondition(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().updateCondition(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().removeCondition((long) item.getIndex());

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
                saleDecisionModel.getObject().addCondition();

                target.add(container);
            }
        });

        ListView<RuleAction> actions = new ListView<RuleAction>("actions",
                new PropertyModel<>(saleDecisionModel, "rules.0.actions")) {
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
                        saleDecisionModel.getObject().updateAction(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().updateAction(item.getModelObject().getIndex());

                        target.add(container);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().removeAction((long) item.getIndex());

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
                saleDecisionModel.getObject().addAction();

                target.add(container);
            }
        });

        ListView<Rule> rules = new ListView<Rule>("rules",
                new PropertyModel<>(saleDecisionModel, "rules")) {
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
                        saleDecisionModel.getObject().getRules().remove(item.getModelObject());

                        target.add(container);
                    }

                    @Override
                    public boolean isVisible() {
                        return saleDecisionModel.getObject().getRules().size() > 1;
                    }
                });
            }
        };
        container.add(rules);

        container.add(new AjaxLink<RuleCondition>("addRule") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().addRule();

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
                    .add(OnChangeAjaxBehavior.onChange(t -> {}));
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
                    .add(OnChangeAjaxBehavior.onChange(t -> {}));
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
                    .add(OnChangeAjaxBehavior.onChange(t -> {}));
        }else{
            return new EmptyPanel("value");
        }
    }

    public void add(AjaxRequestTarget target){
        SaleDecision saleDecision = new SaleDecision();
        saleDecision.addRule();
        saleDecision.addCondition();
        saleDecision.addAction();

        saleDecisionModel.setObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    public void edit(SaleDecision saleDecision,  AjaxRequestTarget target){
        saleDecisionService.loadRules(saleDecision);

        saleDecisionModel.setObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    private void cancel(AjaxRequestTarget target) {
        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());

        appendCloseDialogJavaScript(target);
    }

    private void save(AjaxRequestTarget target) {
        saleDecisionService.save(saleDecisionModel.getObject());

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());

        getSession().success(getString("info_sale_decision_saved"));

        appendCloseDialogJavaScript(target);

        onUpdate(target);
    }

    protected void onUpdate(AjaxRequestTarget target){

    }
}
