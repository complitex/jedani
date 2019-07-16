package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Reward;

public class RewardListPage extends DomainListModalPage<Reward> {
    private RewardModal rewardModal;

    public RewardListPage() {
        super(Reward.class);

        Form rewardForm = new Form("rewardForm");
        getContainer().add(rewardForm);

        rewardForm.add(rewardModal = new RewardModal("rewardModal"));
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        rewardModal.create(target);
    }

    @Override
    protected void onEdit(Reward object, AjaxRequestTarget target) {
        rewardModal.edit(object, target);
    }
}
