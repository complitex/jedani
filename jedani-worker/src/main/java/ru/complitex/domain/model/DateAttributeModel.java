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

    private IModel<? extends Domain> domainModel;

    public DateAttributeModel(Domain domain, Long entityAttributeId) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;
    }

    public DateAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domainModel = domainModel;
    }

    @Override
    public Date getObject() {
        if (domainModel != null){
            return domainModel.getObject().getDate(entityAttributeId);
        }

        return domain.getDate(entityAttributeId);
    }

    @Override
    public void setObject(Date object) {
        if (domainModel != null){
            domainModel.getObject().setDate(entityAttributeId, object);
        }else {
            domain.setDate(entityAttributeId, object);
        }
    }

    public static DateAttributeModel of(IModel<? extends Domain> domainModel, Long entityAttributeId){
        return new DateAttributeModel(domainModel, entityAttributeId);
    }
}
