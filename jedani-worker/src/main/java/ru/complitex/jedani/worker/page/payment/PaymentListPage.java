package ru.complitex.jedani.worker.page.payment;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import static ru.complitex.jedani.worker.security.JedaniRoles.AUTHORIZED;

/**
 * @author Anatoly Ivanov
 * 14.09.2019 0:14
 */
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

            @Override
            protected Component getPagingLeft(String id) {
                return null;
            }
        };

        if (isAdmin() || isPaymentAdmin()){
            paymentPanel.setSellerWorkerIdFilter(null);
            paymentPanel.getFilterWrapper().put(Payment.FILTER_PERIOD, null);
        }

        add(paymentPanel);
    }
}
