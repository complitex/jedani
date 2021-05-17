package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

public class Rank extends Domain {
    public static final String ENTITY_NAME = "rank";

    public static final long NAME = 1;

    public Rank() {
        super(ENTITY_NAME);
    }

    public String getName(){
        return getTextValue(NAME);
    }
}
