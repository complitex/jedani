package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.ArrayList;
import java.util.List;

public class SaleDecision extends Domain {
    public static final String ENTITY_NAME = "sale_decision";

    public static final long NAME = 1;
    public static final long DATE_BEGIN = 2;
    public static final long DATE_END = 3;
    public static final long COUNTRY = 4;
    public static final long NOMENCLATURES = 5;
    public static final long NOMENCLATURE_TYPE = 6;

    public static final String FILTER_DATE = "date";
    public static final String FILTER_NOMENCLATURE = "nomenclature";

    private List<Rule> rules = new ArrayList<>();

    public SaleDecision() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }

    public String getName(){
        return getText(NAME);
    }

    public void setName(String name){
        setText(NAME, name);
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

    public void addNomenclatureId(Long nomenclatureId){
        addNumberValue(NOMENCLATURES, nomenclatureId);
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

    public void clearObjectId(){
        setObjectId(null);

        if (getRules() != null){
            getRules().forEach(r -> {
                r.setObjectId(null);

                if (r.getActions() != null){
                    r.getActions().forEach(a -> a.setObjectId(null));
                }

                if (r.getConditions() != null){
                    r.getConditions().forEach(c -> c.setObjectId(null));
                }
            });
        }
    }
}
