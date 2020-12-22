package ru.complitex.jedani.worker.page.payment;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import static ru.complitex.jedani.worker.security.JedaniRoles.AUTHORIZED;

@AuthorizeInstantiation({AUTHORIZED})
public class PaymentListPage extends BasePage {
    public PaymentListPage() {
        PaymentPanel paymentPanel = new PaymentPanel("payment", getCurrentWorker()){
            @Override
            protected boolean isCreateEnabled() {
                return isUserInRole(JedaniRoles.ADMINISTRATORS) || isUserInRole(JedaniRoles.PAYMENT_ADMINISTRATORS);
            }

            @Override
            public boolean isEditEnabled() {
                return isUserInRole(JedaniRoles.ADMINISTRATORS) || isUserInRole(JedaniRoles.PAYMENT_ADMINISTRATORS);
            }

            @Override
            protected boolean isRemoveEnabled() {
                return isUserInRole(JedaniRoles.ADMINISTRATORS) || isUserInRole(JedaniRoles.PAYMENT_ADMINISTRATORS);
            }
        };

        if (isAdmin() || isPaymentAdmin()){
            paymentPanel.setSellerWorkerIdFilter(null);
        }

        add(paymentPanel);
    }
}
