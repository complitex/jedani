package ru.complitex.domain.entity;

import ru.complitex.common.entity.IdEnum;

import java.util.Arrays;
import java.util.Objects;

public enum ValueType implements IdEnum {
    TEXT_LIST(0L, "value"),
    NUMBER_LIST(1L, "number_value"),
    TEXT(2L, "text"),
    BOOLEAN(3L, "number"),
    DECIMAL(4L, "text"),
    NUMBER(5L, "number"),
    DATE(6L, "date"),

    ENTITY_LIST(10L, "entity_value"),
    ENTITY(11L, "entity");

    private Long id;
    private String key;

    ValueType(Long id, String key) {
        this.id = id;
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public boolean isSimple(){
        return id < 10;
    }

    public String getKey(){
        return key;
    }

    public static ValueType getValue(Long id){
        if (id == null){
            return null;
        }

        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(values()).filter(t -> Objects.equals(id, t.getId())).findAny().get();
    }
}
