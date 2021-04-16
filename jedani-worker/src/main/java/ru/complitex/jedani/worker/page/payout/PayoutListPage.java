package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class PayoutListPage extends BasePage {
    @Inject
    private DomainService domainService;

    public PayoutListPage() {
        RepeatingView payouts = new RepeatingView("payouts");

        List<Currency> currencies = domainService.getDomains(Currency.class, FilterWrapper.of(new Currency()));

        currencies.forEach(currency -> {
            Fragment fragment = new Fragment(payouts.newChildId(), "payout", PayoutListPage.this);

            payouts.add(fragment);

            fragment.add(new PayoutPanel("payout") {
                @Override
                protected Currency getCurrency() {
                    return currency;
                }

                @Override
                protected IModel<String> getCaptionModel() {
                    return Model.of(Attributes.capitalize(currency.getName()));
                }

                @Override
                protected AbstractToolbar newFooter(Table<Payout> table) {
                    return new PayoutSummary(table,
                            LoadableDetachableModel.of(() -> getFilterWrapper().getObject().getPeriodId()),
                            LoadableDetachableModel.of(() -> getFilterWrapper().getObject().getCurrencyId()));
                }
            });
        });

        add(payouts);
    }
}
