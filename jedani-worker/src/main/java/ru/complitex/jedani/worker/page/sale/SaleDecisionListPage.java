package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.SaleDecision;

public class SaleDecisionListPage extends DomainListModalPage<SaleDecision> {
    private SaleDecisionModal saleDecisionModal;

    public SaleDecisionListPage() {
        super(SaleDecision.class);

        Form saleDecisionForm = new Form("saleDecisionForm");
        getContainer().add(saleDecisionForm);

        saleDecisionForm.add(saleDecisionModal = new SaleDecisionModal("saleDecision"));
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        saleDecisionModal.edit(target);
    }
}
