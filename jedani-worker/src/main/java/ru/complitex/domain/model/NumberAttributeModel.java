package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:44
 */
public class NumberAttributeModel implements IModel<Long> {
    private Domain domain;
    private Long entityAttributeId;

    public NumberAttributeModel(Domain domain, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domain = domain;
    }

    @Override
    public Long getObject() {
        return domain.getNumber(entityAttributeId);
    }

    @Override
    public void setObject(Long object) {
        domain.setNumber(entityAttributeId, object);
    }
}
