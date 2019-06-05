package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.SaleDecision;

import java.util.List;

public class SaleDecisionListPage extends DomainListModalPage<SaleDecision> {
    private SaleDecisionModal saleDecisionModal;

    public SaleDecisionListPage() {
        super(SaleDecision.class);

        Form saleDecisionForm = new Form("saleDecisionForm");
        getContainer().add(saleDecisionForm);

        saleDecisionForm.add(saleDecisionModal = new SaleDecisionModal("saleDecision"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getContainer());
            }
        });
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(SaleDecision.COUNTRY).withReference(Country.ENTITY_NAME, Country.NAME);
        entity.getEntityAttribute(SaleDecision.NOMENCLATURES).withReference(Nomenclature.ENTITY_NAME, Nomenclature.NAME);

        return super.getEntityAttributes(entity);
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        saleDecisionModal.add(target);
    }

    @Override
    protected void onEdit(SaleDecision object, AjaxRequestTarget target) {
        saleDecisionModal.edit(object, target);
    }
}
