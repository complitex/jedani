package ru.complitex.domain.model;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.util.Attributes;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2018 19:24
 */
public class TextValueModel implements IModel<String> {
    private IModel<? extends Domain> domainModel;
    private Long entityAttributeId;
    private Long localeId;

    public TextValueModel(IModel<? extends Domain> domainModel, Long entityAttributeId, Long localeId) {
        this.domainModel = domainModel;
        this.entityAttributeId = entityAttributeId;
        this.localeId = localeId;
    }

    @Override
    public String getObject() {
        return Attributes.capitalize(domainModel.getObject().getOrCreateAttribute(entityAttributeId)
                .getOrCreateValue(localeId).getText());
    }

    @Override
    public void setObject(String object) {
        if (object != null){
            object = object.trim();

            object = object.toUpperCase();
        }

        domainModel.getObject().getOrCreateAttribute(entityAttributeId).getOrCreateValue(localeId).setText(object);
    }
}
