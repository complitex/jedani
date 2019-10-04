package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.ValueType;

/**
 * @author Anatoly A. Ivanov
 * 22.09.2019 22:19
 */
public enum PromotionConditionType implements IConditionType{
    INITIAL_PAYMENT_PERCENT(1L, ValueType.DECIMAL);

    private Long id;
    private ValueType valueType;

    PromotionConditionType(Long id, ValueType valueType) {
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
}
