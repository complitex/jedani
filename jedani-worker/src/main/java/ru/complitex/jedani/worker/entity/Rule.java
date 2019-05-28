package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Rule extends Domain<Rule> {
    public final static String ENTITY_NAME = "rule";

    public final static long INDEX = 1;

    private List<RuleCondition> conditions = new ArrayList<>();
    private List<RuleAction> actions = new ArrayList<>();

    public Rule() {
        super(ENTITY_NAME);
    }

    public Rule(Rule rule){
        this();

        rule.getConditions().forEach(this::add);
        rule.getActions().forEach(this::add);
    }

    public List<RuleCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<RuleCondition> conditions) {
        this.conditions = conditions;
    }

    public List<RuleAction> getActions() {
        return actions;
    }

    public void setActions(List<RuleAction> actions) {
        this.actions = actions;
    }

    public Rule add(RuleCondition ruleCondition){
        conditions.add(new RuleCondition()
                .setIndex(ruleCondition.getIndex())
                .setType(ruleCondition.getType())
                .setValueType(ruleCondition.getValueType()));

        return this;
    }

    public void addCondition(){
        conditions.add(new RuleCondition().setIndex((long) conditions.size()));
    }

    public RuleCondition getCondition(Long index){
        return conditions.get(index.intValue());
    }

    public void updateCondition(RuleCondition ruleCondition){
        RuleCondition condition = conditions.get(ruleCondition.getIndex().intValue());

        condition.setType(ruleCondition.getType());
        condition.setValueType(ruleCondition.getValueType());
    }

    public void removeCondition(Long index){
        conditions.removeIf(c -> Objects.equals(c.getIndex(), index));

        updateConditionIndex();
    }

    public void updateConditionIndex(){
        for (int i = 0; i < conditions.size(); i++) {
            conditions.get(i).setIndex((long)i);
        }
    }

    public void sortConditionIndex(){
        conditions.sort(Comparator.comparingLong(RuleCondition::getIndex));
    }

    public void addAction(){
        actions.add(new RuleAction().setIndex((long) actions.size()));
    }

    public Rule add(RuleAction ruleAction){
        actions.add(new RuleAction()
                .setIndex(ruleAction.getIndex())
                .setType(ruleAction.getType())
                .setValueType(ruleAction.getValueType()));

        return this;
    }

    public RuleAction getAction(Long index){
        return actions.get(index.intValue());
    }

    public void updateAction(RuleAction ruleAction){
        RuleAction action = actions.get(ruleAction.getIndex().intValue());

        action.setType(ruleAction.getType());
    }

    public void removeAction(Long index){
        actions.removeIf(a -> Objects.equals(a.getIndex(), index));

        updateActionIndex();
    }

    public void updateActionIndex(){
        for (int i = 0; i < actions.size(); i++) {
            actions.get(i).setIndex((long)i);
        }
    }

    public void sortActionIndex(){
        actions.sort(Comparator.comparingLong(RuleAction::getIndex));
    }


}
