package ru.complitex.jedani.worker.page.payment;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.AbstractDateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupStringField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.FormGroupWorker;
import ru.complitex.jedani.worker.entity.Payment;

import javax.inject.Inject;
import java.util.Date;

public class PaymentModal extends AbstractEditModal<Payment> {
    @Inject
    private DomainService domainService;

    public PaymentModal(String markupId) {
        super(markupId);

        setModel(Model.of(new Payment()));

        add(new FormGroupWorker("worker", getModel(), Payment.WORKER){
            @Override
            public boolean isVisible() {
                return isAdmin() || isStructureAdmin();
            }
        }.setRequired(true));

        add(new FormGroupDateTextField("date", getModel(), Payment.DATE).setRequired(true));

        DateTextFieldConfig monthDateTextFieldConfig = new DateTextFieldConfig()
                .withFormat("MM.yyyy")
                .withLanguage("ru")
                .autoClose(true)
                .highlightToday(true)
                .withMinViewMode(AbstractDateTextFieldConfig.View.Month)
                .withView(AbstractDateTextFieldConfig.View.Month);

        FormGroupDateTextField periodEnd;
        add(periodEnd = new FormGroupDateTextField("periodEnd", getModel(), Payment.PERIOD_END){
            @Override
            protected DateTextFieldConfig getDateTextFieldConfig() {
                return monthDateTextFieldConfig;
            }
        }.setRequired(true));

        add(new FormGroupDateTextField("periodStart", getModel(), Payment.PERIOD_START){
            @Override
            protected DateTextFieldConfig getDateTextFieldConfig() {
                return monthDateTextFieldConfig;
            }
        }.onUpdate(t -> {
            getModelObject().setPeriodEnd(getModelObject().getPeriodStart());

            t.add(periodEnd);
        }).setRequired(true));

        add(new FormGroupStringField("contract", getModel(), Payment.CONTRACT).setRequired(true));

        add(new FormGroupDecimalField("payment", getModel(), Payment.PAYMENT).setRequired(true));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Date date = Dates.currentDate();

        Payment payment = new Payment();

        payment.setDate(date);
        payment.setPeriodStart(date);
        payment.setPeriodEnd(date);

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

        Payment payment = getModelObject();

        payment.setPeriodEnd(Dates.lastDayOfMonth(payment.getPeriodEnd()));

        domainService.save(payment);

        success(getString("info_payment_saved"));
    }
}
