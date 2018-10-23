package ru.complitex.domain.component.form;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Locales;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 12:45
 */
public class DomainAutoComplete<T extends Domain> extends Panel {
    @Inject
    private DomainMapper domainMapper;

    private Class<T> domainClass;
    private EntityAttribute entityAttribute;

    private AutoCompleteTextField<T> autoCompleteTextField;

    public DomainAutoComplete(String id, Class<T> domainClass, EntityAttribute entityAttribute, IModel<Long> model) {
        super(id);

        this.entityAttribute = entityAttribute;

        HiddenField inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

        IModel<T> domainModel = new Model<T>(){
            @Override
            public void setObject(T domain) {
                if (domain == null){
                    model.setObject(null);
                }

                super.setObject(domain);
            }
        };

        if (model.getObject() != null){
            domainModel.setObject(getDomain(model));
        }

        autoCompleteTextField = new AutoCompleteTextField<T>("input", domainModel, domainClass,
                new AbstractAutoCompleteTextRenderer<T>() {

                    @Override
                    protected String getTextValue(T domain) {
                        return DomainAutoComplete.this.getTextValue(domain);
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(T item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); input";
                    }
                },
                new AutoCompleteSettings()
                        .setAdjustInputWidth(true)
                        .setShowListOnFocusGain(true)
                        .setPreselect(true)
        ) {
            @Override
            protected Iterator<T> getChoices(String input) {
                return getDomains(input).iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                return new IConverter<T>() {
                    @Override
                    public T convertToObject(String s, Locale locale) throws ConversionException {
                        if (s == null || s.isEmpty()){
                            return null;
                        }

                        try {
                            return domainClass.newInstance();
                        } catch (Exception e) {
                            throw new WicketRuntimeException(e);
                        }
                    }

                    @Override
                    public String convertToString(T domain, Locale locale) {
                        return getTextValue(domain);
                    }
                };
            }
        };

        add(autoCompleteTextField);
    }

    @SuppressWarnings("unchecked")
    protected T getDomain(IModel<Long> model) {
        return (T) domainMapper.getDomain(entityAttribute.getEntityName(), model.getObject());
    }

    @SuppressWarnings("unchecked")
    protected List<T> getDomains(String input) {
        Domain domain = new Domain(entityAttribute.getEntityName());
        domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId()).setText(input);

        return (List<T>) domainMapper.getDomains(FilterWrapper.of(domain).limit(0L, 10L));
    }

    protected String getTextValue(T domain) {
        Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId());

        switch (entityAttribute.getValueType()){
            case TEXT_VALUE:
                String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                return textValue != null && entityAttribute.isDisplayCapitalize()
                        ? Attributes.capitalize(textValue)
                        : textValue;
            case TEXT:
                String text = attribute.getText();

                return text != null && entityAttribute.isDisplayCapitalize()
                        ? Attributes.capitalize(text)
                        : text;
            case NUMBER:
                return attribute.getNumber() + "";
        }

        return null;
    }

    public AutoCompleteTextField<T> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }
}
