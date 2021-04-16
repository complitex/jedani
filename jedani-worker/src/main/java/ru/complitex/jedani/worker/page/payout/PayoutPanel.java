package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PayoutMapper;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutPanel extends DomainListModalPanel<Payout> {
    @Inject
    private WorkerService workerService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private PayoutMapper payoutMapper;

    private final PayoutModal payoutModal;

    public PayoutPanel(String id) {
        super(id, Payout.class);

        Form<?> payoutForm = new Form<>("payoutForm");
        getContainer().add(payoutForm);

        payoutModal = new PayoutModal("payoutModal").onUpdate(this::update);

        payoutForm.add(payoutModal);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Payout.DATE, Payout.PERIOD, Payout.WORKER, Payout.AMOUNT);
    }

    @Override
    protected AbstractDomainColumn<Payout> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Payout.WORKER)){
            return new AbstractDomainColumn<>("worker", this) {
                @Override
                public void populateItem(Item<ICellPopulator<Payout>> cellItem, String componentId, IModel<Payout> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));

                }
            };
        } else if (a.getEntityAttributeId().equals(Payout.PERIOD)){
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Payout>> cellItem, String componentId, IModel<Payout> rowModel) {
                    Period period = periodMapper.getPeriod(rowModel.getObject().getPeriodId());

                    cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
                }
            };
        } else if (a.getEntityAttributeId().equals(Payout.CURRENCY)){
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Payout>> cellItem, String componentId, IModel<Payout> rowModel) {
                    cellItem.add(new Label(componentId, domainService.getText(Currency.ENTITY_NAME, rowModel.getObject().getCurrencyId(), Currency.SYMBOL)));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        payoutModal.create(target);

        Currency currency = getCurrency();

        if (currency != null) {
            payoutModal.getModel().getObject().setCurrencyId(currency.getObjectId());
        }
    }

    @Override
    protected void onEdit(Payout payout, AjaxRequestTarget target) {
        payoutModal.edit(payout, target);
    }

    @Override
    protected FilterWrapper<Payout> newFilterWrapper(Payout payout) {
        payout.setPeriodId(periodMapper.getActualPeriod().getObjectId());

        FilterWrapper<Payout> filterWrapper = FilterWrapper.of(payout);

        Currency currency = getCurrency();

        if (currency != null) {
            filterWrapper.getObject().setCurrencyId(currency.getObjectId());
        }

        return filterWrapper;
    }

    @Override
    protected List<Payout> getDomains(FilterWrapper<Payout> filterWrapper) {
        return payoutMapper.getPayouts(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<Payout> filterWrapper) {
        return payoutMapper.getPayoutsCount(filterWrapper);
    }

    @Override
    protected Component getPagingLeft(String id) {
        return new PeriodPanel(id){
            @Override
            protected void onChange(AjaxRequestTarget target, Period period) {
                getFilterWrapper().getObject().setPeriodId(period != null ? period.getObjectId() : null);

                updateTable(target);
            }
        };
    }

    protected Currency getCurrency() {
        return null;
    }
}
