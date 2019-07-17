package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupStringField;
import ru.complitex.domain.component.form.FormGroupAttributeSelect;
import ru.complitex.domain.component.form.ModalContainer;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;

import javax.inject.Inject;

public class RewardModal extends ModalContainer<Reward> {
    @Inject
    private DomainService domainService;

    private IModel<Reward> model;

    public RewardModal(String markupId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, onUpdate);

        size(Size.Large);

        model = Model.of(new Reward());

        getContainer().add(new FormGroupDateTextField("date", model, Reward.DATE).setRequired(true));

        getContainer().add(new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                model, Reward.WORKER).setRequired(true)));

        getContainer().add(new FormGroupAttributeSelect("type", model, Reward.TYPE, RewardType.ENTITY_NAME,
                RewardType.NAME));

        getContainer().add(new FormGroupAttributeSelect("rank", model, Reward.RANK, Rank.ENTITY_NAME,
                Rank.NAME));

        getContainer().add(new FormGroupDecimalField("point", model, Reward.POINT).setRequired(true));

        getContainer().add(new FormGroupStringField("detail", model, Reward.DETAIL));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Reward reward = new Reward();
        reward.setDate(Dates.currentDate());

        model.setObject(reward);
    }

    @Override
    public void edit(Reward object, AjaxRequestTarget target) {
        super.edit(object, target);

        model.setObject(object);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        super.save(target);

        domainService.save(model.getObject());

        success(getString("info_reward_saved"));
    }

    @Override
    protected void cancel(AjaxRequestTarget target) {
        super.cancel(target);
    }
}
