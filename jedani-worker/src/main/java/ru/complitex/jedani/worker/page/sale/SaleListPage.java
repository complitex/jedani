package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;

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
                return  isUserInRole(JedaniRoles.ADMINISTRATORS) || isUserInRole(JedaniRoles.SALE_ADMINISTRATORS);
            }

            @Override
            protected boolean isCreateEnabled() {
                return isUserInRole(JedaniRoles.ADMINISTRATORS) || isUserInRole(JedaniRoles.SALE_ADMINISTRATORS);
            }

            @Override
            public boolean isViewOnly() {
                return !isUserInRole(JedaniRoles.ADMINISTRATORS) && !isUserInRole(JedaniRoles.SALE_ADMINISTRATORS);
            }

            @Override
            public boolean isRemoveEnabled() {
                return isUserInRole(JedaniRoles.ADMINISTRATORS) || isUserInRole(JedaniRoles.SALE_ADMINISTRATORS);
            }
        };

        salePanel.setSellerWorkerJidFilter(parameters.get("jid").toOptionalString());

        add(salePanel);
    }
}
