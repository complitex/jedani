package ru.complitex.domain.entity;

public enum ValueType implements IdEnum {
    STRING_VALUE(0),
    STRING(1),
    BOOLEAN(2),
    DECIMAL(3),
    INTEGER(4),
    DATE(5),

    ENTITY(10),

    BUILDING_CODE(20),
    LAST_NAME(21),
    FIRST_NAME(22),
    MIDDLE_NAME(23);

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
}
