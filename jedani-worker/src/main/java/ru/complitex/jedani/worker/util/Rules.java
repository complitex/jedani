package ru.complitex.jedani.worker.util;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.ValueType;

import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 01.10.2019 4:58 PM
 */
public class Rules {
    public static void updateValues(Domain domain, Long valueTypeEAId, Long valueEAId, Long comparatorEAId){
        if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.BOOLEAN.getId())){
            domain.setText(valueEAId, null);
            domain.setDate(valueEAId, null);
            domain.setNumber(comparatorEAId, null);
        }else if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.DECIMAL.getId())){
            domain.setNumber(valueEAId, null);
            domain.setDate(valueEAId, null);
        }else if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.NUMBER.getId())){
            domain.setText(valueEAId, null);
            domain.setDate(valueEAId, null);
        }else if (Objects.equals(domain.getNumber(valueTypeEAId), ValueType.DATE.getId())){
            domain.setNumber(valueEAId, null);
            domain.setText(valueEAId, null);
        }
    }
}
