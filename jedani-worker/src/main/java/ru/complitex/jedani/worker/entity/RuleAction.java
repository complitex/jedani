package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RuleAction extends Domain<RuleAction> {
    public static final String ENTITY_NAME = "rule_action";

    public static final long INDEX = 1;
    public static final long TYPE = 2;

    public RuleAction() {
        super(ENTITY_NAME);
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
}
