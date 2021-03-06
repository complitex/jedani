package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.entity.WorkerNode;
import ru.complitex.jedani.worker.entity.WorkerReward;
import ru.complitex.jedani.worker.entity.WorkerRewardTree;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 23.10.2019 11:15 PM
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardTreePage extends BasePage {
    @Inject
    private RewardService rewardService;

    @Inject
    public PeriodMapper periodMapper;

    public RewardTreePage() {
        WorkerRewardTree tree = rewardService.getWorkerRewardTree(periodMapper.getActualPeriod());

        StringBuilder rewards = new StringBuilder();

        for (long l = 1; l <= tree.getTreeDepth(); ++l){
            List<WorkerReward> list = tree.getWorkerRewards(l);

            rewards.append("Level: ").append(l).append("\n");

            for (WorkerReward wr : list){
                if ((wr.getSaleVolume().compareTo(ZERO) > 0) ||
                        wr.getStructureSaleVolume().compareTo(ZERO) > 0 ||
                        wr.getPaymentVolume().compareTo(ZERO) > 0 ||
                        wr.getGroupRegistrationCount() > 0 ||
                        wr.getFirstLevelCount() > 0) {
                    WorkerNode n = wr.getWorkerNode();

                    rewards.append("\n")
                            .append("objectId: ").append(n.getObjectId())
                            .append(", managerId: ").append(n.getManagerId());

                    if (wr.getSaleVolume().compareTo(ZERO) > 0){
                        rewards.append(", saleVolume: ").append(wr.getSaleVolume());
                    }
                    if (wr.getStructureSaleVolume().compareTo(ZERO) > 0){
                        rewards.append(", groupSaleVolume: ").append(wr.getStructureSaleVolume());
                    }
                    if (wr.getPaymentVolume().compareTo(ZERO) > 0){
                        rewards.append(", paymentVolume: ").append(wr.getPaymentVolume());
                    }
                    if (wr.getGroupRegistrationCount() > 0){
                        rewards.append(", registrationCount: ").append(wr.getGroupRegistrationCount());
                    }
                    if (wr.getFirstLevelCount() > 0){
                        rewards.append(", firstLevelCount: ").append(wr.getFirstLevelCount());
                    }

                    wr.getRewards().forEach(r -> rewards.append("\n\t")
                            .append("saleId: ").append(r.getSaleId())
                            .append(", rewordPoint: ").append(r.getPoint())
                            .append(", rewordType: ").append(r.getType()));
                }
            }

            rewards.append("\n\n\n");
        }

        add(new Label("rewards", rewards.toString()));
    }


}
