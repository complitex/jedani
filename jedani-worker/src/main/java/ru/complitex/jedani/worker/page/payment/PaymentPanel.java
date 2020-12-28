package ru.complitex.jedani.worker.page.payment;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.PaymentMapper;
import ru.complitex.jedani.worker.service.PeriodService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 14.12.2020 16:55
 */
public class PaymentPanel extends DomainListModalPanel<Payment> {
    @Inject
    private DomainService domainService;

    @Inject
    private PaymentMapper paymentMapper;

    @Inject
    private PeriodService periodService;

    private final PaymentModal paymentModal;

    private final PaymentRemoveModal paymentRemoveModal;

    public PaymentPanel(String id, Worker worker) {
        super(id, Payment.class);

        setSellerWorkerIdFilter(worker.getObjectId());

        getFilterWrapper().put(Payment.FILTER_MONTH, periodService.getActualPeriod());

        Form<?> paymentForm = new Form<>("paymentForm");
        getContainer().add(paymentForm);

        paymentForm.add(paymentModal = new PaymentModal("paymentModal").onUpdate(t -> t.add(getFeedback(), getTable())));

        Form<?> form = new Form<>("paymentRemoveForm");
        getContainer().add(form);

        form.add(paymentRemoveModal = new PaymentRemoveModal("paymentRemoveModal"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getFeedback(), getTable());
            }
        });
    }

    protected void setSellerWorkerIdFilter(Long workerId){
        getFilterWrapper().put(Payment.FILTER_SELLER_WORKER_ID, workerId);
    }

    protected boolean isActualMonthFilter(){
        return true;
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Payment.DATE, Payment.CONTRACT, Payment.PERIOD_START, Payment.PERIOD_END,
                Payment.POINT);
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

    @Override
    protected boolean isCreateEnabled() {
        return false;
    }

    protected boolean isRemoveEnabled() {
        return false;
    }

    @Override
    public boolean isEditEnabled() {
        return false;
    }

    @Override
    protected void populateAction(RepeatingView repeatingView, IModel<Payment> rowModel) {
        if (isRemoveEnabled() && rowModel.getObject().getStartDate().after(periodService.getActualPeriod()
                .getOperatingMonth())) {
            repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxButton(LinkPanel.LINK_COMPONENT_ID,
                    Buttons.Type.Link) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    paymentRemoveModal.delete(target, rowModel.getObject());
                }

                @Override
                protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                    super.updateAjaxAttributes(attributes);

                    attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                }
            }.setIconType(GlyphIconType.remove)));
        }
    }

    @Override
    protected Component getPagingLeft(String id) {
        return new PeriodPanel(id){
            @Override
            protected void onChange(AjaxRequestTarget target, Period period) {
                getFilterWrapper().put(Payment.FILTER_MONTH, period != null ? period.getOperatingMonth() : null);

                target.add(getTable());
            }
        };
    }
}
