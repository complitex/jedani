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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.entity.Payment;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.PaymentMapper;
import ru.complitex.jedani.worker.mapper.PeriodMapper;

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
    private PeriodMapper periodMapper;

    private final PaymentModal paymentModal;

    private final PaymentRemoveModal paymentRemoveModal;

    public PaymentPanel(String id, Worker worker) {
        super(id, Payment.class);

        setSellerWorkerIdFilter(worker.getObjectId());

        getFilterWrapper().put(Payment.FILTER_PERIOD, periodMapper.getActualPeriod().getObjectId());

        Form<?> paymentForm = new Form<>("paymentForm");
        getContainer().add(paymentForm);

        paymentForm.add(paymentModal = new PaymentModal("paymentModal").onUpdate(this::update));

        Form<?> form = new Form<>("paymentRemoveForm");
        getContainer().add(form);

        form.add(paymentRemoveModal = new PaymentRemoveModal("paymentRemoveModal"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                PaymentPanel.this.update(target);
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
        return entity.getEntityAttributes(Payment.DATE, Payment.PERIOD, Payment.SALE,
                Payment.PERIOD_START, Payment.PERIOD_END, Payment.POINT, Payment.RATE, Payment.AMOUNT);
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
        if (a.getEntityAttributeId() == Payment.PERIOD){
            return new AbstractDomainColumn<>("period", this) {
                @Override
                public void populateItem(Item<ICellPopulator<Payment>> cellItem, String componentId, IModel<Payment> rowModel) {
                    Period period = periodMapper.getPeriod(rowModel.getObject().getPeriodId());

                    cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
                }

                @Override
                public Component newFilter(String componentId, Table<Payment> table) {
                    return new TextFilter<>(componentId, Model.of());
                }
            };
        } else if (a.getEntityAttributeId().equals(Payment.SALE)) {
            return new DomainColumn<>(a, new StringResourceModel("sale", this)) {
                @Override
                public Component newFilter(String componentId, Table<Payment> table) {
                    return new TextFilter<>(componentId, new TextAttributeModel(table.getFilterWrapper().getObject(),
                            Payment.CONTRACT, StringType.DEFAULT))
                            .onChange(table::update);
                }

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
        paymentModal.edit(payment.getObjectId(), target);
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
        if (isRemoveEnabled() && rowModel.getObject().getStartDate().after(periodMapper.getActualPeriod()
                .getOperatingMonth())) {
            repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxButton(LinkPanel.COMPONENT_ID,
                    Buttons.Type.Link) {
                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    paymentRemoveModal.delete(rowModel.getObject().getObjectId(), target);
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
                getFilterWrapper().put(Payment.FILTER_PERIOD, period != null ? period.getObjectId() : null);

                updateTable(target);
            }
        };
    }
}
