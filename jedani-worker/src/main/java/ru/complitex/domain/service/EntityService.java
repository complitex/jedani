package ru.complitex.domain.service;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.mapper.EntityAttributeMapper;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 16.05.2018 14:01
 */
public class EntityService implements Serializable {
    @Inject
    private EntityAttributeMapper entityAttributeMapper;

    public void setRefEntityAttribute(Entity entity, Long entityAttributeId, String refEntityName, Long refEntityAttributeId){
        entity.getEntityAttribute(entityAttributeId).setRefEntityAttribute(entityAttributeMapper
                .getEntityAttribute(refEntityName, refEntityAttributeId));
    }
}
