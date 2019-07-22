package ru.complitex.common.wicket.form;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.TextAttributeModel;

/**
 * @author Anatoly A. Ivanov
 * 16.07.2019 18:02
 */
public class FormGroupStringField extends FormGroupTextField<String>{
    public FormGroupStringField(String id, IModel<? extends Domain> domainModel, Long entityAttributeId) {
        super(id, TextAttributeModel.of(domainModel, entityAttributeId));
    }
}
