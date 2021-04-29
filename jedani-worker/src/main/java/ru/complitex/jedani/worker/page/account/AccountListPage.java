package ru.complitex.jedani.worker.page.account;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Account;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class AccountListPage extends DomainListModalPage<Account> {
    @Inject
    private WorkerService workerService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainService domainService;

    public AccountListPage() {
        super(Account.class);
    }

    @Override
    protected AbstractDomainColumn<Account> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Account.WORKER)) {
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        } else if (a.getEntityAttributeId().equals(Account.PERIOD)) {
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                    Period period = periodMapper.getPeriod(rowModel.getObject().getPeriodId());

                    cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
                }
            };
        } else if (a.getEntityAttributeId().equals(Account.CURRENCY)) {
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                    cellItem.add(new Label(componentId, domainService.getTextValue(Currency.ENTITY_NAME, rowModel.getObject().getCurrencyId(), Currency.SHORT_NAME).toLowerCase()));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected boolean isCreateEnabled() {
        return false;
    }
}
