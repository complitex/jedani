package ru.complitex.domain.entity;

import ru.complitex.common.entity.IdEnum;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 15:31
 */
public enum Status implements IdEnum<Status> {
    INACTIVE(0L), ACTIVE(1L),  ARCHIVE(2L), SYNC(3L), SYSTEM(4L);

    private final Long id;

    Status(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}

