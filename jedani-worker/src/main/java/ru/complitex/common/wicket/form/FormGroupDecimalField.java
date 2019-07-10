package ru.complitex.common.wicket.form;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.DecimalAttributeModel;

import java.math.BigDecimal;

public class FormGroupDecimalField extends FormGroupTextField<BigDecimal>{
    public FormGroupDecimalField(String id, IModel<? extends Domain> domainModel, Long entityAttributeId) {
        super(id, DecimalAttributeModel.of(domainModel, entityAttributeId), BigDecimal.class);
    }
}
