package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.ValueType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum RuleConditionType {
    PAYMENT_DATE(1L, ValueType.DATE),
    PAYMENT_PERCENT(2L, ValueType.NUMBER),
    PAYMENT_PERIOD_MONTH(3L, ValueType.NUMBER),
    PAYMENT_MONTHLY(4L, ValueType.BOOLEAN),
    PAYMENT_SUM(5L, ValueType.DECIMAL);

    private Long id;
    private ValueType valueType;

    RuleConditionType(Long id, ValueType valueType) {
        this.id = id;
        this.valueType = valueType;
    }

    public Long getId() {
        return id;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public static RuleConditionType getValue(Long id){
        if (id == null){
            return null;
        }

        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(values()).filter(t -> Objects.equals(id, t.getId())).findAny().get();
    }

    public static List<Long> getIds(){
        return Arrays.stream(values()).map(RuleConditionType::getId).collect(Collectors.toList());
    }
}
