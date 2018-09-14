package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:46
 */
public class TextAttributeModel implements IModel<String> {
    private Domain domain;
    private Long entityAttributeId;

    public TextAttributeModel(Domain domain, Long entityAttributeId) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;
    }

    @Override
    public String getObject() {
        return domain.getText(entityAttributeId);
    }

    @Override
    public void setObject(String object) {
        domain.setText(entityAttributeId, object);
    }
}
