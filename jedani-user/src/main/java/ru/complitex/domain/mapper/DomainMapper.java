package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Domain;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class DomainMapper {
    @Inject
    private SqlSession sqlSession;

    public void insertDomain(Domain domain){
        sqlSession.insert("insertDomain", domain);
    }
}
