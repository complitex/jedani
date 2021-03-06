package ru.complitex.jedani.worker.page.period;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 18.11.2019 2:06 AM
 */
public class PeriodCalculateModal extends AbstractEditModal<Period> {
    private Logger log = LoggerFactory.getLogger(PeriodCalculateModal.class);

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardService rewardService;

    public PeriodCalculateModal(String markupId) {
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
    public void create(AjaxRequestTarget target) {
        super.create(target);

        setModelObject(periodMapper.getActualPeriod());
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        try {
            rewardService.calculateRewards();

            getSession().success(getString("info_rewards_calculated"));
        } catch (Exception e) {
            log.error("error calculate rewards ", e);

            getSession().error(getString("error_calculate_rewards"));
        }

        super.save(target);
    }

    @Override
    protected ResourceModel getSaveLabelModel() {
        return new ResourceModel("calculate");
    }
}
