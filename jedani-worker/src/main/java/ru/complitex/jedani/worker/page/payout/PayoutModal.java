package ru.complitex.jedani.worker.page.payout;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.component.form.FormGroupAttributeSelect;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.FormGroupWorker;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.AccountService;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutModal extends AbstractEditModal<Payout> {
    @Inject
    private DomainService domainService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private AccountService accountService;

    private final IModel<Payout> model;

    public PayoutModal(String markupId) {
        super(markupId);

        model = Model.of(new Payout());

        add(new FormGroupDateTextField("date", model, Payout.DATE).setRequired(true));

        add(new FormGroupWorker("worker", new PropertyModel<>(model, "workerId")).setRequired(true));

        add(new FormGroupAttributeSelect("currency", model, Payout.CURRENCY, Currency.ENTITY_NAME, Currency.NAME).setRequired(true));

        add(new FormGroupDecimalField("amount", model, Payout.AMOUNT).setRequired(true));
    }

    public void create(Long currencyId, AjaxRequestTarget target) {
        super.create(target);

        Payout payout = new Payout();

        payout.setDate(Dates.currentDate());
        payout.setPeriodId(periodMapper.getActualPeriod().getObjectId());
        payout.setCurrencyId(currencyId);

        model.setObject(payout);
    }

    @Override
    public void edit(Payout object, AjaxRequestTarget target) {
        super.edit(object, target);

        model.setObject(object);
    }

    @Override
    protected void save(AjaxRequestTarget target) {
        super.save(target);

        domainService.save(model.getObject());

        //todo update account

        success(getString("info_payout_saved"));
    }

    @Override
    public IModel<Payout> getModel() {
        return model;
    }
}
