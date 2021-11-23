package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardStatus;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.RewardMapper;
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
    
    @Inject
    private RewardMapper rewardMapper;

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
        List<Reward> db = rewardMapper.getRewards(FilterWrapper.of(new Reward().setPeriodId(period.getObjectId())));

        add(new Label("rz", getSize(rewards)));
        add(new Label("rs", getSum(rewards)));
        add(new Label("rt", rewardServiceTime));

        add(new Label("cz", getSize(compensations)));
        add(new Label("cs", getSum(compensations)));
        add(new Label("ct", compensationServiceTime));

        add(new Label("r2z", getSize(rewards2)));
        add(new Label("r2s", getSum(rewards2)));
        add(new Label("r2t", rewardServiceTime2));

        add(new Label("dz", getSize(db)));
        add(new Label("ds", getSum(db)));

        rewards.sort(Comparator.comparing(reward ->  Objects.requireNonNullElse(reward.getSaleId(), -1L)));
        compensations.sort(Comparator.comparing(reward ->  Objects.requireNonNullElse(reward.getSaleId(), -1L)));
        rewards2.sort(Comparator.comparing(reward ->  Objects.requireNonNullElse(reward.getSaleId(), -1L)));

        add(new Label("r", getRewards(rewards)).setEscapeModelStrings(false));
        add(new Label("c", getRewards(compensations)).setEscapeModelStrings(false));
        add(new Label("r2", getRewards(rewards2)).setEscapeModelStrings(false));
        add(new Label("d", getRewards(db)).setEscapeModelStrings(false));

        add(new Label("rewards", "" +
                getRewardsDiff("-c", rewards, compensations) + "<br/>" +
                getRewardsDiff("+c", compensations, rewards) + "<br/>" +
                getRewardsDiff("-r2", rewards, rewards2) + "<br/>" +
                getRewardsDiff("+r2", compensations, rewards2))
                .setEscapeModelStrings(false));

        add(new Label("compensations", "" +
                getRewardsDiff("-r", compensations, rewards) + "<br/>" +
                getRewardsDiff("+r", rewards, compensations) + "<br/>" +
                getRewardsDiff("-r2", compensations, rewards2) + "<br/>" +
                getRewardsDiff("+r2", rewards2, compensations))
                .setEscapeModelStrings(false));

        add(new Label("rewards2", "" +
                getRewardsDiff("-r", rewards2, rewards) + "<br/>" +
                getRewardsDiff("+r", rewards, rewards2) + "<br/>" +
                getRewardsDiff("-c", rewards2, compensations) + "<br/>" +
                getRewardsDiff("+c", compensations, rewards2))
                .setEscapeModelStrings(false));

        add(new Label("db", "" +
                getRewardsDiff("-r2", db, rewards2) + "<br/>" +
                getRewardsDiff("+r2", rewards2, db) + "<br/>" +
                getRewardsDiff("-c", db, compensations) + "<br/>" +
                getRewardsDiff("+c", compensations, db))
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
        return "e: " + getEstimatedSize(rewards) + ", c:" + getChargedSize(rewards) + ", w:" + getWithdrawnSize(rewards);
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
        return "e: " + getEstimatedSum(rewards) + ", c: " + getChargedSum(rewards) + ", w: " + getWithdrawnSum(rewards);
    }


}
