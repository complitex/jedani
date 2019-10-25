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

            for (WorkerReward wr : list){
                if ((wr.getSaleVolume() != null && wr.getSaleVolume().compareTo(BigDecimal.ZERO) > 0) ||
                        wr.getGroupSaleVolume() != null && wr.getGroupSaleVolume().compareTo(BigDecimal.ZERO) > 0 ||
                        wr.getRegistrationCount() > 0 ||
                        wr.getFirstLevelCount() > 0) {
                    WorkerNode n = wr.getWorkerNode();

                    rewards.append("\n")
                            .append("objectId: ").append(n.getObjectId())
                            .append(", managerId: ").append(n.getManagerId())
                            .append(", saleVolume: ").append(wr.getSaleVolume())
                            .append(", groupSaleVolume: ").append(wr.getGroupSaleVolume())
                            .append(", registrationCount: ").append(wr.getRegistrationCount())
                            .append(", firstLevelCount: ").append(wr.getFirstLevelCount());

                    wr.getRewards().forEach(r -> rewards.append("\n\t")
                            .append("rewordPoint: ").append(r.getPoint())
                            .append("rewordType: ").append(r.getType()));
                }
            }

            rewards.append("\n\n\n");
        }

        add(new Label("rewards", rewards.toString()));
    }


}
