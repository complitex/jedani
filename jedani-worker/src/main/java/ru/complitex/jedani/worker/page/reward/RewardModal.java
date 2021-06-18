package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupStringField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.component.form.FormGroupAttributeSelect;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.FormGroupWorker;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class RewardModal extends AbstractEditModal<Reward> {
    @Inject
    private DomainService domainService;

    private final IModel<Reward> model;

    public RewardModal(String markupId) {
        super(markupId);

        model = Model.of(new Reward());

        add(new FormGroupDateTextField("date", model, Reward.DATE).setRequired(true));

        add(new FormGroupWorker("worker", new PropertyModel<>(model, "workerId")).setRequired(true));

        add(new FormGroupAttributeSelect("type", model, Reward.TYPE, RewardType.ENTITY_NAME, RewardType.NAME));

        add(new FormGroupAttributeSelect("rank", model, Reward.RANK, Rank.ENTITY_NAME, Rank.NAME));

        add(new FormGroupDecimalField("point", model, Reward.POINT).setRequired(true));

        add(new FormGroupStringField("detail", model, Reward.DETAIL));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Reward reward = new Reward();
        reward.setDate(Dates.currentDate());

        model.setObject(reward);
    }

    @Override
    public void edit(Long rewardId, AjaxRequestTarget target) {
        Reward reward = domainService.getDomain(Reward.class, rewardId);

        super.edit(rewardId, target);

        model.setObject(reward);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        super.save(target);

        domainService.save(model.getObject());

        success(getString("info_reward_saved"));
    }
}
