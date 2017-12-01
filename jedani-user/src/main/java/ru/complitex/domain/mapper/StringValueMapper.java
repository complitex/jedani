package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.StringValue;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 01.12.2017 15:42
 */
public class StringValueMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertStringValue(StringValue stringValue){
        sqlSession.insert("insertStringValue", stringValue);
    }
}
