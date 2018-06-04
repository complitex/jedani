package ru.complitex.domain.entity;

import ru.complitex.common.entity.IdEnum;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:31
 */
public enum Status implements IdEnum {
    INACTIVE(0), ACTIVE(1),  ARCHIVE(2), SYNC(3), SYSTEM(4);

    private Integer id;

    Status(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}

