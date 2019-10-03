package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.ValueType;

/**
 * @author Anatoly A. Ivanov
 * 25.05.2019 18:33
 */
public enum SaleDecisionActionType implements IActionType{
    EURO_RATE_AS_INITIAL_PAYMENT(1L, ValueType.BOOLEAN),
    EURO_RATE_LESS_OR_EQUAL(2L, ValueType.DECIMAL),
    DISCOUNT(3L, ValueType.DECIMAL),
    PRICE(4L, ValueType.DECIMAL);

    private Long id;
    private ValueType valueType;

    SaleDecisionActionType(Long id, ValueType valueType) {
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
