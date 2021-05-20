package ru.complitex.jedani.worker.page.price;

import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.AbstractModal;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Price;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.04.2019 20:33
 */
public class PriceListPage extends DomainListModalPage<Price> {
    public PriceListPage() {
        super(Price.class, Nomenclature.class, Nomenclature.NAME);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Price.COUNTRY).withReference(Country.class, Country.NAME));
        list.add(entity.getEntityAttribute(Price.DATE_BEGIN));
        list.add(entity.getEntityAttribute(Price.PRICE));

        return list;
    }

    @Override
    protected AbstractModal<Price> newDomainModal(String componentId) {
        return new PriceModal(componentId, getCurrentUser().getId(), t -> t.add(getContainer()));
    }
}
