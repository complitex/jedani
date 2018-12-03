package ru.complitex.domain.component.form;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
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

    private EntityAttribute entityAttribute;

    private HiddenField inputId;
    private AutoCompleteTextField<Domain> autoCompleteTextField;

    public DomainAutoComplete(String id, EntityAttribute entityAttribute, IModel<Long> model) {
        super(id);

        this.entityAttribute = entityAttribute;

        inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        add(inputId);

        autoCompleteTextField = new AutoCompleteTextField<Domain>("input", new IModel<Domain>() {
            @Override
            public Domain getObject() {
                if (model.getObject() != null){
                    return domainMapper.getDomain(getEntityName(), model.getObject());
                }

                return null;
            }

            @Override
            public void setObject(Domain object) {
            }
        }, Domain.class, new AbstractAutoCompleteTextRenderer<Domain>() {
            @Override
            protected String getTextValue(Domain domain) {
                return DomainAutoComplete.this.getTextValue(domain);
            }

            @Override
            protected CharSequence getOnSelectJavaScriptExpression(Domain item) {
                return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); input";
            }
        }, new AutoCompleteSettings()
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
                return new IConverter<Domain>() {
                    @Override
                    public Domain convertToObject(String s, Locale locale) throws ConversionException {
                        return null;
                    }

                    @Override
                    public String convertToString(Domain domain, Locale locale) {
                        if (domain == null){
                            return null;
                        }

                        return getTextValue(domain);
                    }
                };
            }
        };

        add(autoCompleteTextField);
    }

    public String getEntityName(){
        return entityAttribute.getEntityName();
    }

    protected Domain getDomain(IModel<Long> model) {
        return domainMapper.getDomain(entityAttribute.getEntityName(), model.getObject());
    }

    protected Domain getFilterObject(String input){
        Domain domain = new Domain(entityAttribute.getEntityName());
        domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId()).setText(input);

        return domain;
    }

    protected List<Domain> getDomains(String input) {
        return domainMapper.getDomains(FilterWrapper.of(getFilterObject(input))
                .setFilter("search")
                .limit(0L, 10L));
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

    public AutoCompleteTextField<Domain> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }

    public DomainAutoComplete setRequired(boolean required){
        autoCompleteTextField.setRequired(required);
//        inputId.setRequired(true);

        return this;
    }

    public DomainAutoComplete setLabel(IModel<String> labelModel){
        autoCompleteTextField.setLabel(labelModel);

        return this;
    }
}
