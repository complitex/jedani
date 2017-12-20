package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Value;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 01.12.2017 15:42
 */
public class ValueMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertValue(Value value){
        sqlSession.insert("insertValue", value);
    }
}
