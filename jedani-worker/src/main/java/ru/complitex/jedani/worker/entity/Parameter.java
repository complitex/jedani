package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Ivanov Anatoliy
 */
public class Parameter extends Domain {
    public static final String ENTITY_NAME = "parameter";

    public static final long PARAMETER_ID = 1;
    public static final long TYPE = 2;
    public static final long NAME = 3;

    public Parameter() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getParameterId() {
        return getNumber(PARAMETER_ID);
    }

    public void setParameterId(Long parameterId) {
        setNumber(PARAMETER_ID, parameterId);
    }

    public Long getType() {
        return getNumber(TYPE);
    }

    public void setType(Long type) {
        setNumber(TYPE, type);
    }

    public String getName() {
        return getText(NAME);
    }

    public void setName(String name) {
        setText(NAME, name);
    }
}
