package ru.complitex.jedani.worker.page.payment;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.AbstractDateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.common.wicket.form.FormGroupStringField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.service.PriceService;
import ru.complitex.jedani.worker.service.SaleDecisionService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

public class PaymentModal extends AbstractEditModal<Payment> {
    @Inject
    private DomainService domainService;

    @Inject
    private PriceService priceService;

    @Inject
    private SaleDecisionService saleDecisionService;

    private boolean warnTotal = false;

    public PaymentModal(String markupId) {
        super(markupId);

        setModel(Model.of(new Payment()));

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
        Payment payment = getModelObject();

        payment.setPeriodEnd(Dates.lastDayOfMonth(payment.getPeriodEnd()));

        List<Sale> sales = domainService.getDomains(Sale.class, FilterWrapper.of(new Sale()
                .setContract(payment.getContract())).setFilter(FilterWrapper.FILTER_EQUAL));

        if (sales.isEmpty()){
            error(getString("error_sale_not_found"));

            target.add(getFeedback());

            return;
        }

        if (sales.size() > 1){
            error(getString("error_sale_more_than_one"));

            target.add(getFeedback());

            return;
        }

        Sale sale = sales.get(0);

        List<SaleItem> saleItems = domainService.getDomains(SaleItem.class, FilterWrapper.of(new SaleItem()
                .setParentId(sale.getObjectId())));

        if (saleItems.isEmpty()){
            error(getString("error_sale_item_not_found"));

            target.add(getFeedback());

            return;
        }

        SaleItem saleItem = saleItems.get(0);

        BigDecimal rate = priceService.getRate(sale.getStorageId(), saleItem.getNomenclatureId(),
                saleDecisionService.getSaleDecision(saleItem.getSaleDecisionId()),
                payment.getDate(), sale.getTotal(), sale.getInstallmentMonths());

        if (rate == null){
            error(getString("error_null_rate"));

            target.add(getFeedback());

            return;
        }

        payment.setRate(rate);
        payment.setPoint(payment.getPayment().divide(rate, 2, RoundingMode.HALF_EVEN));
        payment.setSaleId(sale.getObjectId());

        BigDecimal sum = domainService.getDomains(Payment.class, FilterWrapper.of(new Payment()
                .setContract(payment.getContract()))).stream()
                .filter(p -> !p.getObjectId().equals(payment.getObjectId()) && p.getPoint() != null)
                .map(Payment::getPoint).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sale.getTotal() != null && sale.getTotal().compareTo(sum.add(payment.getPoint())) <= 0 && !warnTotal){
            warn(getString("error_payment_more_than_sale_total"));
            target.add(getFeedback());

            warnTotal = true;

            return;
        }else {
            warnTotal = false;
        }

        domainService.save(payment);

        success(getString("info_payment_saved"));

        getContainer().visitChildren(FormComponent.class, (c, v) -> ((FormComponent)c).clearInput());

        if (getOnUpdate() != null) {
            getOnUpdate().accept(target);
        }
    }
}
