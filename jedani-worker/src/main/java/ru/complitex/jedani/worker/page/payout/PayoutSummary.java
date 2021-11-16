package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Account;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.entity.RewardStatus;
import ru.complitex.jedani.worker.mapper.PayoutMapper;
import ru.complitex.jedani.worker.service.AccountService;
import ru.complitex.jedani.worker.service.RewardService2;

import javax.inject.Inject;
import java.math.BigDecimal;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutSummary extends AbstractToolbar {
    @Inject
    private RewardService2 rewardService;

    @Inject
    private AccountService accountService;

    @Inject
    private DomainService domainService;

    @Inject
    private PayoutMapper payoutMapper;

    public PayoutSummary(Table<Account> table) {
        super(table);

        add(new Label("charged", LoadableDetachableModel.of(() -> {
            Account account = table.getFilterWrapper().getObject();

            String symbol = domainService.getText(Currency.ENTITY_NAME, account.getCurrencyId(), Currency.SYMBOL);

            if (symbol == null) {
                symbol = "";
            }

            BigDecimal charged = rewardService.getRewardsLocalByCurrency(account.getPeriodId(), RewardStatus.CHARGED, account.getCurrencyId());

            return accountService.getCharged(account.getPeriodId(), account.getCurrencyId()).add(charged).toPlainString() + symbol;
        })));

        add(new WebMarkupContainer("paid")
                .add(new Label("paid", LoadableDetachableModel.of(() -> {
                    Account account = table.getFilterWrapper().getObject();

                    BigDecimal paid = payoutMapper.getPayouts(FilterWrapper.of(new Payout()
                            .setPeriodId(account.getPeriodId())
                            .setCurrencyId(account.getCurrencyId())))
                            .stream()
                            .map(Payout::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    String symbol = domainService.getText(Currency.ENTITY_NAME, account.getCurrencyId(), Currency.SYMBOL);

                    if (symbol == null) {
                        symbol = "";
                    }

                    return accountService.getPaid(account.getPeriodId(), account.getCurrencyId()).add(paid).toPlainString() + symbol;
                })))
                .add(AttributeModifier.replace("colspan",
                        () -> String.valueOf(table.getColumns().size() - 1))));
    }
}
