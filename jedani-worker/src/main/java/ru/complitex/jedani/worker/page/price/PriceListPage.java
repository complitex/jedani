package ru.complitex.jedani.worker.page.price;

import ru.complitex.domain.page.AbstractDomainEditModal;
import ru.complitex.domain.page.DomainListModalPage;
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
    protected AbstractDomainEditModal<Price> newDomainEditModal(String componentId) {
        return new PriceEditModal(componentId, getCurrentUser().getId(), t -> t.add(getContainer()));
    }
}
