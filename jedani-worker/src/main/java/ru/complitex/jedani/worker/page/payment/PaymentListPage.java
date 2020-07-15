package ru.complitex.jedani.worker.page.payment;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.mapper.PaymentMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.*;

@AuthorizeInstantiation({AUTHORIZED})
public class PaymentListPage extends DomainListModalPage<Payment> {
    @Inject
    private DomainService domainService;

    @Inject
    private PaymentMapper paymentMapper;

    private PaymentModal paymentModal;

    public PaymentListPage() {
        super(Payment.class);

        Form paymentForm = new Form("paymentForm");
        getContainer().add(paymentForm);

        paymentForm.add(paymentModal = new PaymentModal("paymentModal").onUpdate(t -> t.add(getFeedback(), getTable())));
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Payment.DATE, Payment.CONTRACT, Payment.PERIOD_START, Payment.PERIOD_END,
                Payment.POINT);
    }

    @Override
    protected FilterWrapper<Payment> newFilterWrapper(Payment domainObject) {
        FilterWrapper<Payment> filterWrapper = super.newFilterWrapper(domainObject);

        if (!isAdmin() && !isStructureAdmin() && !isPaymentAdmin()){
            filterWrapper.put(Payment.FILTER_SELLER_WORKER_ID, getCurrentWorker().getObjectId());
        }

        return filterWrapper;
    }

    @Override
    protected List<Payment> getDomains(FilterWrapper<Payment> filterWrapper) {
        return paymentMapper.getPayments(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<Payment> filterWrapper) {
        return paymentMapper.getPaymentsCount(filterWrapper);
    }

    @Override
    protected AbstractDomainColumn<Payment> newDomainColumn(EntityAttribute a) {
       if (a.getEntityAttributeId().equals(Payment.SALE)){
            return new AbstractDomainColumn<Payment>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Payment>> cellItem, String componentId, IModel<Payment> rowModel) {
                    Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getSaleId());

                    cellItem.add(new Label(componentId, sale != null ? sale.getContract() : ""));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected boolean isCreateEnabled() {
        return isAdmin() || isStructureAdmin() || isPaymentAdmin();
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        paymentModal.create(target);
    }

    @Override
    protected void onEdit(Payment payment, AjaxRequestTarget target) {
        paymentModal.edit(payment, target);
    }

    @Override
    protected boolean isDomainModalEditEnabled() {
        return false;
    }
}
