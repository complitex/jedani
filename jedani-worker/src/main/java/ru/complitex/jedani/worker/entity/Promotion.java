package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 19:37
 */
public class Promotion extends Domain<Promotion> {
    public static final String ENTITY_NAME = "promotion";

    public static final long NAME = 1;
    public static final long COUNTRY = 2;
    public static final long BEGIN = 3;
    public static final long END = 4;
    public static final long FILE = 5;
    public static final long NOMENCLATURES = 6;
    public static final long EUR = 7;

    public Promotion() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }
}
