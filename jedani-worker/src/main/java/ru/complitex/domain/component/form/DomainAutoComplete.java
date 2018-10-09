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
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 12:45
 */
public class DomainAutoComplete extends Panel {
    @Inject
    private DomainMapper domainMapper;

    @Inject
    private EntityService entityService;

    private AutoCompleteTextField<Attribute> autoCompleteTextField;

    public DomainAutoComplete(String id, String entityName, Long entityAttributeId, IModel<Long> model, boolean upperCase) {
        super(id);

        HiddenField inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

        EntityAttribute entityAttribute = entityService.getEntityAttribute(entityName, entityAttributeId);

        IModel<Attribute> attributeModel = new Model<Attribute>(){
            @Override
            public void setObject(Attribute attribute) {
                if (attribute == null){
                    model.setObject(null);
                }

                super.setObject(attribute);
            }
        };

        if (model.getObject() != null){
            attributeModel.setObject(domainMapper.getDomain(entityName, model.getObject())
                    .getAttribute(entityAttributeId));
        }

        autoCompleteTextField = new AutoCompleteTextField<Attribute>("input", attributeModel, Attribute.class,
                new AbstractAutoCompleteTextRenderer<Attribute>() {

                    @Override
                    protected String getTextValue(Attribute attribute) {
                        switch (entityAttribute.getValueType()){
                            case TEXT_VALUE:
                                String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                                return getPrefix(attribute) +
                                        (textValue != null && upperCase ? Attributes.capitalize(textValue) : textValue);
                            case TEXT:
                                String text = attribute.getText();

                                return getPrefix(attribute) +
                                        (text != null && upperCase ? Attributes.capitalize(text) : text);
                            case NUMBER:
                                return attribute.getNumber() + "";
                        }

                        return null;
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Attribute item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); input";
                    }
                },
                new AutoCompleteSettings()
                        .setAdjustInputWidth(true)
                        .setShowListOnFocusGain(true)
                        .setPreselect(true)
        ) {
            @Override
            protected Iterator<Attribute> getChoices(String input) {
                Domain domain = new Domain(entityName);
                domain.getOrCreateAttribute(entityAttributeId).setText(input); //todo def attribute


                return domainMapper.getDomains(FilterWrapper.of(domain).limit(0L, 10L))
                        .stream() //todo opt load attribute
                        .map(d -> d.getAttribute(entityAttributeId)) //todo sort duplicate
                        .sorted(Comparator.comparing(a -> a.getOrCreateValue(Locales.getSystemLocaleId()).getText()))
                        .collect(Collectors.toList())
                        .iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                if (Attribute.class.equals(type)) {
                    return new IConverter<Attribute>() {
                        @Override
                        public Attribute convertToObject(String s, Locale locale) throws ConversionException {
                            if (s == null || s.isEmpty()){
                                return null;
                            }

                            Attribute attribute = new Attribute(entityAttributeId);

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

                            return attribute;
                        }

                        @Override
                        public String convertToString(Attribute attribute, Locale locale) {
                            switch (entityAttribute.getValueType()){
                                case TEXT_VALUE:
                                    String textValue = attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();

                                    return getPrefix(attribute) + (upperCase ? Attributes.capitalize(textValue) : textValue);
                                case TEXT:
                                    String text = attribute.getText();

                                    return getPrefix(attribute) + (upperCase ? Attributes.capitalize(text) : text);
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

    public AutoCompleteTextField<Attribute> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }

    protected String getPrefix(Attribute attribute){
        return "";
    }
}
