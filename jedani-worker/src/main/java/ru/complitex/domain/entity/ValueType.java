package ru.complitex.domain.entity;

import ru.complitex.common.entity.IdEnum;

public enum ValueType implements IdEnum {
    VALUE(0),
    STRING(1),
    BOOLEAN(2),
    DECIMAL(3),
    INTEGER(4),
    DATE(5),

    ENTITY(10);

    private Integer id;

    ValueType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public boolean isSimple(){
        return id <= 5;
    }

    public String getKey(){
        switch (this){
            case VALUE:
                return "value";
            case STRING:
            case DATE:
            case DECIMAL:
                return "text";
            case BOOLEAN:
            case INTEGER:
                return "number";
            case ENTITY:
                return "entity";
        }

        return null;
    }
}
