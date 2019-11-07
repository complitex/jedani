package ru.complitex.jedani.worker.page.period;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Period;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2019 9:49 PM
 */
public class PeriodListPage extends DomainListModalPage<Period> {
    private PeriodModal periodModal;

    public PeriodListPage() {
        super(Period.class);

        Form periodForm = new Form("periodForm");
        getContainer().add(periodForm);

        periodForm.add(periodModal = new PeriodModal("periodModal").onUpdate(t -> t.add(getContainer())));
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        periodModal.create(target);
    }

    @Override
    protected void onEdit(Period object, AjaxRequestTarget target) {
        periodModal.edit(object, target);
    }
}
