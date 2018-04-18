package ru.complitex.domain.entity;

import ru.complitex.common.entity.IdEnum;

public enum ValueType implements IdEnum {
    VALUE(0),
    TEXT(1),
    BOOLEAN(2),
    DECIMAL(3),
    NUMBER(4),
    DATE(5),
    JSON(6),

    ENTITY(10);

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
            case VALUE:
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
