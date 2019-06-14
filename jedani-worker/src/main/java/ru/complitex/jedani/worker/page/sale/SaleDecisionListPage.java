package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
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
    protected AbstractDomainColumn<SaleDecision> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(SaleDecision.NOMENCLATURE_TYPE)){
            return new AbstractDomainColumn<SaleDecision>(a){

                @Override
                public void populateItem(Item<ICellPopulator<SaleDecision>> cellItem, String componentId, IModel<SaleDecision> rowModel) {
                    Long type = rowModel.getObject().getNomenclatureType();

                    cellItem.add(new Label(componentId, type != null ? getString("type." + type) : ""));
                }
            };
        }

        return super.newDomainColumn(a);
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
