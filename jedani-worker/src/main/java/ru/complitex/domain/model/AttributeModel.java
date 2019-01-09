package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 09.01.2019 19:40
 */
public class AttributeModel implements IModel<Attribute> {
    private IModel<? extends Domain> domainModel;
    private Long entityAttributeId;

    public AttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.domainModel = domainModel;
        this.entityAttributeId = entityAttributeId;
    }

    @Override
    public Attribute getObject() {
        return domainModel.getObject().getOrCreateAttribute(entityAttributeId);
    }

    @Override
    public void setObject(Attribute object) {
        domainModel.getObject().removeAttribute(entityAttributeId);
        domainModel.getObject().getAttributes().add(object);
    }
}
