package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class RuleAction extends Domain<RuleAction> {
    public static final String ENTITY_NAME = "rule_action";

    public static final long TYPE = 1;

    public RuleAction() {
        super(ENTITY_NAME);
    }
}
