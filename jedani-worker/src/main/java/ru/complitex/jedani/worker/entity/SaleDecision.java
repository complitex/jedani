package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.ArrayList;
import java.util.List;

public class SaleDecision extends Domain<SaleDecision> {
    public static final String ENTITY_NAME = "sale_decision";

    public final static long NAME = 1;
    public final static long DATE_BEGIN = 2;
    public final static long DATE_END = 3;
    public final static long COUNTRY = 4;
    public final static long NOMENCLATURES = 5;
    public final static long NOMENCLATURE_TYPE = 6;

    public static final String FILTER_DATE = "date";
    public static final String FILTER_NOMENCLATURE = "nomenclature";

    private List<Rule> rules = new ArrayList<>();

    public SaleDecision() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }

    public Long getCountryId(){
        return getNumber(COUNTRY);
    }

    public SaleDecision setCountryId(Long countryId){
        setNumber(COUNTRY, countryId);

        return this;
    }

    public List<Long> getNomenclatureIds(){
        return getNumberValues(NOMENCLATURES);
    }

    public Long getNomenclatureType(){
        return getNumber(NOMENCLATURE_TYPE);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public SaleDecision addRule(){
        if (rules.isEmpty()){
            rules.add(new Rule());
        } else {
            rules.add(new Rule(rules.get(0)));
        }

        return this;
    }

    public void addCondition(){
        rules.forEach(Rule::addCondition);
    }

    public void updateCondition(Long index){
        int rulesSize = rules.size();

        if (rulesSize > 1) {
            Rule rule = rules.get(0);

            rules.subList(1, rulesSize).forEach(r -> {
                r.updateCondition(rule.getCondition(index));
            });
        }
    }

    public void removeCondition(Long index){
        rules.forEach(r -> r.removeCondition(index));
    }

    public void addAction(){
        rules.forEach(Rule::addAction);
    }

    public void updateAction(Long index){
        int rulesSize = rules.size();

        if (rulesSize > 1) {
            Rule rule = rules.get(0);

            rules.subList(1, rulesSize).forEach(r -> {
                r.updateAction(rule.getAction(index));
            });
        }
    }


    public void removeAction(Long index){
        rules.forEach(r -> r.removeAction(index));
    }
}
