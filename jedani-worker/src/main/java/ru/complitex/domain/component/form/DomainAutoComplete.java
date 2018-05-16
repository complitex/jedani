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
import ru.complitex.domain.mapper.EntityAttributeMapper;
import ru.complitex.domain.util.Locales;

import javax.inject.Inject;
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
    private EntityAttributeMapper entityAttributeMapper;

    private AutoCompleteTextField autoCompleteTextField;

    public DomainAutoComplete(String id, String entityName, Long entityAttributeId, IModel<Long> model) {
        super(id);

        HiddenField inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

        EntityAttribute entityAttribute = entityAttributeMapper.getEntityAttribute(entityName, entityAttributeId);

        IModel<Attribute> attributeModel = new Model<>();

        if (model.getObject() != null){
            attributeModel.setObject(domainMapper.getDomain(entityName, model.getObject())
                    .getAttribute(entityAttributeId));
        }

        autoCompleteTextField = new AutoCompleteTextField<Attribute>("input", attributeModel, Attribute.class,
                new AbstractAutoCompleteTextRenderer<Attribute>() {

                    @Override
                    protected String getTextValue(Attribute attribute) {
                        switch (entityAttribute.getValueType()){
                            case VALUE:
                                return attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();
                            case TEXT:
                                return attribute.getText();
                            case NUMBER:
                                return attribute.getNumber() + "";
                        }

                        return null;
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Attribute item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); input";
                    }
                }, new AutoCompleteSettings().setAdjustInputWidth(true).setShowListOnFocusGain(true).setPreselect(true)) {
            @Override
            protected Iterator<Attribute> getChoices(String input) {
                Domain domain = new Domain(entityName);
                domain.getOrCreateAttribute(entityAttributeId).setText(input); //todo def attribute

                return domainMapper.getDomains(FilterWrapper.of(domain).limit(0L, 10L)
                        .sort("value", domain.getAttribute(entityAttributeId)).asc())
                        .stream() //todo opt load attribute
                        .map(d -> d.getAttribute(entityAttributeId))
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
                                case VALUE:
                                     attribute.setValue(s, Locales.getLocaleId(locale));
                                     break;
                                case TEXT:
                                    attribute.setText(s);;
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
                                case VALUE:
                                    return attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();
                                case TEXT:
                                    return attribute.getText();
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

    public AutoCompleteTextField getAutoCompleteTextField() {
        return autoCompleteTextField;
    }




}
