package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.jedani.worker.service.ValidationService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class RewardValidationPage extends BasePage {
    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private WorkerService workerService;

    @Inject
    private SaleService saleService;

    @Inject
    private ValidationService validationService;

    public RewardValidationPage() {
        long time = System.nanoTime();

        StringBuilder stringBuilder = new StringBuilder();

        validationService.validateRewardChargedSum()
                .forEach(reward -> {
                    stringBuilder
                            .append("rId: ").append(reward.getId())
                            .append(", sId: ").append(reward.getSaleId())
                            .append(", wId: ").append(reward.getWorkerId())
                            .append(", p: ").append(reward.getPoint())
                            .append(", t: ").append(reward.getType())
                            .append(", r: ").append(reward.getRank())
                            .append(", s: ").append(reward.getRewardStatus())
                            .append(", e: ").append(reward.getErrors())
                            .append(reward.getSaleId() != null ? ", c: " + saleService.getContract(reward.getSaleId()) : "")
                            .append(reward.getWorkerId() != null ? ", jId: " + workerService.getJId(reward.getWorkerId()) : "")
                            .append(reward.getEstimatedId() != null ? ", eId: " + reward.getEstimatedId() : "")
                            .append(", pId: ").append(reward.getPeriodId())
                            .append("</br>");
                });

        add(new Label("test", (System.nanoTime() - time)).setEscapeModelStrings(false));

        add(new Label("rewards", stringBuilder.toString()).setEscapeModelStrings(false));
    }
}
