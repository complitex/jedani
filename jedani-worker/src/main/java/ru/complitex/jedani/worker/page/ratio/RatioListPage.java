package ru.complitex.jedani.worker.page.ratio;

import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.AbstractModal;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Ratio;

import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class RatioListPage extends DomainListModalPage<Ratio> {

    public RatioListPage() {
        super(Ratio.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Ratio.COUNTRY).withReference(Country.class, Country.NAME);

        return entity.getAttributes();
    }

    @Override
    protected AbstractModal<Ratio> newDomainModal(String componentId) {
        return new RatioModal(componentId).onUpdate(t -> t.add(getContainer()));
    }
}
