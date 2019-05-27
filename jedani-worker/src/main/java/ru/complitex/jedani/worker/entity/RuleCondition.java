package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RuleCondition extends Domain<RuleCondition> {
    public static final String ENTITY_NAME = "rule_condition";

    public static final long INDEX = 1;
    public static final long TYPE = 2;

    public RuleCondition() {
        super(ENTITY_NAME);
    }

    public Long getIndex(){
        return getNumber(INDEX);
    }

    public RuleCondition setIndex(Long index){
        setNumber(INDEX, index);

        return this;
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public RuleCondition setType(Long type){
        setNumber(TYPE, type);

        return this;
    }
}
