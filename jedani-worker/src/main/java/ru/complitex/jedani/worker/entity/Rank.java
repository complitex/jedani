package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import javax.print.attribute.standard.MediaSize;

public class Rank extends Domain<Rank> {
    public static final String ENTITY_NAME = "rank";

    public static final long NAME = 1;

    public Rank() {
        super(ENTITY_NAME);
    }

    public String getName(){
        return getText(NAME);
    }
}
