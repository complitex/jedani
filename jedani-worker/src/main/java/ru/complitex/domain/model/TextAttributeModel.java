package ru.complitex.domain.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Value;
import ru.complitex.domain.util.Attributes;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:46
 */
public class TextAttributeModel implements IModel<String> {
    public enum TYPE {DEFAULT, LOWER_CASE, UPPER_CASE}

    private Domain domain;
    private Long entityAttributeId;

    private Attribute attribute;

    private Value value;

    private TYPE type;

    private IModel<? extends Domain> domainModel;

    public TextAttributeModel(Domain domain, Long entityAttributeId) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;

        this.type = TYPE.DEFAULT;
    }

    public TextAttributeModel(Domain domain, Long entityAttributeId, TYPE type) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;

        this.type = type;
    }

    public TextAttributeModel(Attribute attribute, TYPE type) {
        this.attribute = attribute;
        this.type = type;
    }

    public TextAttributeModel(Value value, TYPE type) {
        this.value = value;
        this.type = type;
    }

    public TextAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId, TYPE type) {
        this.domainModel = domainModel;
        this.entityAttributeId = entityAttributeId;

        this.type = type;
    }

    @Override
    public String getObject() {
        String text = domainModel != null ? domainModel.getObject().getText(entityAttributeId)
                : domain != null ? domain.getText(entityAttributeId)
                : attribute != null ? attribute.getText()
                : value.getText();

        switch (type){
            case LOWER_CASE:
                return StringUtils.lowerCase(text);
            case UPPER_CASE:
                return Attributes.capitalize(text);
        }

        return text;
    }

    @Override
    public void setObject(String text) {
        if (text != null && !type.equals(TYPE.DEFAULT)){
            text = text.toUpperCase();
        }

        if (domainModel != null){
            domainModel.getObject().setText(entityAttributeId, text);
        }else if (domain != null) {
            domain.setText(entityAttributeId, text);
        } else if (attribute != null) {
            attribute.setText(text);
        } else if (value != null){
            value.setText(text);
        }
    }
}
