package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.page.period.PeriodCalculateModal;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardListPage extends BasePage {
    private final RewardModal rewardModal;

    public RewardListPage() {
        RewardPanel rewardPanel = new RewardPanel("reward", getCurrentWorker()){
            @Override
            protected void onCreate(AjaxRequestTarget target) {
                rewardModal.create(target);
            }

            @Override
            protected void onEdit(Reward reward, AjaxRequestTarget target) {
                rewardModal.edit(reward, target);
            }

            @Override
            protected boolean isCurrentWorkerFilter() {
                return false;
            }

            @Override
            public boolean isEditEnabled() {
                return true;
            }

            @Override
            protected boolean isActualMonthFilter() {
                return false;
            }

            @Override
            protected Component getPagingLeft(String id) {
                return null;
            }
        };
        add(rewardPanel);

        if (isAdmin()) {
            rewardPanel.getFilterWrapper().put(Reward.FILTER_PERIOD, null);
        }

        Form<?> rewardForm = new Form<>("rewardForm");
        add(rewardForm);

        rewardModal = new RewardModal("rewardModal")
                .onUpdate(rewardPanel::update);

        rewardForm.add(rewardModal );

        Form<?> periodCalculateForm = new Form<>("periodCalculateForm");
        add(periodCalculateForm);

        PeriodCalculateModal periodCalculateModal = new PeriodCalculateModal("periodCalculateModal")
                .onUpdate(rewardPanel::update);

        periodCalculateForm.add(periodCalculateModal);

        add(new AjaxLink<Void>("calculateRewards") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                periodCalculateModal.create(target);
            }
        });
    }
}
