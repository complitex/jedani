package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.10.2018 15:27
 */
public class Nomenclature extends Domain {
    public static final String ENTITY_NAME = "nomenclature";

    public static final long NAME = 1;
    public static final long CODE = 2;
    public static final long COUNTRIES = 3;
    public static final long TYPE = 4;

    public Nomenclature() {
        super(ENTITY_NAME);

        setUseNumberValue(true);
    }

    public String getCode(){
        return getText(CODE);
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public void setType(Long type){
        setNumber(TYPE, type);
    }

    public List<Long> getCountryIds(){
        return getNumberValues(COUNTRIES);
    }
}
