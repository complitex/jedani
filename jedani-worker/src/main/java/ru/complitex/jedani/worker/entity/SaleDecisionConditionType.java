package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.ValueType;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 25.05.2019 18:33
 */
public enum SaleDecisionConditionType implements IConditionType{
    PAYMENT_DATE(1L, ValueType.DATE),
    PAYMENT_PERCENT(2L, ValueType.NUMBER),
    PAYMENT_PERIOD_MONTH(3L, ValueType.NUMBER),
    PAYMENT_MONTHLY(4L, ValueType.BOOLEAN),
    PAYMENT_TOTAL(5L, ValueType.DECIMAL),
    FOR_YOURSELF(6L, ValueType.BOOLEAN),
    QUANTITY(7L, ValueType.NUMBER);

    private Long id;
    private ValueType valueType;

    SaleDecisionConditionType(Long id, ValueType valueType) {
        this.id = id;
        this.valueType = valueType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    public static SaleDecisionConditionType getValue(Long id){
        return Arrays.stream(values()).filter(t -> Objects.equals(id, t.getId())).findAny().orElse(null);
    }
}
