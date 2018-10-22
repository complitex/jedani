package ru.complitex.domain.component.form;

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
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.domain.util.Locales;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 12:45
 */
public class DomainAutoComplete extends Panel {
    @Inject
    private DomainMapper domainMapper;

    @Inject
    private EntityService entityService;

    private AutoCompleteTextField<Domain> autoCompleteTextField;

    public DomainAutoComplete(String id, String entityName, Long entityAttributeId, IModel<Long> model, boolean upperCase) {
        super(id);

        HiddenField inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

        EntityAttribute entityAttribute = entityService.getEntityAttribute(entityName, entityAttributeId);

        IModel<Domain> domainModel = new Model<Domain>(){
            @Override
            public void setObject(Domain domain) {
                if (domain == null){
                    model.setObject(null);
                }

                super.setObject(domain);
            }
        };

        if (model.getObject() != null){
            domainModel.setObject(domainMapper.getDomain(entityName, model.getObject()));
        }

        autoCompleteTextField = new AutoCompleteTextField<Domain>("input", domainModel, Domain.class,
                new AbstractAutoCompleteTextRenderer<Domain>() {

                    @Override
                    protected String getTextValue(Domain domain) {
                        Attribute attribute = domain.getOrCreateAttribute(entityAttributeId);

                        switch (entityAttribute.getValueType()){
                            case TEXT_VALUE:
                                String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                                return getPrefix(domain) +
                                        (textValue != null && upperCase ? Attributes.capitalize(textValue) : textValue);
                            case TEXT:
                                String text = attribute.getText();

                                return getPrefix(domain) +
                                        (text != null && upperCase ? Attributes.capitalize(text) : text);
                            case NUMBER:
                                return attribute.getNumber() + "";
                        }

                        return null;
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Domain item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); input";
                    }
                },
                new AutoCompleteSettings()
                        .setAdjustInputWidth(true)
                        .setShowListOnFocusGain(true)
                        .setPreselect(true)
        ) {
            @Override
            protected Iterator<Domain> getChoices(String input) {
                Domain domain = new Domain(entityName);
                domain.getOrCreateAttribute(entityAttributeId).setText(input);

                return domainMapper.getDomains(FilterWrapper.of(domain).limit(0L, 10L)).iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                if (Domain.class.equals(type)) {
                    return new IConverter<Domain>() {
                        @Override
                        public Domain convertToObject(String s, Locale locale) throws ConversionException {
                            if (s == null || s.isEmpty()){
                                return null;
                            }

                            Domain domain = new Domain();

                            Attribute attribute = domain.getOrCreateAttribute(entityAttributeId);

                            switch (entityAttribute.getValueType()){
                                case TEXT_VALUE:
                                    attribute.setTextValue(upperCase ? s.toUpperCase() : s, Locales.getLocaleId(locale));
                                    break;
                                case TEXT:
                                    attribute.setText(upperCase ? s.toUpperCase() : s);;
                                    break;
                                case NUMBER:
                                    try {
                                        attribute.setNumber(Long.parseLong(s));
                                    } catch (NumberFormatException e) {
                                        //
                                    }
                                    break;
                            }

                            return domain;
                        }

                        @Override
                        public String convertToString(Domain domain, Locale locale) {
                            Attribute attribute = domain.getOrCreateAttribute(entityAttributeId);

                            switch (entityAttribute.getValueType()){
                                case TEXT_VALUE:
                                    String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                                    return getPrefix(domain) + (upperCase ? Attributes.capitalize(textValue) : textValue);
                                case TEXT:
                                    String text = attribute.getText();

                                    return getPrefix(domain) + (upperCase ? Attributes.capitalize(text) : text);
                                case NUMBER:
                                    return attribute.getNumber() + "";
                            }

                            return null;
                        }
                    };
                }

                return super.createConverter(type);
            }
        };

        add(autoCompleteTextField);
    }

    public AutoCompleteTextField<Domain> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }

    protected String getPrefix(Domain domain){
        return "";
    }
}
