package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.RewardStatus;
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

    public PayoutSummary(DataTable<?, ?> table, IModel<Long> periodModel, IModel<Long> currencyModel) {
        super(table);

        add(new Label("charged", LoadableDetachableModel.of(() -> {
            String symbol = domainService.getText(Currency.ENTITY_NAME, currencyModel.getObject(), Currency.SYMBOL);

            if (symbol == null) {
                symbol = "";
            }

            BigDecimal charged = rewardService.getRewardsLocalByCurrency(periodModel.getObject(), RewardStatus.CHARGED, currencyModel.getObject());

            return accountService.getCharged(periodModel.getObject(), currencyModel.getObject()).add(charged).toPlainString() + symbol;
        })));

        add(new WebMarkupContainer("paid")
                .add(new Label("paid", LoadableDetachableModel.of(() -> {
                    String symbol = domainService.getText(Currency.ENTITY_NAME, currencyModel.getObject(), Currency.SYMBOL);

                    if (symbol == null) {
                        symbol = "";
                    }

                    BigDecimal paid = rewardService.getRewardsLocalByCurrency(periodModel.getObject(), RewardStatus.PAID, currencyModel.getObject());

                    return accountService.getPaid(periodModel.getObject(), currencyModel.getObject()).add(paid).toPlainString() + symbol;
                })))
                .add(AttributeModifier.replace("colspan",
                        () -> String.valueOf(table.getColumns().size() - 1))));
    }
}
