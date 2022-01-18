package ru.complitex.jedani.worker.page.period;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.PeriodService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 18.11.2019 2:07 AM
 */
public class PeriodCloseModal extends AbstractEditModal<Period> {
    private final Logger log = LoggerFactory.getLogger(PeriodCloseModal.class);

    @Inject
    private PeriodService periodService;

    @Inject
    private PeriodMapper periodMapper;

    private final IModel<Boolean> calculateModel = Model.of(false);

    public PeriodCloseModal(String markupId) {
        super(markupId);

        setModel(Model.of(new Period()));

        add(new Label("month", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Dates.getMonthText(getModelObject().getOperatingMonth());
            }
        }));
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        try {
            Period period = periodMapper.getPeriod(getModelObject().getObjectId());

            if (period.getCloseTimestamp() != null) {
                throw new RuntimeException("period already closed " + period);
            }

            periodService.closeOperatingMonth(getModelObject(), true, getCurrentWorkerId());

            if (calculateModel.getObject()) {
                getSession().success(getString("info_rewards_calculated"));
            }

            getSession().success(getString("info_period_closed"));
            getSession().success(getString("info_accounts_updated"));
        } catch (Exception e) {
            log.error("error close period", e);

            getSession().error(getString("error_close_period"));
        }

        super.save(target);
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        getModel().setObject(periodMapper.getActualPeriod());
    }

    @Override
    protected ResourceModel getSaveLabelModel() {
        return new ResourceModel("close");
    }
}
