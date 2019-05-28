package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.jedani.worker.entity.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SaleDecisionModal extends Modal<SaleDecision> {
    public SaleDecisionModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        header(new ResourceModel("header"));

        IModel<SaleDecision> saleDecisionModel = Model.of(new SaleDecision().addRule());

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);

        //todo name, date

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
                                item.getModelObject().setType(object.getId());
                                item.getModelObject().setValueType(object.getValueType().getId());
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
                    protected void onApply(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().updateCondition(item.getModelObject().getIndex());

                        target.add(form);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().removeCondition((long) item.getIndex());

                        target.add(form);
                    }
                });
            }
        };
        conditions.setReuseItems(false);
        form.add(conditions);

        form.add(new AjaxLink<RuleCondition>("addCondition") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().addCondition();

                target.add(form);
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
                                item.getModelObject().setType(object.getId());
                                item.getModelObject().setValueType(object.getValueType().getId());
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
                    protected void onApply(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().updateAction(item.getModelObject().getIndex());

                        target.add(form);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        saleDecisionModel.getObject().removeAction((long) item.getIndex());

                        target.add(form);
                    }
                });
            }
        };
        actions.setReuseItems(false);
        form.add(actions);

        form.add(new AjaxLink<RuleCondition>("addAction") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().addAction();

                target.add(form);
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

                //todo remove
            }
        };
        form.add(rules);

        form.add(new AjaxLink<RuleCondition>("addRule") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                saleDecisionModel.getObject().addRule();

                target.add(form);
            }
        });

        //todo modal button
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
            return new TextField<>("value", new IModel<BigDecimal>() {
                @Override
                public BigDecimal getObject() {
                    return domainModel.getObject().getDecimal(valueAttributeId);
                }

                @Override
                public void setObject(BigDecimal object) {
                    domainModel.getObject().setDecimal(valueAttributeId, object);
                }
            }, BigDecimal.class)
                    .add(OnChangeAjaxBehavior.onChange(t -> {}));
        }else if (Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.NUMBER.getId())){
            return new TextField<>("value", new IModel<Long>() {
                @Override
                public Long getObject() {
                    return domainModel.getObject().getNumber(valueAttributeId);
                }

                @Override
                public void setObject(Long object) {
                    domainModel.getObject().setNumber(valueAttributeId, object);
                }
            }, Long.class)
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
            },new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true))
                    .add(OnChangeAjaxBehavior.onChange(t -> {}));
        }else{
            return new EmptyPanel("value");
        }
    }



    public void edit(AjaxRequestTarget target){
        appendShowDialogJavaScript(target);
    }
}
