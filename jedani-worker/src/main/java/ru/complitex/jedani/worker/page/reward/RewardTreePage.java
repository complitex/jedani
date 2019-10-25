package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.entity.WorkerNode;
import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.entity.WorkerRewardTree;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 23.10.2019 11:15 PM
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardTreePage extends BasePage {
    @Inject
    private RewardService rewardService;

    public RewardTreePage() {
        WorkerRewardTree tree = rewardService.calcRewards();

        StringBuilder rewards = new StringBuilder();

        for (long l = 1; l <= tree.getTreeDepth(); ++l){
            List<WorkerReward> list = tree.getWorkerRewards(l);

            rewards.append("Level: ").append(l).append("\n");

            for (WorkerReward r : list){
                if ((r.getSaleVolume() != null && r.getSaleVolume().compareTo(BigDecimal.ZERO) > 0) ||
                        r.getGroupSaleVolume() != null && r.getGroupSaleVolume().compareTo(BigDecimal.ZERO) > 0 ||
                        r.getRegistrationCount() > 0 ||
                        r.getFirstLevelCount() > 0) {
                    WorkerNode n = r.getWorkerNode();

                    rewards.append("\n")
                            .append("objectId: ").append(n.getObjectId())
                            .append(", managerId: ").append(n.getManagerId())
                            .append(", saleVolume: ").append(r.getSaleVolume())
                            .append(", groupSaleVolume: ").append(r.getGroupSaleVolume())
                            .append(", registrationCount: ").append(r.getRegistrationCount())
                            .append(", firstLevelCount: ").append(r.getFirstLevelCount());
                }
            }

            rewards.append("\n\n\n");
        }

        add(new Label("rewards", rewards.toString()));
    }


}
