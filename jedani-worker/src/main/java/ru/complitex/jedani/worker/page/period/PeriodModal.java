package ru.complitex.jedani.worker.page.period;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.AbstractDateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.jedani.worker.entity.Period;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2019 10:14 PM
 */
public class PeriodModal extends AbstractEditModal<Period> {
    public PeriodModal(String markupId) {
        super(markupId);

        setModel(Model.of(new Period()));

        add(new FormGroupDateTextField("month", getModel(), Period.OPERATING_MONTH){
            @Override
            protected DateTextFieldConfig getDateTextFieldConfig() {
                return super.getDateTextFieldConfig()
                        .withFormat("MM.yyyy")
                        .withMinViewMode(AbstractDateTextFieldConfig.View.Month);
            }
        }.setRequired(true));
        add(new FormGroupDateTextField("start", getModel(), Period.PERIOD_START).setRequired(true));
        add(new FormGroupDateTextField("end", getModel(), Period.PERIOD_END).setRequired(true));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Period period = new Period();

        period.setOperatingMonth(Dates.firstDayOfMonth());
        period.setPeriodStart(Dates.firstDayOfMonth());
        period.setPeriodEnd(Dates.lastDayOfMonth());

        setModelObject(period);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        super.save(target);
    }
}
