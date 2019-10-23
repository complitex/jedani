package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.entity.WorkerNode;
import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 23.10.2019 11:15 PM
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardCalcPage extends BasePage {
    @Inject
    private RewardService rewardService;

    public RewardCalcPage() {
        Map<Long, List<WorkerReward>> map = rewardService.calcRewards();

        StringBuilder rewards = new StringBuilder();

        Long treeDepth = rewardService.getTreeDepth(map);

        for (long l = 1; l <= treeDepth; ++l){
            List<WorkerReward> list = map.get(l);

            rewards.append("Level: ").append(l).append("\n");

            for (WorkerReward r : list){
                if ((r.getSaleVolume() != null && r.getSaleVolume().compareTo(BigDecimal.ZERO) > 0) || r.getGroupSaleVolume() != null) {
                    WorkerNode n = r.getWorkerNode();

                    rewards.append("\n")
                            .append("objectId: ").append(n.getObjectId())
                            .append(", managerId: ").append(n.getManagerId())
                            .append(", saleVolume: ").append(r.getSaleVolume())
                            .append(", groupSaleVolume: ").append(r.getGroupSaleVolume());
                }
            }

            rewards.append("\n\n\n");
        }

        add(new Label("rewards", rewards.toString()));
    }


}
