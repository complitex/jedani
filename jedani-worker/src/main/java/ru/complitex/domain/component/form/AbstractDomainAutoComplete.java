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
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 09.01.2019 20:50
 */
public abstract class AbstractDomainAutoComplete<T extends Domain<T>> extends FormComponentPanel<Long> {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    private final Class<T> domainClass;

    private final HiddenField<Long> inputId;
    private final AutoCompleteTextField<T> autoCompleteTextField;

    private boolean error;
    private boolean errorRendered;

    private SerializableConsumer<AjaxRequestTarget> onChange;

    public AbstractDomainAutoComplete(String id, Class<T> domainClass, IModel<Long> model) {
        super(id, model);

        setOutputMarkupId(true);

        this.domainClass = domainClass;

        inputId = new HiddenField<>("inputId", new LoadableDetachableModel<>() {
            @Override
            protected Long load() {
                return AbstractDomainAutoComplete.this.getModel().getObject();
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

        autoCompleteTextField = new AutoCompleteTextField<>("input", new LoadableDetachableModel<T>() {
            @Override
            protected T load() {
                Long objectId = AbstractDomainAutoComplete.this.getModel().getObject();

                return objectId != null ? AbstractDomainAutoComplete.this.getDomain(objectId) : null;
            }
        }, domainClass,
                new AbstractAutoCompleteTextRenderer<T>() {
                    @Override
                    protected String getTextValue(T domain) {
                        return AbstractDomainAutoComplete.this.getTextValue(domain);
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(T item) {
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
            protected Iterator<T> getChoices(String input) {
                return getDomains(input).iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                return new IConverter<T>() {
                    @Override
                    public T convertToObject(String s, Locale locale) throws ConversionException {
                        if (s != null && !s.isEmpty()){
                            Long objectId = inputId.getConvertedInput();

                            if (objectId != null){
                                T domain =  getDomain(objectId);

                                if (s.equalsIgnoreCase(getTextValue(domain))){
                                    return domain;
                                }
                            }
                        }

                        return null;
                    }

                    @Override
                    public String convertToString(T domain, Locale locale) {
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

    public Class<T> getDomainClass() {
        return domainClass;
    }

    @Override
    public void convertInput() {
        Domain<?> domain = autoCompleteTextField.getConvertedInput();

        setConvertedInput(domain != null ? domain.getObjectId() : null);
    }

    protected T getDomain(Long objectId) {
        return domainService.getDomain(domainClass, objectId);
    }

    protected abstract T getFilterObject(String input);

    protected List<T> getDomains(String input) {
        T domain = getFilterObject(input);

        domain.getAttributes().forEach(a -> entityService.loadReference(a.getEntityAttribute()));

        return domainService.getDomains(domainClass, FilterWrapper.of(domain).setFilter("search").limit(0L, 10L));
    }

    protected abstract String getTextValue(T domain) ;

    public AutoCompleteTextField<T> getAutoCompleteTextField() {
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

    public AbstractDomainAutoComplete<T> onChange(SerializableConsumer<AjaxRequestTarget> onChange) {
        this.onChange = onChange;

        return this;
    }

    public SerializableConsumer<AjaxRequestTarget> getOnChange() {
        return onChange;
    }

    public void detachModels(){
        super.detachModels();

        inputId.detachModels();
        autoCompleteTextField.detachModels();
    }
}
