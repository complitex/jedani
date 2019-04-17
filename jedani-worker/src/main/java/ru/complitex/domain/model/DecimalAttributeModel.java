package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:44
 */
public class DecimalAttributeModel implements IModel<BigDecimal> {
    private Domain domain;
    private IModel<? extends Domain> domainModel;

    private Long entityAttributeId;

    private Attribute attribute;

    public DecimalAttributeModel(Domain domain, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domain = domain;
    }

    public DecimalAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.entityAttributeId = entityAttributeId;
        this.domainModel = domainModel;
    }

    public DecimalAttributeModel(Attribute attribute){
        this.attribute = attribute;
    }

    @Override
    public BigDecimal getObject() {
        String s = domainModel != null
                ? domainModel.getObject().getText(entityAttributeId)
                : attribute != null
                ? attribute.getText()
                : domain.getText(entityAttributeId);

        return s != null ? new BigDecimal(s) : null;
    }

    @Override
    public void setObject(BigDecimal object) {
        String text = object != null ? object.toPlainString() : null;

        if (domainModel != null){
            domainModel.getObject().setText(entityAttributeId, text);
        }else if (domain != null){
            domain.setText(entityAttributeId, text);
        }else {
            attribute.setText(text);
        }
    }

    public static DecimalAttributeModel of(IModel<? extends Domain> domainModel, Long entityAttributeId){
        return new DecimalAttributeModel(domainModel, entityAttributeId);
    }

    public static DecimalAttributeModel of(Attribute attribute){
        return new DecimalAttributeModel(attribute);
    }
}
