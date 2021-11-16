package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.CompensationService;
import ru.complitex.jedani.worker.service.RewardService;
import ru.complitex.jedani.worker.service.RewardService2;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardTestPage extends BasePage {
    @Inject
    private RewardService rewardService;

    @Inject
    private RewardService2 rewardService2;

    @Inject
    private CompensationService compensationService;

    public RewardTestPage() {
        long rewardServiceTime = System.currentTimeMillis();

        try {
            rewardService.testRewards();
        } catch (RewardException e) {
            throw new RuntimeException(e);
        }

        rewardServiceTime = System.currentTimeMillis() - rewardServiceTime;

        long compensationServiceTime = System.currentTimeMillis();

        compensationService.testRewards();

        compensationServiceTime = System.currentTimeMillis() - compensationServiceTime;

        long rewardServiceTime2 = System.currentTimeMillis();

        try {
            rewardService2.testRewards();
        } catch (RewardException e) {
            throw new RuntimeException(e);
        }

        rewardServiceTime2 = System.currentTimeMillis() - rewardServiceTime2;

        BigDecimal rewardServiceSum = rewardService.getRewards().stream()
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal compensationServiceSum = compensationService.getRewards().stream()
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rewardServiceSum2 = rewardService2.getRewards().stream()
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        add(new Label("test", rewardService.getRewards().size() + " " + rewardServiceSum.toPlainString() + " " + rewardServiceTime + " = " +
                compensationService.getRewards().size() + " " + compensationServiceSum.toPlainString() + " " + compensationServiceTime + " = " +
                rewardService2.getRewards().size() + " " + rewardServiceSum2.toPlainString() + " " + rewardServiceTime2));

        add(new Label("r", getRewards(rewardService.getRewards().stream()
                .sorted(Comparator.comparing(Reward::getWorkerId)).collect(Collectors.toList())))
                .setEscapeModelStrings(false));
        add(new Label("c", getRewards(compensationService.getRewards().stream()
                .sorted(Comparator.comparing(reward -> Objects.requireNonNullElse(reward.getWorkerId(), -1L))).collect(Collectors.toList())))
                .setEscapeModelStrings(false));
        add(new Label("r2", getRewards(rewardService2.getRewards().stream()
                .sorted(Comparator.comparing(Reward::getWorkerId)).collect(Collectors.toList())))
                .setEscapeModelStrings(false));

        add(new Label("rewards", "" +
                getRewardsDiff("-c", rewardService.getRewards(), compensationService.getRewards()) + "<br/>" +
                getRewardsDiff("+c", compensationService.getRewards(), rewardService.getRewards()) + "<br/>" +
                getRewardsDiff("-r2", rewardService.getRewards(), rewardService2.getRewards()) + "<br/>" +
                getRewardsDiff("+r2", compensationService.getRewards(), rewardService2.getRewards()))
                .setEscapeModelStrings(false));

        add(new Label("compensations", "" +
                getRewardsDiff("-r", compensationService.getRewards(), rewardService.getRewards()) + "<br/>" +
                getRewardsDiff("+r", rewardService.getRewards(), compensationService.getRewards()) + "<br/>" +
                getRewardsDiff("-r2", compensationService.getRewards(), rewardService2.getRewards()) + "<br/>" +
                getRewardsDiff("+r2", rewardService2.getRewards(), compensationService.getRewards()))
                .setEscapeModelStrings(false));

        add(new Label("rewards2", "" +
                getRewardsDiff("-r", rewardService2.getRewards(), rewardService.getRewards()) + "<br/>" +
                getRewardsDiff("+r", rewardService.getRewards(), rewardService2.getRewards()) + "<br/>" +
                getRewardsDiff("-c", rewardService2.getRewards(), compensationService.getRewards()) + "<br/>" +
                getRewardsDiff("+c", compensationService.getRewards(), rewardService2.getRewards()))
                .setEscapeModelStrings(false));
    }

    private String getRewards(List<Reward> list) {
        StringBuilder rewards = new StringBuilder();

        list.forEach(reward -> rewards
                .append("workerId: ").append(reward.getWorkerId())
                .append(", point: ").append(reward.getPoint())
                .append(", type: ").append(reward.getType())
                .append(", rank: ").append(reward.getRank())
                .append(", saleId: ").append(reward.getSaleId())
                .append(", status: ").append(reward.getRewardStatus())
                .append(", periodId: ").append(reward.getPeriodId())
                .append("</br>"));

        return rewards.toString();
    }

    private String getRewardsDiff(String prefix, List<Reward> list1, List<Reward> list2) {
        StringBuilder rewards = new StringBuilder();

        list1.forEach(reward -> {
            if (list2.stream()
                    .noneMatch(r -> Objects.equals(reward.getWorkerId(), r.getWorkerId()) &&
                            Objects.equals(reward.getPoint(), r.getPoint()) &&
                            Objects.equals(reward.getType(), r.getType()) &&
                            Objects.equals(reward.getSaleId(), r.getSaleId()) &&
                            Objects.equals(reward.getRewardStatus(), r.getRewardStatus()) &&
                            Objects.equals(reward.getPeriodId(), r.getPeriodId()))) {
                rewards.append(prefix).append(" ")
                        .append("workerId: ").append(reward.getWorkerId())
                        .append(", point: ").append(reward.getPoint())
                        .append(", type: ").append(reward.getType())
                        .append(", rank: ").append(reward.getRank())
                        .append(", saleId: ").append(reward.getSaleId())
                        .append(", status: ").append(reward.getRewardStatus())
                        .append(", periodId: ").append(reward.getPeriodId())
                        .append("</br>");
            }
        });
        return rewards.toString();
    }


}


