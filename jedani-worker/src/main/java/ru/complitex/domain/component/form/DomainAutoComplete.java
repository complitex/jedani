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
public class DomainAutoComplete extends Panel {
    @Inject
    private DomainMapper domainMapper;

    private AutoCompleteTextField<Domain> autoCompleteTextField;

    private EntityAttribute entityAttribute;

    public DomainAutoComplete(String id, EntityAttribute entityAttribute, IModel<Long> model) {
        super(id);

        this.entityAttribute = entityAttribute;

        HiddenField inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

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
            domainModel.setObject(domainMapper.getDomain(entityAttribute.getEntityName(), model.getObject()));
        }

        autoCompleteTextField = new AutoCompleteTextField<Domain>("input", domainModel, Domain.class,
                new AbstractAutoCompleteTextRenderer<Domain>() {

                    @Override
                    protected String getTextValue(Domain domain) {
                        return DomainAutoComplete.this.getTextValue(domain);
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
                return getDomains(input).iterator();
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

                            return new Domain();
                        }

                        @Override
                        public String convertToString(Domain domain, Locale locale) {
                            return getTextValue(domain);
                        }
                    };
                }

                return super.createConverter(type);
            }
        };

        add(autoCompleteTextField);
    }

    private List<Domain> getDomains(String input) {
        Domain domain = new Domain(entityAttribute.getEntityName());
        domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId()).setText(input);

        return domainMapper.getDomains(FilterWrapper.of(domain).limit(0L, 10L));
    }

    private String getTextValue(Domain domain) {
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

    AutoCompleteTextField<Domain> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }
}
