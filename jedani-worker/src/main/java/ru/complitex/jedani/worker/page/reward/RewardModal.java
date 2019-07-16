package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupStringField;
import ru.complitex.domain.component.form.FormGroupAttributeSelect;
import ru.complitex.domain.component.form.ModalContainer;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;

public class RewardModal extends ModalContainer<Reward> {
    private IModel<Reward> model;

    public RewardModal(String markupId) {
        super(markupId);

        size(Size.Large);

        model = Model.of(new Reward());

        getContainer().add(new FormGroupDateTextField("date", model, Reward.DATE));

        getContainer().add(new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                model, Reward.WORKER)));

        getContainer().add(new FormGroupAttributeSelect("rank", model, Reward.RANK, Rank.ENTITY_NAME,
                Rank.NAME));

        getContainer().add(new FormGroupAttributeSelect("type", model, Reward.TYPE, RewardType.ENTITY_NAME,
                RewardType.NAME));

        getContainer().add(new FormGroupDecimalField("point", model, Reward.POINT));

        getContainer().add(new FormGroupStringField("detail", model, Reward.DETAIL));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);
    }

    @Override
    public void edit(Reward object, AjaxRequestTarget target) {
        super.edit(object, target);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        super.save(target);
    }

    @Override
    protected void cancel(AjaxRequestTarget target) {
        super.cancel(target);
    }
}
