package ru.complitex.jedani.worker.page.payment;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.service.SaleService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @author Anatoly Ivanov
 * 23.11.2019 22:21
 */
public class PaymentRemoveModal extends Modal<Sale> {
    @Inject
    private DomainService domainService;

    @Inject
    private SaleService saleService;

    private IModel<Payment> paymentModel;

    private Component paymentLabel;

    public PaymentRemoveModal(String markupId) {
        super(markupId);

        paymentModel = Model.of(new Payment());

        setBackdrop(Backdrop.FALSE);

        header(new ResourceModel("header"));

        add(paymentLabel = new Label("payment", new LoadableDetachableModel<String>() {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            protected String load() {
                Payment payment = paymentModel.getObject();

                if (payment.getObjectId() != null) {
                    return dateFormat.format(payment.getDate()) + " " + payment.getContract() + " " +
                            payment.getPoint().toPlainString();
                }

                return "";
            }
        }).setOutputMarkupId(true));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                Payment payment = paymentModel.getObject();

                domainService.delete(paymentModel.getObject());

                BigDecimal paymentTotalLocal = domainService.getDomains(Payment.class, FilterWrapper.of(new Payment()
                        .setContract(payment.getContract()))).stream()
                        .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

                Sale sale = saleService.getSale(payment.getSaleId());

                saleService.updateSale(sale, paymentTotalLocal);

                getSession().success(getString("info_deleted"));

                appendCloseDialogJavaScript(target);

                PaymentRemoveModal.this.onUpdate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                getSession().error(getString("error_deleted"));

                PaymentRemoveModal.this.onUpdate(target);

                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("delete")).add(new AjaxIndicatorAppender()));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void delete(AjaxRequestTarget target, Payment payment){
        paymentModel.setObject(payment);

        target.add(paymentLabel);

        appendShowDialogJavaScript(target);
    }

    protected void onUpdate(AjaxRequestTarget target){}
}
