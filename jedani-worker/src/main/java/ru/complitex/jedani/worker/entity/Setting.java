package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 10.01.2019 18:40
 */
public class Setting extends Domain<Setting> {
    public static final String ENTITY_NAME = "setting";

    public static final long VALUE = 1;

    public static final long PHOTO_ID = 2;

    public Setting() {
        super(ENTITY_NAME);
    }
}
