package ru.complitex.jedani.worker.page.payment;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.AbstractDateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.*;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.service.PriceService;
import ru.complitex.jedani.worker.service.SaleDecisionService;
import ru.complitex.jedani.worker.service.SaleService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PaymentModal extends AbstractEditModal<Payment> {
    @Inject
    private DomainService domainService;

    @Inject
    private PriceService priceService;

    @Inject
    private SaleDecisionService saleDecisionService;

    @Inject
    private SaleService saleService;

    private boolean warnTotal = false;

    public PaymentModal(String markupId) {
        super(markupId);

        size(Size.Large);

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

        add(new FormGroupSelectPanel("type", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(getModel(), Payment.TYPE),
                Arrays.asList(PaymentType.POINT, PaymentType.LOCAL),
                new LongChoiceRenderer() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        switch (object.intValue()){
                            case (int) PaymentType.POINT:
                                return getString("point");
                            case (int) PaymentType.LOCAL:
                                return getString("local");
                            default:
                                return null;
                        }
                    }
                }).with(new BootstrapSelectConfig().withNoneSelectedText(""))
                .setNullValid(false)
                .setRequired(true)
                .add(OnChangeAjaxBehavior.onChange(t -> {
                    t.add(getContainer().get("paymentPoint"), getContainer().get("paymentLocal"));
                }))));

        add(new FormGroupDecimalField("paymentLocal", getModel(), Payment.LOCAL){
            @Override
            public boolean isVisible() {
                return Objects.equals(PaymentType.LOCAL, getModel().getObject().getType());
            }
        }.setRequired(true));
        add(new FormGroupDecimalField("paymentPoint", getModel(), Payment.POINT){
            @Override
            public boolean isVisible() {
                return Objects.equals(PaymentType.POINT, getModel().getObject().getType());
            }
        }.setRequired(true));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Date date = Dates.currentDate();

        Payment payment = new Payment();

        payment.setDate(date);
        payment.setPeriodStart(date);
        payment.setPeriodEnd(date);
        payment.setType(PaymentType.LOCAL);

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

        BigDecimal rate = priceService.getRate(sale.getStorageId(),
                saleDecisionService.getSaleDecision(saleItem.getSaleDecisionId()),
                payment.getDate(), sale.getTotal(), sale.getInstallmentMonths(), sale.isForYourself(),
                saleItem.getQuantity(), saleService.getPaymentPercent(sale).longValue());

        if (rate == null){
            error(getString("error_null_rate"));

            target.add(getFeedback());

            return;
        }

        payment.setRate(rate);

        if (payment.getType().equals(PaymentType.LOCAL)){
            payment.setPoint(payment.getLocal().divide(rate, 2, RoundingMode.HALF_EVEN));
        }else if (payment.getType().equals(PaymentType.POINT)){
            payment.setLocal(payment.getPoint().multiply(rate).setScale(2, RoundingMode.HALF_EVEN));
        }

        payment.setSaleId(sale.getObjectId());

        BigDecimal paymentTotal = domainService.getDomains(Payment.class, FilterWrapper.of(new Payment()
                .setContract(payment.getContract()))).stream()
                .filter(p -> !p.getObjectId().equals(payment.getObjectId()) && p.getPoint() != null)
                .map(Payment::getPoint).reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(payment.getPoint());

        if (sale.getTotal() != null && sale.getTotal().compareTo(paymentTotal) < 0 && !warnTotal){
            warn(getString("error_payment_more_than_sale_total"));
            target.add(getFeedback());

            warnTotal = true;

            return;
        }else {
            warnTotal = false;
        }

        domainService.save(payment);

        saleService.updateSale(sale, payment, paymentTotal);

        success(getString("info_payment_saved"));

        super.save(target);
    }
}
