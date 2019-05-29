package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RuleAction extends Domain<RuleAction> {
    public static final String ENTITY_NAME = "rule_action";

    public static final long INDEX = 1;
    public static final long TYPE = 2;
    public static final long VALUE_TYPE = 3;
    public static final long COMPARATOR = 4;
    public static final long ACTION = 5;

    public RuleAction() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getIndex(){
        return getNumber(INDEX);
    }

    public RuleAction setIndex(Long index){
        setNumber(INDEX, index);

        return this;
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public RuleAction setType(Long type){
        setNumber(TYPE, type);

        return this;
    }

    public Long getValueType(){
        return getNumber(VALUE_TYPE);
    }

    public RuleAction setValueType(Long valueType){
        setNumber(VALUE_TYPE, valueType);

        return this;
    }

    public Long getComparator(){
        return getNumber(COMPARATOR);
    }

    public RuleAction setComparator(Long comparator){
        setNumber(COMPARATOR, comparator);

        return this;
    }
}
