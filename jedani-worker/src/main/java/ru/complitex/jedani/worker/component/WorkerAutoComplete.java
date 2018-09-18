package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Objects;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
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

    public WorkerAutoComplete(String id, IModel<Long> workerIdModel) {
        super(id);

        HiddenField inputId = new HiddenField<>("inputId", workerIdModel, Long.class);
        inputId.setConvertEmptyInputStringToNull(true);
        inputId.setOutputMarkupId(true);
        inputId.add(OnChangeAjaxBehavior.onChange(this::onChange));
        add(inputId);

        IModel<Worker> workerModel = new Model<Worker>(){
            @Override
            public void setObject(Worker worker) {
                super.setObject(worker);

                if (worker != null){
                    workerIdModel.setObject(worker.getObjectId());
                }
            }
        };

        if (workerIdModel.getObject() != null){
            workerModel.setObject(workerMapper.getWorker(workerIdModel.getObject()));
        }

        autoCompleteTextField = new AutoCompleteTextField<Worker>("input", workerModel, Worker.class,
                new AbstractAutoCompleteTextRenderer<Worker>() {
                    @Override
                    protected String getTextValue(Worker worker) {
                        return WorkerAutoComplete.this.getTextValue(worker);
                    }

                    @Override
                    protected CharSequence getOnSelectJavaScriptExpression(Worker item) {
                        return "$('#" + inputId.getMarkupId() + "').val('" + item.getObjectId() + "'); " +
                                "$('#" + inputId.getMarkupId() + "').change(); input";
                    }
                },
                new AutoCompleteSettings().setAdjustInputWidth(true)
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
                        if (workerIdModel.getObject() != null){
                            return workerMapper.getWorker(workerIdModel.getObject());
                        }

                        return null;
                    }

                    @Override
                    public String convertToString(Worker worker, Locale locale) {
                        if (worker != null){
                            return getTextValue(worker);
                        }

                        return null;
                    }
                };
            }
        };

        add(autoCompleteTextField);
    }

    private String getTextValue(Worker worker){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + " " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME))) + " " +
                Attributes.capitalize(nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME))) + " " +
                        Attributes.capitalize(nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME)));
    }

    public AutoCompleteTextField<Worker> getAutoCompleteTextField() {
        return autoCompleteTextField;
    }

    protected void onChange(AjaxRequestTarget target){

    }
}
