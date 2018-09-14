package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:40
 */
public class DateAttributeModel implements IModel<Date> {
    private Domain domain;
    private Long entityAttributeId;

    public DateAttributeModel(Domain domain, Long entityAttributeId) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;
    }

    @Override
    public Date getObject() {
        return domain.getDate(entityAttributeId);
    }

    @Override
    public void setObject(Date object) {
        domain.setDate(entityAttributeId, object);
    }
}
