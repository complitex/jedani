package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.commons.lang3.StringUtils;
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
                        RuleCondition ruleCondition = item.getModelObject();

                        DropDownChoice comparator = new DropDownChoice<>("comparator",
                                new IModel<RuleConditionComparator>(){
                                    @Override
                                    public RuleConditionComparator getObject() {
                                        return RuleConditionComparator.getValue(item.getModelObject().getComparator());
                                    }

                                    @Override
                                    public void setObject(RuleConditionComparator object) {
                                        item.getModelObject().setComparator(object != null ? object.getId() : null);
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
                                });
                        comparator.setOutputMarkupId(true);
                        comparator.add(OnChangeAjaxBehavior.onChange(t -> {}));
                        item.add(comparator);

                        if (Objects.equals(ruleCondition.getValueType(), ValueType.BOOLEAN.getId())){
                            comparator.setVisible(false);

                            item.add(new BootstrapCheckbox("value", new IModel<Boolean>() {
                                @Override
                                public Boolean getObject() {
                                    return Objects.equals(ruleCondition.getNumber(RuleCondition.CONDITION), 1L);
                                }

                                @Override
                                public void setObject(Boolean object) {
                                    ruleCondition.setNumber(RuleCondition.CONDITION, object ? 1L : 0L);
                                }
                            }){
                                @Override
                                protected CheckBox newCheckBox(String id, IModel<Boolean> model) {
                                    CheckBox checkBox =  super.newCheckBox(id, model);
                                    checkBox.add(OnChangeAjaxBehavior.onChange(t -> {}));

                                    return checkBox;
                                }
                            });
                        }else if (Objects.equals(ruleCondition.getValueType(), ValueType.DECIMAL.getId())){
                            item.add(new TextField<>("value", new IModel<BigDecimal>() {
                                @Override
                                public BigDecimal getObject() {
                                    return ruleCondition.getDecimal(RuleCondition.CONDITION);
                                }

                                @Override
                                public void setObject(BigDecimal object) {
                                    ruleCondition.setDecimal(RuleCondition.CONDITION, object);
                                }
                            }, BigDecimal.class)
                                    .add(OnChangeAjaxBehavior.onChange(t -> {})));
                        }else if (Objects.equals(ruleCondition.getValueType(), ValueType.NUMBER.getId())){
                            item.add(new TextField<>("value", new IModel<Long>() {
                                @Override
                                public Long getObject() {
                                    return ruleCondition.getNumber(RuleCondition.CONDITION);
                                }

                                @Override
                                public void setObject(Long object) {
                                    ruleCondition.setNumber(RuleCondition.CONDITION, object);
                                }
                            }, Long.class)
                                    .add(OnChangeAjaxBehavior.onChange(t -> {})));
                        }else if (Objects.equals(ruleCondition.getValueType(), ValueType.DATE.getId())){
                            item.add(new DateTextField("value", new IModel<Date>() {
                                @Override
                                public Date getObject() {
                                    return ruleCondition.getDate(RuleCondition.CONDITION);
                                }

                                @Override
                                public void setObject(Date object) {
                                    ruleCondition.setDate(RuleCondition.CONDITION, object);
                                }
                            },new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true))
                                    .add(OnChangeAjaxBehavior.onChange(t -> {})));
                        }else{
                            item.add(new EmptyPanel("value"));
                        }
                    }
                });

                item.add(new ListView<RuleAction>("ruleActions",
                        new PropertyModel<>(item.getModel(), "actions")) {
                    @Override
                    protected void populateItem(ListItem<RuleAction> item) {
                        RuleAction ruleAction = item.getModelObject();

                        DropDownChoice comparator = new DropDownChoice<>("comparator",
                                new IModel<RuleConditionComparator>(){
                                    @Override
                                    public RuleConditionComparator getObject() {
                                        return RuleConditionComparator.getValue(item.getModelObject().getComparator());
                                    }

                                    @Override
                                    public void setObject(RuleConditionComparator object) {
                                        item.getModelObject().setComparator(object != null ? object.getId() : null);
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
                                });
                        comparator.setOutputMarkupId(true);
                        comparator.add(OnChangeAjaxBehavior.onChange(t -> {}));
                        item.add(comparator);

                        if (Objects.equals(ruleAction.getValueType(), ValueType.BOOLEAN.getId())){
                            comparator.setVisible(false);

                            item.add(new BootstrapCheckbox("value", new IModel<Boolean>() {
                                @Override
                                public Boolean getObject() {
                                    return Objects.equals(ruleAction.getNumber(RuleAction.ACTION), 1L);
                                }

                                @Override
                                public void setObject(Boolean object) {
                                    ruleAction.setNumber(RuleAction.ACTION, object ? 1L : 0L);
                                }
                            }){
                                @Override
                                protected CheckBox newCheckBox(String id, IModel<Boolean> model) {
                                    CheckBox checkBox =  super.newCheckBox(id, model);
                                    checkBox.add(OnChangeAjaxBehavior.onChange(t -> {}));

                                    return checkBox;
                                }
                            }
                                    );
                        }else if (Objects.equals(ruleAction.getValueType(), ValueType.DECIMAL.getId())){
                            item.add(new TextField<>("value", new IModel<BigDecimal>() {
                                @Override
                                public BigDecimal getObject() {
                                    return ruleAction.getDecimal(RuleAction.ACTION);
                                }

                                @Override
                                public void setObject(BigDecimal object) {
                                    ruleAction.setDecimal(RuleAction.ACTION, object);
                                }
                            }, BigDecimal.class)
                                    .add(OnChangeAjaxBehavior.onChange(t -> {})));
                        }else if (Objects.equals(ruleAction.getValueType(), ValueType.NUMBER.getId())){
                            item.add(new TextField<>("value", new IModel<Long>() {
                                @Override
                                public Long getObject() {
                                    return ruleAction.getNumber(RuleAction.ACTION);
                                }

                                @Override
                                public void setObject(Long object) {
                                    ruleAction.setNumber(RuleAction.ACTION, object);
                                }
                            }, Long.class));
                        }else if (Objects.equals(ruleAction.getValueType(), ValueType.DATE.getId())){
                            item.add(new DateTextField("value", new IModel<Date>() {
                                @Override
                                public Date getObject() {
                                    return ruleAction.getDate(RuleAction.ACTION);
                                }

                                @Override
                                public void setObject(Date object) {
                                    ruleAction.setDate(RuleAction.ACTION, object);
                                }
                            },new DateTextFieldConfig().withFormat("dd.MM.yyyy").withLanguage("ru").autoClose(true))
                                    .add(OnChangeAjaxBehavior.onChange(t -> {})));
                        }else{
                            item.add(new EmptyPanel("value"));
                        }
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

    public void edit(AjaxRequestTarget target){
        appendShowDialogJavaScript(target);
    }
}
