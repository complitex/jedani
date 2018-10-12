package ru.complitex.domain.service;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.EntityMapper;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 16.05.2018 14:01
 */
public class EntityService implements Serializable {
    @Inject
    private EntityMapper entityMapper;

    public Entity getEntity(Long id){
        return entityMapper.getEntity(id);
    }

    public Entity getEntity(String entityName){
        return entityMapper.getEntity(entityName);
    }

    public EntityAttribute getEntityAttribute(String entityName, Long entityAttributeId){
        return getEntity(entityName).getEntityAttribute(entityAttributeId);
    }
}
