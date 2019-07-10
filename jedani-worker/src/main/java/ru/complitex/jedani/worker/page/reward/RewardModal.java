package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.model.IModel;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.component.form.FormGroupAttributeSelect;
import ru.complitex.domain.component.form.ModalPanel;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;

public class RewardModal extends ModalPanel<Reward> {
    private IModel<Reward> rewardModel;

    public RewardModal(String markupId) {
        super(markupId);

        getContainer().add(new FormGroupDateTextField("date", rewardModel, Reward.DATE));
        getContainer().add(new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                rewardModel, Reward.WORKER)));
        getContainer().add(new FormGroupDecimalField("point", rewardModel, Reward.POINT));
        getContainer().add(new FormGroupAttributeSelect("type", rewardModel, Reward.TYPE, RewardType.ENTITY_NAME,
                RewardType.NAME));
        getContainer().add(new FormGroupAttributeSelect("rank", rewardModel, Reward.RANK, Rank.ENTITY_NAME,
                Rank.NAME));
        getContainer().add(new FormGroupTextField<>("detail", rewardModel, Reward.DETAIL));


    }
}
