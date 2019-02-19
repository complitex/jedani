package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:44
 */
public class NumberAttributeModel implements IModel<Long> {
    private Domain domain;
    private Long entityAttributeId;

    private IModel<? extends Domain> domainModel;

    private Attribute attribute;

    public NumberAttributeModel(Domain domain, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domain = domain;
    }

    public NumberAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domainModel = domainModel;
    }

    public NumberAttributeModel(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public Long getObject() {
        if (attribute != null){
            return attribute.getNumber();
        }

        return domainModel != null
                ? domainModel.getObject().getNumber(entityAttributeId)
                : domain.getNumber(entityAttributeId);
    }

    @Override
    public void setObject(Long object) {
        if (attribute != null){
            attribute.setNumber(object);
        }else if (domainModel != null){
            domainModel.getObject().setNumber(entityAttributeId, object);
        }else{
            domain.setNumber(entityAttributeId, object);
        }
    }

    public static NumberAttributeModel of(IModel<? extends Domain> domainModel, Long entityAttributeId){
        return new NumberAttributeModel(domainModel, entityAttributeId);
    }

    public static NumberAttributeModel of(Attribute attribute){
        return new NumberAttributeModel(attribute);
    }
}
