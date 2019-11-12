package ru.complitex.jedani.worker.page.period;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2019 9:49 PM
 */
public class PeriodListPage extends DomainListModalPage<Period> {
    @Inject
    private WorkerService workerService;

    private PeriodModal periodModal;

    public PeriodListPage() {
        super(Period.class);

        Form periodForm = new Form("periodForm");
        getContainer().add(periodForm);

        periodForm.add(periodModal = new PeriodModal("periodModal").onUpdate(t -> t.add(getFeedback(), getTable())));
    }

    @Override
    protected AbstractDomainColumn<Period> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Period.WORKER)){
            return new AbstractDomainColumn<Period>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Period>> cellItem, String componentId, IModel<Period> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        periodModal.create(target);
    }

    @Override
    protected void onEdit(Period object, AjaxRequestTarget target) {
        periodModal.edit(object, target);
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }
}
