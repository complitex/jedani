package ru.complitex.domain.mapper;

import org.apache.ibatis.session.SqlSession;
import ru.complitex.common.util.MapUtil;
import ru.complitex.domain.entity.EntityAttribute;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 06.12.2017 18:09
 */
public class EntityAttributeMapper implements Serializable {
    @Inject
    private transient SqlSession sqlSession;

    public EntityAttribute getEntityAttribute(String entityName, Long entityAttributeId){
        return sqlSession.selectOne("selectEntityAttribute", MapUtil.of("entityName", entityName,
                "entityAttributeId", entityAttributeId));
    }
}
