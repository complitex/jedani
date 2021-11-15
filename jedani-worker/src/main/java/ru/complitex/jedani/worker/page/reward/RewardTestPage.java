package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.CompensationService;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Objects;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardTestPage extends BasePage {
    @Inject
    private RewardService rewardService;

    @Inject
    private CompensationService compensationService;

    public RewardTestPage() {
        rewardService.setTest(true);

        long rewardServiceTime = System.currentTimeMillis();

        try {
            rewardService.calculateRewards();
        } catch (RewardException e) {
            throw new RuntimeException();
        }

        rewardServiceTime = System.currentTimeMillis() - rewardServiceTime;

        long compensationServiceTime = System.currentTimeMillis();

        compensationService.testRewards();

        compensationServiceTime = System.currentTimeMillis() - compensationServiceTime;

        BigDecimal rewardServiceSum = rewardService.getRewards().stream()
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal compensationServiceSum = compensationService.getRewards().stream()
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        add(new Label("test", rewardService.getRewards().size() + " " + rewardServiceSum.toPlainString() + " " + rewardServiceTime + " = " +
                compensationService.getRewards().size() + " " + compensationServiceSum.toPlainString() + " " + compensationServiceTime));

        StringBuilder rewards = new StringBuilder();

        rewardService.getRewards().forEach(reward -> {
            if (compensationService.getRewards().stream()
                    .noneMatch(r -> Objects.equals(reward.getWorkerId(), r.getWorkerId()) &&
                            Objects.equals(reward.getPoint(), r.getPoint()) &&
                            Objects.equals(reward.getType(), r.getType()) &&
                            Objects.equals(reward.getSaleId(), r.getSaleId()) &&
                            Objects.equals(reward.getRewardStatus(), r.getRewardStatus()) &&
                            Objects.equals(reward.getPeriodId(), r.getPeriodId()))) {
                rewards.append("workerId: ").append(reward.getWorkerId())
                        .append(", point: ").append(reward.getPoint())
                        .append(", type: ").append(reward.getType())
                        .append(", rank: ").append(reward.getRank())
                        .append(", saleId: ").append(reward.getSaleId())
                        .append(", status: ").append(reward.getRewardStatus())
                        .append(", periodId: ").append(reward.getPeriodId())
                        .append("</br>");
            }
        });

        add(new Label("rewards", rewards.toString()).setEscapeModelStrings(false));

        StringBuilder compensations = new StringBuilder();

        compensationService.getRewards().forEach(reward -> {
            if (rewardService.getRewards().stream()
                    .noneMatch(r -> Objects.equals(reward.getWorkerId(), r.getWorkerId()) &&
                            Objects.equals(reward.getPoint(), r.getPoint()) &&
                            Objects.equals(reward.getType(), r.getType()) &&
                            Objects.equals(reward.getSaleId(), r.getSaleId()) &&
                            Objects.equals(reward.getRewardStatus(), r.getRewardStatus()) &&
                            Objects.equals(reward.getPeriodId(), r.getPeriodId()))) {
                compensations.append("workerId: ").append(reward.getWorkerId())
                        .append(",  point: ").append(reward.getPoint())
                        .append(", type: ").append(reward.getType())
                        .append(", rank: ").append(reward.getRank())
                        .append(", saleId: ").append(reward.getSaleId())
                        .append(", status: ").append(reward.getRewardStatus())
                        .append(", periodId: ").append(reward.getPeriodId())
                        .append("</br>");
            }
        });

        add(new Label("compensations", compensations.toString()).setEscapeModelStrings(false));
    }
}

