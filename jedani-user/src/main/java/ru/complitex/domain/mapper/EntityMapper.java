package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.domain.entity.Entity;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 29.11.2017 17:54
 */
public class EntityMapper {
    @Inject
    private SqlSession sqlSession;

    public Entity getEntity(Long id){
        return sqlSession.selectOne("selectEntity", id);
    }

    public Entity getEntity(String entityName){
        return sqlSession.selectOne("selectEntityByName", entityName);
    }


    //todo domain mapper -> region import
}
