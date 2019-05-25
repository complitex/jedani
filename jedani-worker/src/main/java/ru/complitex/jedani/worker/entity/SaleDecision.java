package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.ArrayList;
import java.util.List;

public class SaleDecision extends Domain<SaleDecision> {
    public static final String ENTITY_NAME = "sale_decision";

    public final static long NAME = 1;

    private Rule header;
    private List<Rule> rules = new ArrayList<>();

    public Rule getHeader() {
        return header;
    }

    public SaleDecision setHeader(Rule header) {
        this.header = header;

        return this;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
