package ru.complitex.jedani.worker.page.payout;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.panel.InputPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutListPage extends DomainListModalPage<Payout> {
    @Inject
    private WorkerService workerService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainService domainService;

    private final PayoutModal payoutModal;

    public <P extends Domain<P>> PayoutListPage() {
        super(Payout.class);

        Form<?> payoutForm = new Form<>("payoutForm");
        getContainer().add(payoutForm);

        payoutModal = new PayoutModal("payoutModal").onUpdate(this::update);

        payoutForm.add(payoutModal);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Payout.DATE, Payout.PERIOD, Payout.WORKER, Payout.AMOUNT, Payout.CURRENCY);
    }

    @Override
    protected AbstractDomainColumn<Payout> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Payout.WORKER)){
            return new AbstractDomainColumn<>(a) {
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
                    cellItem.add(new Label(componentId, domainService.getTextValue(Currency.ENTITY_NAME, rowModel.getObject().getCurrencyId(), Currency.SYMBOL)));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected Component newEditComponent(String componentId, Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(Payout.PERIOD)) {
            return new InputPanel(componentId, new DateTextField(InputPanel.INPUT_COMPONENT_ID, new PropertyModel<>(attribute, "date"))
                    .add(new CssClassNameAppender("form-control")));
        } else if (attribute.getEntityAttributeId().equals(Payout.WORKER)) {
            return new WorkerAutoComplete(componentId, new PropertyModel<>(attribute, "number"));
        } else if (attribute.getEntityAttributeId().equals(Payout.CURRENCY)) {
            return  new DomainAutoComplete<>(componentId, Currency.class, Currency.NAME, new PropertyModel<>(attribute, "number"));
        }

        return super.newEditComponent(componentId, attribute);
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        payoutModal.create(target);
    }

    @Override
    protected void onEdit(Payout payout, AjaxRequestTarget target) {
        payoutModal.edit(payout, target);
    }
}
