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
import ru.complitex.domain.util.Locales;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 12:45
 */
public class DomainAutoComplete extends Panel{
    private final static Attribute ATTRIBUTE = new Attribute();

    @Inject
    private DomainMapper domainMapper;

    public DomainAutoComplete(String id, String entityName, EntityAttribute entityAttribute, IModel<Long> model) {
        super(id);


        HiddenField inputId = new HiddenField<>("inputId", model);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

        AutoCompleteTextField input = new AutoCompleteTextField<Attribute>("input",
                new Model<Attribute>(){
                    @Override
                    public Attribute getObject() {
                        if (model.getObject() == null){
                            return null;
                        }

                        return domainMapper.getDomain(entityName, model.getObject())
                                .getAttribute(entityAttribute.getAttributeId());
                    }

                    @Override
                    public void setObject(Attribute attribute) {
                        if (attribute == null){
                            model.setObject(null);
                        }
                    }
                }, Attribute.class,
                new AbstractAutoCompleteTextRenderer<Attribute>() {

                    @Override
                    protected String getTextValue(Attribute attribute) {
                        switch (entityAttribute.getValueType().getKey()){
                            case "value":
                                return attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();
                            case "text":
                                return attribute.getText();
                            case "number":
                                return attribute.getNumber() + "";
                        }

                        return null;
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Attribute item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); input";
                    }
                }, new AutoCompleteSettings()) {
            @Override
            protected Iterator<Attribute> getChoices(String input) {
                Domain domain = new Domain(entityName);
                domain.getOrCreateAttribute(entityAttribute.getAttributeId()).setText(input);

                return domainMapper.getDomains(FilterWrapper.of(domain)).stream() //todo opt load attribute
                        .map(d -> d.getAttribute(entityAttribute.getAttributeId()))
                        .collect(Collectors.toList())
                        .iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                if (Attribute.class.equals(type)) {
                    return new IConverter<Attribute>() {
                        @Override
                        public Attribute convertToObject(String s, Locale locale) throws ConversionException {
                            return s != null ? ATTRIBUTE : null;
                        }

                        @Override
                        public String convertToString(Attribute attribute, Locale locale) {
                            switch (entityAttribute.getValueType().getKey()){
                                case "value":
                                    return attribute.getOrCreateValue(Locales.getSystemLocaleId()).getText();
                                case "text":
                                    return attribute.getText();
                                case "number":
                                    return attribute.getNumber() + "";
                            }

                            return null;
                        }
                    };
                }

                return super.createConverter(type);
            }
        };
        add(input);

    }
}
