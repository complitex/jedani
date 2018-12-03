package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.util.Workers;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2018 14:58
 */
public class WorkerAutoComplete extends Panel {
    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private NameService nameService;

    private AutoCompleteTextField<Worker> autoCompleteTextField;

    public WorkerAutoComplete(String id, IModel<Long> model) {
        super(id);

        HiddenField inputId = new HiddenField<>("inputId", model, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        inputId.add(OnChangeAjaxBehavior.onChange(this::onChange));
        add(inputId);

        autoCompleteTextField = new AutoCompleteTextField<Worker>("input", new IModel<Worker>() {
            @Override
            public Worker getObject() {
                if (model.getObject() != null){
                    return workerMapper.getWorker(model.getObject());
                }

                return null;
            }

            @Override
            public void setObject(Worker object) {
            }
        }, Worker.class, new AbstractAutoCompleteTextRenderer<Worker>() {
            @Override
            protected String getTextValue(Worker worker) {
                return Workers.getWorkerLabel(worker, nameService);
            }

            @Override
            protected CharSequence getOnSelectJavaScriptExpression(Worker item) {
                return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); " +
                        "$('#" + inputId.getMarkupId() + "').change(); input";
            }
        }, new AutoCompleteSettings().setAdjustInputWidth(true)
                .setShowListOnFocusGain(true)
                .setPreselect(true)) {
            @Override
            protected Iterator<Worker> getChoices(String s) {
                return workerMapper.getWorkers(s).iterator();
            }

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                return new IConverter<Worker>() {
                    @Override
                    public Worker convertToObject(String s, Locale locale) throws ConversionException {
                        return null;
                    }

                    @Override
                    public String convertToString(Worker worker, Locale locale) {
                        if (worker != null){
                            return Workers.getWorkerLabel(worker, nameService);
                        }

                        return null;
                    }
                };
            }
        };

        add(autoCompleteTextField);
    }

    public AutoCompleteTextField<Worker> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }

    protected void onChange(AjaxRequestTarget target){

    }

    public WorkerAutoComplete setRequired(boolean required){
        autoCompleteTextField.setRequired(true);

        return this;
    }

    public WorkerAutoComplete setLabel(IModel<String> labelModel){
        autoCompleteTextField.setLabel(labelModel);

        return this;
    }
}
