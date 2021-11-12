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
    }
}
