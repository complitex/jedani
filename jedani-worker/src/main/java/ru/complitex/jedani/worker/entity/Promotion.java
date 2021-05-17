package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 19:37
 */
public class Promotion extends Domain {
    public static final String ENTITY_NAME = "promotion";

    public static final long NAME = 1;
    public static final long COUNTRY = 2;
    public static final long DATE_BEGIN = 3;
    public static final long DATE_END = 4;
    public static final long FILE = 5;
    public static final long NOMENCLATURES = 6;
    public static final long RATE = 7;

    private List<Rule> rules = new ArrayList<>();

    public Promotion() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }

    public Long getCountryId(){
        return getNumber(COUNTRY);
    }

    public void addNomenclatureId(Long nomenclatureId){
        addNumberValue(NOMENCLATURES, nomenclatureId);
    }

    public List<Long> getNomenclatureIds(){
        return getNumberValues(NOMENCLATURES);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
