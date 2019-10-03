package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.ValueType;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 25.09.2019 1:47 PM
 */
public interface IConditionType {
    Long getId();

    ValueType getValueType();

    default String getName(){
        return ((Enum)this).name();
    }

    static <T extends Enum<T> & IConditionType> T getValue(Class<T> enumClass, Long id){
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(t -> Objects.equals(id, t.getId()))
                .findAny()
                .orElse(null);
    }
}
