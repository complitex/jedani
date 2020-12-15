package ru.complitex.jedani.worker.page.payment;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.jedani.worker.page.BasePage;

import static ru.complitex.jedani.worker.security.JedaniRoles.AUTHORIZED;

@AuthorizeInstantiation({AUTHORIZED})
public class PaymentListPage extends BasePage {
    public PaymentListPage() {
        PaymentPanel paymentPanel = new PaymentPanel("payment", getCurrentWorker());

        if (isAdmin() || isPaymentAdmin()){
            paymentPanel.setSellerWorkerIdFilter(null);
        }

        add(paymentPanel);
    }
}
