package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:44
 */
public class DecimalAttributeModel implements IModel<BigDecimal> {
    private Domain domain;
    private Long entityAttributeId;

    private IModel<? extends Domain> domainModel;

    public DecimalAttributeModel(Domain domain, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domain = domain;
    }

    public DecimalAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domainModel = domainModel;
    }

    @Override
    public BigDecimal getObject() {
        String s = domainModel != null
                ? domainModel.getObject().getText(entityAttributeId)
                : domain.getText(entityAttributeId);

        return s != null ? new BigDecimal(s) : null;
    }

    @Override
    public void setObject(BigDecimal object) {
        if (domainModel != null){
            domainModel.getObject().setText(entityAttributeId, object.toPlainString());
        }else{
            domain.setText(entityAttributeId, object.toPlainString());
        }
    }

    public static DecimalAttributeModel of(IModel<? extends Domain> domainModel, Long entityAttributeId){
        return new DecimalAttributeModel(domainModel, entityAttributeId);
    }
}
