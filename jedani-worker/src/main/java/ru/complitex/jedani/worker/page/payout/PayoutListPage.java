package ru.complitex.jedani.worker.page.payout;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
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
import java.util.stream.Collectors;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class PayoutListPage extends BasePage {
    @Inject
    private DomainService domainService;

    public PayoutListPage() {
        List<ITab> payouts = domainService.getDomains(Currency.class, FilterWrapper.of(new Currency())).stream()
                .map(currency -> new AbstractTab(Model.of(Attributes.capitalize(currency.getName()))) {
                    @Override
                    public WebMarkupContainer getPanel(String panelId) {
                        Fragment fragment = new Fragment(panelId, "payout", PayoutListPage.this);
                        add(fragment);

                        fragment.add(new PayoutPanel("payout") {
                            @Override
                            protected Currency getCurrency() {
                                return currency;
                            }

                            @Override
                            protected AbstractToolbar newFooter(Table<Payout> table) {
                                return new PayoutSummary(table);
                            }
                        });

                        return fragment;
                    }
                })
                .collect(Collectors.toList());

        add(new AjaxBootstrapTabbedPanel<>("payouts", payouts));
    }
}
