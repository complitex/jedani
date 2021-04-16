package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.entity.RewardStatus;
import ru.complitex.jedani.worker.mapper.PayoutMapper;
import ru.complitex.jedani.worker.service.AccountService;
import ru.complitex.jedani.worker.service.RewardService;

import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutSummary extends AbstractToolbar {
    @Inject
    private RewardService rewardService;

    @Inject
    private AccountService accountService;

    @Inject
    private DomainService domainService;

    @Inject
    private PayoutMapper payoutMapper;

    public PayoutSummary(Table<Payout> table) {
        super(table);

        add(new Label("charged", LoadableDetachableModel.of(() -> {
            Payout payout = table.getFilterWrapper().getObject();

            String symbol = domainService.getText(Currency.ENTITY_NAME, payout.getCurrencyId(), Currency.SYMBOL);

            if (symbol == null) {
                symbol = "";
            }

            BigDecimal charged = rewardService.getRewardsLocalByCurrency(payout.getPeriodId(), RewardStatus.CHARGED, payout.getCurrencyId());

            return accountService.getCharged(payout.getPeriodId(), payout.getCurrencyId()).add(charged).toPlainString() + symbol;
        })));

        add(new WebMarkupContainer("paid")
                .add(new Label("paid", LoadableDetachableModel.of(() -> {
                    Payout payout = table.getFilterWrapper().getObject();

                    String symbol = domainService.getText(Currency.ENTITY_NAME, payout.getCurrencyId(), Currency.SYMBOL);

                    if (symbol == null) {
                        symbol = "";
                    }

                    BigDecimal paid = payoutMapper.getPayouts(FilterWrapper.of(payout)).stream()
                            .map(Payout::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return accountService.getPaid(payout.getPeriodId(), payout.getCurrencyId()).add(paid).toPlainString() + symbol;
                })))
                .add(AttributeModifier.replace("colspan",
                        () -> String.valueOf(table.getColumns().size() - 1))));
    }
}
