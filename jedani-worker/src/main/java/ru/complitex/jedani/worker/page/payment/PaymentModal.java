package ru.complitex.jedani.worker.page.payment;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.FormGroupWorker;
import ru.complitex.jedani.worker.entity.Payment;

import javax.inject.Inject;

public class PaymentModal extends AbstractEditModal<Payment> {
    @Inject
    private DomainService domainService;

    public PaymentModal(String markupId) {
        super(markupId);

        size(Size.Large);

        setModel(Model.of(new Payment()));

        add(new FormGroupWorker("worker", getModel(), Payment.WORKER){
            @Override
            public boolean isVisible() {
                return isAdmin() || isStructureAdmin();
            }
        }.setRequired(true));

        add(new FormGroupDateTextField("date", getModel(), Payment.DATE).setRequired(true));

        add(new FormGroupTextField<>("sale", Model.of("")).setRequired(true));

        add(new FormGroupDecimalField("payment", getModel(), Payment.PAYMENT).setRequired(true));

        add(new FormGroupDecimalField("point", getModel(), Payment.POINT).setEnabled(false));

        add(new FormGroupDecimalField("rate", getModel(), Payment.RATE).setEnabled(false));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Payment payment = new Payment();
        payment.setDate(Dates.currentDate());
        payment.setWorkerId(getBasePage().getCurrentWorker().getObjectId());

        setModelObject(payment);
    }

    @Override
    public void edit(Payment payment, AjaxRequestTarget target) {
        super.edit(payment, target);

        setModelObject(payment);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        super.save(target);

        domainService.save(getModelObject());

        success(getString("info_payment_saved"));
    }
}
