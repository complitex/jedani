package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardStatus;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    @Inject
    private WorkerService workerService;

    @Inject
    private SaleService saleService;

    @Inject
    private PeriodMapper periodMapper;

    public RewardTestPage() {
        Period period = periodMapper.getPeriod(17L);

        long rewardServiceTime = System.currentTimeMillis();

        try {
            rewardService.testRewards(period);
        } catch (RewardException e) {
            throw new RuntimeException(e);
        }

        rewardServiceTime = System.currentTimeMillis() - rewardServiceTime;

        long compensationServiceTime = System.currentTimeMillis();

        compensationService.testRewards(period);

        compensationServiceTime = System.currentTimeMillis() - compensationServiceTime;

        long rewardServiceTime2 = System.currentTimeMillis();

        try {
            rewardService2.testRewards(period);
        } catch (RewardException e) {
            throw new RuntimeException(e);
        }

        rewardServiceTime2 = System.currentTimeMillis() - rewardServiceTime2;

        List<Reward> rewards = rewardService.getRewards();
        List<Reward> compensations = compensationService.getRewards();
        List<Reward> rewards2 = rewardService2.getRewards();

        add(new Label("test", getSize(rewards)+ ", " + getSum(rewards) + ", " + rewardServiceTime + " = " +
                getSize(compensations) + ", " + getSum(compensations) + ", " + compensationServiceTime + " = " +
                getSize(rewards2) + ", " + getSum(rewards2) + ", " + rewardServiceTime2));

        rewardService.getRewards().sort(Comparator.comparing(reward ->  Objects.requireNonNullElse(reward.getSaleId(), -1L)));
        compensationService.getRewards().sort(Comparator.comparing(reward ->  Objects.requireNonNullElse(reward.getSaleId(), -1L)));
        rewardService2.getRewards().sort(Comparator.comparing(reward ->  Objects.requireNonNullElse(reward.getSaleId(), -1L)));

        add(new Label("r", getRewards(rewardService.getRewards())).setEscapeModelStrings(false));
        add(new Label("c", getRewards(compensationService.getRewards())).setEscapeModelStrings(false));
        add(new Label("r2", getRewards(rewardService2.getRewards())).setEscapeModelStrings(false));

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

        list.forEach(reward -> append("", rewards, reward));

        return rewards.toString();
    }

    private String getRewardsDiff(String prefix, List<Reward> list1, List<Reward> list2) {
        StringBuilder rewards = new StringBuilder();

        list1.forEach(reward -> {
            if (list2.stream()
                    .noneMatch(r -> Objects.equals(reward.getWorkerId(), r.getWorkerId()) &&
                            Objects.equals(reward.getType(), r.getType()) &&
                            Objects.equals(reward.getSaleId(), r.getSaleId()) &&
                            Objects.equals(reward.getRewardStatus(), r.getRewardStatus()) &&
                            Objects.equals(reward.getPeriodId(), r.getPeriodId()))) {
                append(prefix, rewards, reward);
            }

            Reward r1 = list2.stream()
                    .filter(r -> Objects.equals(reward.getWorkerId(), r.getWorkerId()) &&
                            !Objects.equals(reward.getPoint(), r.getPoint()) &&
                            Objects.equals(reward.getType(), r.getType()) &&
                            Objects.equals(reward.getSaleId(), r.getSaleId()) &&
                            Objects.equals(reward.getRewardStatus(), r.getRewardStatus()) &&
                            Objects.equals(reward.getPeriodId(), r.getPeriodId()))
                    .findAny()
                    .orElse(null);

            if (r1 != null) {
                append(prefix + "+-", rewards, reward);
                append(prefix + "-+", rewards, r1);
            }
        });

        return rewards.toString();
    }

    private void append(String prefix, StringBuilder rewards, Reward reward) {
        rewards.append(prefix).append(" ")
                .append("sId: ").append(reward.getSaleId())
                .append(", wId: ").append(reward.getWorkerId())
                .append(", p: ").append(reward.getPoint())
                .append(", t: ").append(reward.getType())
                .append(", r: ").append(reward.getRank())
                .append(", s: ").append(reward.getRewardStatus())
                .append(reward.getSaleId() != null ? ", c: " + saleService.getContract(reward.getSaleId()) : "")
                .append(reward.getWorkerId() != null ? ", jId: " + workerService.getJId(reward.getWorkerId()) : "")
                .append(reward.getEstimatedId() != null ? ", eId: " + reward.getEstimatedId() : "")
                .append(", pId: ").append(reward.getPeriodId())
                .append("</br>");
    }

    private Stream<Reward> getEstimatedStream(List<Reward> rewards) {
        return rewards.stream().filter(reward -> reward.getRewardStatus().equals(RewardStatus.ESTIMATED));
    }

    private Stream<Reward> getChargedStream(List<Reward> rewards) {
        return rewards.stream().filter(reward -> reward.getRewardStatus().equals(RewardStatus.CHARGED));
    }

    private Stream<Reward> getWithdrawnStream(List<Reward> rewards) {
        return rewards.stream().filter(reward -> reward.getRewardStatus().equals(RewardStatus.WITHDRAWN));
    }

    private String getEstimatedSize(List<Reward> rewards) {
        return  getEstimatedStream(rewards).count() + "";
    }

    private String getChargedSize(List<Reward> rewards) {
        return getChargedStream(rewards).count() + "";
    }

    private String getWithdrawnSize(List<Reward> rewards) {
        return getWithdrawnStream(rewards).count() + "";
    }

    private String getSize(List<Reward> rewards) {
        return getEstimatedSize(rewards) + " / " + getChargedSize(rewards) + " / " + getWithdrawnSize(rewards);
    }

    private String getEstimatedSum(List<Reward> rewards) {
        return  getEstimatedStream(rewards)
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .toPlainString();
    }

    private String getChargedSum(List<Reward> rewards) {
        return getChargedStream(rewards)
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .toPlainString();
    }

    private String getWithdrawnSum(List<Reward> rewards) {
        return getWithdrawnStream(rewards)
                .map(Reward::getPoint)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .toPlainString();
    }

    private String getSum(List<Reward> rewards) {
        return getEstimatedSum(rewards) + " / " + getChargedSum(rewards) + " / " + getWithdrawnSum(rewards);
    }


}
