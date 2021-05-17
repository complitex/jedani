package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Domains;
import ru.complitex.domain.util.Locales;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 12:45
 */
public class DomainAutoComplete<T extends Domain> extends AbstractDomainAutoComplete<T> {
    @Inject
    private EntityService entityService;

    private final EntityAttribute entityAttribute;

    public DomainAutoComplete(String id, Class<T> domainClass, EntityAttribute entityAttribute,IModel<Long> model) {
        super(id, domainClass, model);

        this.entityAttribute = entityAttribute;
    }

    public DomainAutoComplete(String id, Class<T> domainClass, Long entityAttributeId, IModel<Long> model) {
        super(id, domainClass, model);

        String entityName = Domains.getEntityName(domainClass);

        this.entityAttribute = entityService.getEntityAttribute(entityName, entityAttributeId);
    }

    protected T getFilterObject(String input){
        T domain = Domains.newObject(getDomainClass());

        domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId()).setText(input);

        return domain;
    }

    protected String getTextValue(T domain) {
        Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_LIST:
                String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                return Attributes.displayText(entityAttribute, textValue);
            case TEXT:
                return attribute.getText();
            case NUMBER:
                return attribute.getNumber() + "";
        }

        return null;
    }

    public DomainAutoComplete<T> onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        super.onChange(onChange);

        return this;
    }
}
