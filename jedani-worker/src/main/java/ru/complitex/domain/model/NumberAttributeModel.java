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

    private IModel<? extends Domain> domainModel;

    public NumberAttributeModel(Domain domain, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domain = domain;
    }

    public NumberAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domainModel = domainModel;
    }

    @Override
    public Long getObject() {
        return domainModel != null
                ? domainModel.getObject().getNumber(entityAttributeId)
                : domain.getNumber(entityAttributeId);
    }

    @Override
    public void setObject(Long object) {
        if (domainModel != null){
            domainModel.getObject().setNumber(entityAttributeId, object);
        }else{
            domain.setNumber(entityAttributeId, object);
        }
    }
}
