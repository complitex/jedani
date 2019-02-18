package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 19:37
 */
public class BooleanAttributeModel implements IModel<Boolean> {
    private IModel<? extends Domain> domainModel;
    private Long entityAttributeId;

    public BooleanAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.domainModel = domainModel;
        this.entityAttributeId = entityAttributeId;
    }

    @Override
    public Boolean getObject() {
        Long number = domainModel.getObject().getNumber(entityAttributeId);

        return number != null ? number.equals(1L) : null;
    }

    @Override
    public void setObject(Boolean object) {
        if (object != null){
            domainModel.getObject().setNumber(entityAttributeId, object ? 1L : 0);
        }else {
            domainModel.getObject().setNumber(entityAttributeId, null);
        }
    }

    public static BooleanAttributeModel of(IModel<? extends Domain> domainModel, Long entityAttributeId){
        return new BooleanAttributeModel(domainModel, entityAttributeId);
    }
}
