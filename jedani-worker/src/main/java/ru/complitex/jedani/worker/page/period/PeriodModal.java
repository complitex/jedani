package ru.complitex.jedani.worker.page.period;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.AbstractDateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2019 10:14 PM
 */
public class PeriodModal extends AbstractEditModal<Period> {
    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainService domainService;

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
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Period period = new Period();

        period.setOperatingMonth(Dates.firstDayOfMonth());
        period.setWorkerId(getCurrentWorkerId());

        setModelObject(period);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        Period period = getModelObject();

        if (periodMapper.hasPeriod(period)){
            error(getString("error_has_period"));

            target.add(getFeedback());

            return;
        }

        try {
            domainService.save(period);

            super.save(target);

            getSession().success(getString("info_period_saved"));
        } catch (Exception e) {
            error(getString("error_period_save") + ": " + e.getLocalizedMessage());

            target.add(getFeedback());
        }
    }
}
