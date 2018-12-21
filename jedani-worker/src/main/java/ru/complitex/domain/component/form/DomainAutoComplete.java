package ru.complitex.domain.component.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.danekja.java.util.function.serializable.SerializableConsumer;
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
public class DomainAutoComplete extends FormComponentPanel<Long> {
    @Inject
    private DomainMapper domainMapper;

    private EntityAttribute entityAttribute;

    private HiddenField<Long> inputId;
    private AutoCompleteTextField<Domain> autoCompleteTextField;

    public DomainAutoComplete(String id, EntityAttribute entityAttribute, IModel<Long> model,
                              SerializableConsumer<AjaxRequestTarget> onChange) {
        super(id, model);

        this.entityAttribute = entityAttribute;

        inputId = new HiddenField<>("inputId", new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return model.getObject();
            }
        }, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);

        if (onChange != null){
            inputId.add(new OnChangeAjaxBehavior(){

                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    setModelObject(inputId.getModelObject());

                    onChange.accept(target);
                }
            });
        }

        add(inputId);

        autoCompleteTextField = new AutoCompleteTextField<Domain>("input", new LoadableDetachableModel<Domain>() {
            @Override
            protected Domain load() {
                return model.getObject() != null  ? getDomain(model.getObject()) : null;
            }
        }, Domain.class,
                new AbstractAutoCompleteTextRenderer<Domain>() {
                    @Override
                    protected String getTextValue(Domain domain) {
                        return DomainAutoComplete.this.getTextValue(domain);
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Domain item) {
                        String js = "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "');";

                        if (onChange != null){
                            js +=  " $('#" + inputId.getMarkupId() + "').change();";
                        }

                        js += " input";

                        return js;
                    }
                }, new AutoCompleteSettings()
                .setAdjustInputWidth(true)
                .setShowListOnFocusGain(true)
                .setPreselect(true)
        ) {
            @Override
            protected Iterator<Domain> getChoices(String input) {
                return getDomains(input).stream().map(d -> (Domain) d).iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                return new IConverter<Domain>() {
                    @Override
                    public Domain convertToObject(String s, Locale locale) throws ConversionException {
                        if (s != null && !s.isEmpty()){
                            Long objectId = inputId.getConvertedInput();

                            if (objectId != null){
                                Domain domain =  getDomain(objectId);

                                if (s.equalsIgnoreCase(getTextValue(domain))){
                                    return domain;
                                }
                            }
                        }

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

            @Override
            public IModel<String> getLabel() {
                return DomainAutoComplete.this.getLabel();
            }
        };
        add(autoCompleteTextField);
    }

    public DomainAutoComplete(String id, EntityAttribute entityAttribute, IModel<Long> model){
        this(id, entityAttribute, model, null);
    }

    @Override
    public void convertInput() {
        Domain domain = autoCompleteTextField.getConvertedInput();

        setConvertedInput(domain != null ? domain.getObjectId() : null);
    }

    public String getEntityName(){
        return entityAttribute.getEntityName();
    }

    protected Domain getDomain(Long objectId) {
        return domainMapper.getDomain(getEntityName(), objectId);
    }

    protected Domain getFilterObject(String input){
        Domain domain = new Domain(entityAttribute.getEntityName());
        domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId()).setText(input);

        return domain;
    }

    protected List<? extends Domain> getDomains(String input) {
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
}
