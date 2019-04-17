package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.04.2019 21:55
 */
public class DomainParentModel implements IModel<Long> {
    private IModel<? extends Domain> domainModel;

    public DomainParentModel(IModel<? extends Domain>  domainModel){
        this.domainModel = domainModel;
    }

    @Override
    public Long getObject() {
        return domainModel.getObject().getParentId();
    }

    @Override
    public void setObject(Long object) {
        domainModel.getObject().setParentId(object);
    }

    public static DomainParentModel of(IModel<? extends Domain>  domainModel){
        return new DomainParentModel(domainModel);
    }
}
