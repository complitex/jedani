package ru.complitex.domain.entity;

import ru.complitex.common.entity.IdEnum;

public enum ValueType implements IdEnum {
    TEXT_VALUE(0),
    NUMBER_VALUE(1),
    TEXT(2),
    BOOLEAN(3),
    DECIMAL(4),
    NUMBER(5),
    DATE(6),

    ENTITY_VALUE(10),
    ENTITY(11);

    private Integer id;

    ValueType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public boolean isSimple(){
        return id < 10;
    }

    public String getKey(){
        switch (this){
            case TEXT_VALUE:
                return "value";
            case TEXT:
            case DECIMAL:
                return "text";
            case BOOLEAN:
            case NUMBER:
                return "number";
            case DATE:
                return "date";
            case ENTITY:
                return "entity";
        }

        return "none";
    }
}
