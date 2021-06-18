package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Account;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.mapper.PayoutMapper;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutEditModal extends AbstractEditModal<Account> {
    @Inject
    private PayoutMapper payoutMapper;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private WorkerService workerService;

    @Inject
    private DomainService domainService;

    private final IModel<Account> accountModel = Model.of(new Account());

    public PayoutEditModal(String markupId) {
        super(markupId);

        getContainer().add(new TextField<>("jid", LoadableDetachableModel.of(() -> workerService.getJId(accountModel.getObject().getWorkerId()))).setEnabled(false));
        getContainer().add(new TextField<>("fio", LoadableDetachableModel.of(() -> workerService.getFio(accountModel.getObject().getWorkerId()))).setEnabled(false));

        ListView<Payout> listView = new ListView<>("payouts", LoadableDetachableModel.of(() ->
                payoutMapper.getPayouts(FilterWrapper.of(new Payout()
                        .setPeriodId(periodMapper.getActualPeriodId())
                        .setWorkerId(accountModel.getObject().getWorkerId()))))) {
            @Override
            protected void populateItem(ListItem<Payout> item) {
                Payout payout = item.getModelObject();

                item.add(new Label("date", Dates.getDateText(payout.getDate())));
                item.add(new Label("period", Dates.getMonthText(periodMapper.getPeriod(payout.getPeriodId()).getOperatingMonth())));
                item.add(new Label("amount", payout.getAmount().toPlainString()));
                item.add(new AjaxLink<>("remove") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        domainMapper.delete(payout);

                        PayoutEditModal.this.success(getString("info_removed"));

                        target.add(getContainer());
                    }
                });
            }
        };

        getContainer().add(listView);
    }

    @Override
    public void edit(Long accountId, AjaxRequestTarget target) {
        Account account = domainService.getDomain(Account.class, accountId);

        super.edit(accountId, target);

        accountModel.setObject(account);

        target.add(getContainer());
    }

    @Override
    protected boolean isSaveVisible() {
        return false;
    }

    @Override
    protected void cancel(AjaxRequestTarget target) {
        super.cancel(target);

        getOnUpdate().accept(target);
    }
}
