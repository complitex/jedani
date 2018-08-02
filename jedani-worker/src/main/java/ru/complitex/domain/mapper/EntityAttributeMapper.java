package ru.complitex.domain.mapper;

import ru.complitex.common.mybatis.BaseMapper;
import ru.complitex.common.util.MapUtil;
import ru.complitex.domain.entity.EntityAttribute;

/**
 * @author Anatoly A. Ivanov
 * 06.12.2017 18:09
 */
public class EntityAttributeMapper extends BaseMapper {
    public EntityAttribute getEntityAttribute(String entityName, Long entityAttributeId){
        return sqlSession().selectOne("selectEntityAttribute", MapUtil.of("entityName", entityName,
                "entityAttributeId", entityAttributeId));
    }
}
