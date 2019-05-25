package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RuleCondition extends Domain<RuleCondition> {
    public static final String ENTITY_NAME = "rule_condition";

    public static final long TYPE = 1;

    public RuleCondition() {
        super(ENTITY_NAME);
    }
}
