package ru.complitex.domain.component.form;

import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.function.Function;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.service.EntityService;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 09.01.2019 20:50
 */
public abstract class AbstractDomainAutoComplete extends FormComponentPanel<Long> {
    @Inject
    private DomainMapper domainMapper;

    @Inject
    private EntityService entityService;

    private String entityName;

    private HiddenField<Long> inputId;
    private AutoCompleteTextField<Domain> autoCompleteTextField;

    private boolean error;
    private boolean errorRendered;

    public AbstractDomainAutoComplete(String id, String entityName, IModel<Long> model,
                              SerializableConsumer<AjaxRequestTarget> onChange) {
        super(id, model);

        setOutputMarkupId(true);

        this.entityName = entityName;

        inputId = new HiddenField<>("inputId", new LoadableDetachableModel<Long>() {
            @Override
            protected Long load() {
                return model.getObject();
            }
        }, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);

        inputId.add(new OnChangeAjaxBehavior(){

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setModelObject(inputId.getModelObject());

                error = false;

                if (errorRendered){
                    target.appendJavaScript(JQuery.$(AbstractDomainAutoComplete.this)
                            .closest(".has-error")
                            .chain(new Function("removeClass", "has-error"))
                            .build());
                }

                if (onChange != null) {
                    onChange.accept(target);
                }

                errorRendered = false;
            }
        });

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
                        return AbstractDomainAutoComplete.this.getTextValue(domain);
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Domain item) {
                        String js = "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "');";

                        js +=  " $('#" + inputId.getMarkupId() + "').change();";

                        js += " input";

                        return js;
                    }
                }, new AutoCompleteSettings()
                .setAdjustInputWidth(true)
                .setShowListOnFocusGain(true)
                .setPreselect(true)
        ) {
            @Override
            protected void onComponentTag(final ComponentTag tag)
            {
                super.onComponentTag(tag);

                tag.put("autocomplete", "off");
            }

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
                return AbstractDomainAutoComplete.this.getLabel();
            }
        };
        add(autoCompleteTextField);
    }

    public AbstractDomainAutoComplete(String id, String entityName, IModel<Long> model){
        this(id, entityName, model, null);
    }

    @Override
    public void convertInput() {
        Domain domain = autoCompleteTextField.getConvertedInput();

        setConvertedInput(domain != null ? domain.getObjectId() : null);
    }

    public String getEntityName(){
        return entityName;
    }

    protected Domain getDomain(Long objectId) {
        return domainMapper.getDomain(getEntityName(), objectId);
    }

    protected abstract Domain getFilterObject(String input);

    protected List<? extends Domain> getDomains(String input) {
        Domain<?> domain = getFilterObject(input);

        domain.getAttributes().forEach(a -> entityService.loadReference(a.getEntityAttribute()));

        return domainMapper.getDomains(FilterWrapper.of(domain).setFilter("search").limit(0L, 10L));
    }

    protected abstract String getTextValue(Domain domain) ;

    public AutoCompleteTextField<Domain> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        FeedbackMessage feedbackMessage = getFeedbackMessages().first(FeedbackMessage.ERROR);

        if (error = feedbackMessage != null){
            feedbackMessage.markRendered();
        }

        if (!error){
            error = autoCompleteTextField.hasErrorMessage();
        }
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (error){
            tag.put("class", "has-error");

            errorRendered = true;
        }
    }

    public boolean isError() {
        return error;
    }

    public boolean isErrorRendered() {
        return errorRendered;
    }
}