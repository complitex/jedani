package ru.complitex.domain.service;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.mapper.EntityMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 16.05.2018 14:01
 */
public class EntityService implements Serializable {
    @Inject
    private EntityMapper entityMapper;

    private Map<Long, Entity> idMap = new HashMap<>();

    public Entity getEntity(Long id){
        Entity entity = idMap.get(id);

        if (entity == null){
            entity = entityMapper.getEntity(id);

            idMap.put(id, entity);
        }

        return entity;
    }

}
