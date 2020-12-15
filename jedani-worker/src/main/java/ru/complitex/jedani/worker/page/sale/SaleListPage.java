package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.jedani.worker.page.BasePage;

import static ru.complitex.jedani.worker.security.JedaniRoles.AUTHORIZED;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:22
 */
@AuthorizeInstantiation({AUTHORIZED})
public class SaleListPage extends BasePage {
    public SaleListPage(PageParameters parameters) {
        SalePanel salePanel = new SalePanel("sale", getCurrentWorker()){
            @Override
            protected boolean isActualFilter() {
                return false;
            }

            @Override
            protected boolean isCurrentWorkerFilter() {
                return false;
            }
        };

        salePanel.setSellerWorkerJidFilter(parameters.get("jid").toOptionalString());

        add(salePanel);
    }
}
