package ru.complitex.domain.service;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.EntityMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 16.05.2018 14:01
 */
@ApplicationScoped
public class EntityService implements Serializable {
    @Inject
    private EntityMapper entityMapper;

    private Map<Long, Entity> idMap = new HashMap<>();
    private Map<String, Entity> entityNameMap = new HashMap<>();

    @SuppressWarnings("Duplicates")
    public Entity getEntity(Long id){
//        todo ref entity attributes configuration
//        Entity entity = idMap.get(id);
//
//        if (entity == null){
//            entity = entityMapper.getEntity(id);
//
//            idMap.put(id, entity);
//        }
//
//        return entity;

        return entityMapper.getEntity(id);
    }

    @SuppressWarnings("Duplicates")
    public Entity getEntity(String entityName){
//        Entity entity = entityNameMap.get(entityName);
//
//        if (entity == null){
//            entity = entityMapper.getEntity(entityName);
//
//            entityNameMap.put(entityName, entity);
//        }
//
//        return entity;

        return entityMapper.getEntity(entityName);
    }

    public EntityAttribute getEntityAttribute(String entityName, Long entityAttributeId){
        return getEntity(entityName).getEntityAttribute(entityAttributeId);
    }
}
