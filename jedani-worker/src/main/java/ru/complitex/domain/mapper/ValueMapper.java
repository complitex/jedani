package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.domain.entity.Value;

/**
 * @author Anatoly A. Ivanov
 * 01.12.2017 15:42
 */
public class ValueMapper extends BaseMapper {

    public void insertValue(Value value){
        if (value.getNumber() != null) {
            sqlSession().insert("insertValueWithNumber", value);
        } else {
            sqlSession().insert("insertValue", value);
        }
    }
}
