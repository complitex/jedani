package ru.complitex.domain.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.entity.Value;
import ru.complitex.domain.util.Attributes;

/**
 * @author Anatoly A. Ivanov
 * 14.09.2018 13:46
 */
public class TextAttributeModel implements IModel<String> {
    private Domain domain;
    private Long entityAttributeId;

    private Attribute attribute;

    private Value value;

    private StringType type;

    private IModel<? extends Domain> domainModel;

    public TextAttributeModel(Domain domain, Long entityAttributeId) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;

        this.type = StringType.DEFAULT;
    }

    public TextAttributeModel(Domain domain, Long entityAttributeId, StringType type) {
        this.domain = domain;
        this.entityAttributeId = entityAttributeId;

        this.type = type;
    }

    public TextAttributeModel(Attribute attribute, StringType type) {
        this.attribute = attribute;
        this.type = type;
    }

    public TextAttributeModel(Value value, StringType type) {
        this.value = value;
        this.type = type;
    }

    public TextAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId, StringType type) {
        this.domainModel = domainModel;
        this.entityAttributeId = entityAttributeId;

        this.type = type;
    }

    public TextAttributeModel(IModel<? extends Domain> domainModel, Long entityAttributeId) {
        this.domainModel = domainModel;
        this.entityAttributeId = entityAttributeId;

        this.type = StringType.DEFAULT;
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
                return StringUtils.upperCase(text);
            case CAPITALIZE:
                return Attributes.capitalize(text);
        }

        return text;
    }

    @Override
    public void setObject(String text) {
        if (text != null){
            text = text.trim();

            if (!type.equals(StringType.DEFAULT)) {
                text = text.toUpperCase();
            }
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

    public static TextAttributeModel of(IModel<? extends Domain> model, Long entityAttributeId){
        return new TextAttributeModel(model, entityAttributeId);
    }
}
