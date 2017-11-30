package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Attribute;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 30.11.2017 16:17
 */
public class AttributeMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertAttribute(Attribute attribute){
        sqlSession.insert("insertAttribute", attribute);
    }
}
