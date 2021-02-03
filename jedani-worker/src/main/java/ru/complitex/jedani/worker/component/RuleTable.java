package ru.complitex.jedani.worker.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.wicket.form.AjaxFormInfoBehavior;
import ru.complitex.common.wicket.form.AjaxSelectLabel;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.entity.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RuleTable extends GenericPanel<List<Rule>> {
    public RuleTable(String id, IModel<List<Rule>> rulesModel, IModel<List<IConditionType>> conditionTypesModel,
                     IModel<List<IActionType>> actionTypesModel) {
        super(id, rulesModel);

        setOutputMarkupId(true);

        ListView<RuleCondition> conditions = new ListView<RuleCondition>("conditions",
                new PropertyModel<>(getModel(), "0.conditions")) {
            @Override
            protected void populateItem(ListItem<RuleCondition> item) {
                item.add(new AjaxSelectLabel<IConditionType>("type",
                        new IModel<IConditionType>() {
                            @Override
                            public IConditionType getObject() {
                                return conditionTypesModel.getObject().stream().filter(c -> c.getId()
                                        .equals(item.getModelObject().getType())).findAny().orElse(null);
                            }

                            @Override
                            public void setObject(IConditionType object) {
                                if (object != null) {
                                    item.getModelObject().setType(object.getId());
                                    item.getModelObject().setValueType(object.getValueType().getId());
                                }
                            }
                        },
                        conditionTypesModel.getObject(),
                        new IChoiceRenderer<IConditionType>() {
                            @Override
                            public Object getDisplayValue(IConditionType object) {
                                return Locales.getString(object.getClass(), object.getName());
                            }

                            @Override
                            public String getIdValue(IConditionType object, int index) {
                                return object.getId() + "";
                            }

                            @Override
                            public IConditionType getObject(String id, IModel<? extends List<? extends IConditionType>> choices) {   
                                return choices.getObject().stream().filter(c -> !id.isEmpty() && c.getId()
                                        .equals(Long.valueOf(id))).findFirst().orElse(null);
                            }
                        }){
                    @Override
                    protected void onSelect(AjaxRequestTarget target) {
                        updateCondition(item.getModelObject().getIndex());

                        target.add(RuleTable.this);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        updateCondition(item.getModelObject().getIndex());

                        target.add(RuleTable.this);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        removeCondition((long) item.getIndex());

                        target.add(RuleTable.this);
                    }
                });
            }
        };
        conditions.setReuseItems(false);
        add(conditions);

        add(new AjaxLink<RuleCondition>("addCondition") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                addCondition();

                target.add(RuleTable.this);
            }
        });

        ListView<RuleAction> actions = new ListView<RuleAction>("actions",
                new PropertyModel<>(getModel(), "0.actions")) {
            @Override
            protected void populateItem(ListItem<RuleAction> item) {
                item.add(new AjaxSelectLabel<IActionType>("type",
                        new IModel<IActionType>() {
                            @Override
                            public IActionType getObject() {
                                return actionTypesModel.getObject().stream().filter(a -> a.getId()
                                        .equals(item.getModelObject().getType())).findAny().orElse(null);
                            }

                            @Override
                            public void setObject(IActionType object) {
                                if (object != null) {
                                    item.getModelObject().setType(object.getId());
                                    item.getModelObject().setValueType(object.getValueType().getId());
                                }
                            }
                        },
                        actionTypesModel.getObject(),
                        new IChoiceRenderer<IActionType>() {
                            @Override
                            public Object getDisplayValue(IActionType object) {
                                return Locales.getString(object.getClass(), object.getName());
                            }

                            @Override
                            public String getIdValue(IActionType object, int index) {
                                return object.getId() + "";
                            }

                            @Override
                            public IActionType getObject(String id, IModel<? extends List<? extends IActionType>> choices) {
                                return choices.getObject().stream().filter(c -> !id.isEmpty() && c.getId()
                                        .equals(Long.valueOf(id))).findFirst().orElse(null);
                            }
                        }){
                    @Override
                    protected void onSelect(AjaxRequestTarget target) {
                        updateAction(item.getModelObject().getIndex());

                        target.add(RuleTable.this);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        updateAction(item.getModelObject().getIndex());

                        target.add(RuleTable.this);
                    }

                    @Override
                    protected void onRemove(AjaxRequestTarget target) {
                        removeAction((long) item.getIndex());

                        target.add(RuleTable.this);
                    }
                });
            }
        };
        actions.setReuseItems(false);
        add(actions);

        add(new AjaxLink<RuleCondition>("addAction") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                addAction();

                target.add(RuleTable.this);
            }
        });

        ListView<Rule> rules = new ListView<Rule>("rules", getModel()) {
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
                        getRules().remove(item.getModelObject());

                        target.add(RuleTable.this);
                    }

                    @Override
                    public boolean isVisible() {
                        return getRules().size() > 1;
                    }
                });
            }
        };
        add(rules);

        add(new AjaxLink<RuleCondition>("addRule") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                addRule();

                target.add(RuleTable.this);
            }
        });
    }

    private List<Rule> getRules(){
        return getModelObject();
    }

    private void updateCondition(Long index){
        List<Rule> rules = getModelObject();

        int rulesSize = rules.size();

        if (rulesSize > 1) {
            Rule rule = rules.get(0);

            rules.subList(1, rulesSize).forEach(r -> {
                r.updateCondition(rule.getCondition(index));
            });
        }
    }

    private void removeCondition(Long index){
        getModelObject().forEach(r -> r.removeCondition(index));
    }

    private void addCondition(){
        getModelObject().forEach(Rule::addCondition);
    }

    private void updateAction(Long index){
        List<Rule> rules = getModelObject();

        int rulesSize = rules.size();

        if (rulesSize > 1) {
            Rule rule = rules.get(0);

            rules.subList(1, rulesSize).forEach(r -> {
                r.updateAction(rule.getAction(index));
            });
        }
    }

    private void removeAction(Long index){
        getModelObject().forEach(r -> r.removeAction(index));
    }

    private void addAction(){
        getModelObject().forEach(Rule::addAction);
    }

    private void addRule(){
        List<Rule> rules = getModelObject();

        if (rules.isEmpty()){
            rules.add(new Rule());
        } else {
            rules.add(new Rule(rules.get(0)));
        }
    }

    @SuppressWarnings("WicketForgeJavaIdInspection")
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

    @SuppressWarnings("WicketForgeJavaIdInspection")
    private Component newValue(IModel<? extends Domain> domainModel, Long valueTypeAttributeId, Long valueAttributeId){
        if (Objects.equals(domainModel.getObject().getNumber(valueTypeAttributeId), ValueType.BOOLEAN.getId())){
            return new BootstrapCheckbox("value", new IModel<Boolean>() {
                @Override
                public Boolean getObject() {
                    return domainModel.getObject().isBoolean(valueAttributeId);
                }

                @Override
                public void setObject(Boolean object) {
                    domainModel.getObject().setBoolean(valueAttributeId, object);
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
}
