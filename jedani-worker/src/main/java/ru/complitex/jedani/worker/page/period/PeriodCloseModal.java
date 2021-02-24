package ru.complitex.jedani.worker.page.period;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
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
import ru.complitex.jedani.worker.service.PeriodService;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 18.11.2019 2:07 AM
 */
public class PeriodCloseModal extends AbstractEditModal<Period> {
    private Logger log = LoggerFactory.getLogger(PeriodCloseModal.class);

    @Inject
    private PeriodService periodService;

    @Inject
    private RewardService rewardService;

    private IModel<Boolean> calculateModel;

    public PeriodCloseModal(String markupId) {
        super(markupId);

        setModel(Model.of(new Period()));

        add(new Label("month", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Dates.getMonthText(getModelObject().getOperatingMonth());
            }
        }));

        calculateModel = Model.of(false);

        add(new BootstrapCheckbox("calculateRewards", calculateModel, new ResourceModel("accrueRewards")));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        setModelObject(periodService.getActualPeriod());
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        try {
            if (calculateModel.getObject()) {
                rewardService.calculateRewards();

                getSession().success(getString("info_rewards_accrued"));
            }

            periodService.closeOperatingMonth(getCurrentWorkerId());

            getSession().success(getString("info_period_closed"));
        } catch (Exception e) {
            log.error("error accrue rewards ", e);

            getSession().error(getString("error_accrue_rewards"));
        }

        super.save(target);
    }

    @Override
    protected ResourceModel getSaveLabelModel() {
        return new ResourceModel("close");
    }
}
