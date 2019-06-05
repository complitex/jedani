package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.ValueType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum  RuleActionType {
    EURO_RATE_AS_INITIAL_PAYMENT(1L, ValueType.BOOLEAN),
    EURO_RATE_LESS_OR_EQUAL(2L, ValueType.DECIMAL),
    DISCOUNT(3L, ValueType.NUMBER),
    PRICE(4L, ValueType.DECIMAL);

    private Long id;
    private ValueType valueType;

    RuleActionType(Long id, ValueType valueType) {
        this.id = id;
        this.valueType = valueType;
    }

    public Long getId() {
        return id;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public static RuleActionType getValue(Long id){
        if (id == null){
            return null;
        }

        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(values()).filter(t -> Objects.equals(id, t.getId())).findAny().get();
    }

    public static List<Long> getIds(){
        return Arrays.stream(values()).map(RuleActionType::getId).collect(Collectors.toList());
    }
}
