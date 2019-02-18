package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Locales;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 12:45
 */
public class DomainAutoComplete extends AbstractDomainAutoComplete{
    @Inject
    private EntityService entityService;

    private EntityAttribute entityAttribute;

    public DomainAutoComplete(String id, String entityName, EntityAttribute entityAttribute,
                              IModel<Long> model, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(id, entityName, model, onChange);

        this.entityAttribute = entityAttribute;
    }

    public DomainAutoComplete(String id, String entityName, EntityAttribute entityAttribute, IModel<Long> model) {
        this(id, entityName, entityAttribute,  model, null);
    }

    public DomainAutoComplete(String id, String entityName, Long entityAttributeId, IModel<Long> model) {
        super(id, entityName, model, null);

        this.entityAttribute = entityService.getEntityAttribute(entityName, entityAttributeId);
    }

    protected Domain getFilterObject(String input){
        Domain domain = new Domain(entityAttribute.getEntityName());
        domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId()).setText(input);

        return domain;
    }

    protected String getTextValue(Domain domain) {
        Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_VALUE:
                String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                return Attributes.displayText(entityAttribute, textValue);
            case TEXT:
                return attribute.getText();
            case NUMBER:
                return attribute.getNumber() + "";
        }

        return null;
    }
}
