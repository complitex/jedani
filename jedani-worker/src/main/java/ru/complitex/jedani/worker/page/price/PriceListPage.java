package ru.complitex.jedani.worker.page.price;

import org.apache.wicket.Component;
import ru.complitex.domain.model.DomainParentModel;
import ru.complitex.domain.page.DomainEditModal;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Price;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2019 20:33
 */
public class PriceListPage extends DomainListModalPage<Price> {
    public PriceListPage() {
        super(Price.class, Nomenclature.ENTITY_NAME, Nomenclature.NAME);
    }

    @Override
    protected Component newParentComponent(DomainEditModal<Price> domainEditModal, String componentId,
                                           String parentEntityName, Long parentEntityAttributeId) {
        return new NomenclatureAutoComplete(componentId, DomainParentModel.of(domainEditModal.getModel()));
    }
}
