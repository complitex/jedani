package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.common.util.MapUtil;
import ru.complitex.domain.entity.Attribute;
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

    public String getReferenceEntityName(String entityName, Long entityAttributeId){
        return sqlSession.selectOne("selectReferenceEntityName", MapUtil.of("entityName", entityName,
                "entityAttributeId", entityAttributeId));
    }

    public String getReferenceEntityName(Attribute attribute){
        return sqlSession.selectOne("selectReferenceEntityName", MapUtil.of("entityName", attribute.getEntityName(),
                "entityAttributeId", attribute.getEntityAttributeId()));
    }



    //todo domain mapper -> region import
}
