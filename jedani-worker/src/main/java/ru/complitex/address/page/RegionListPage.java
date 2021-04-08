package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class RegionListPage extends DomainListModalPage<Region> {
    public RegionListPage() {
        super(Region.class, Country.class, Country.NAME);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Region.NAME).setRequired(true);

        return entity.getEntityAttributes(Region.NAME, Region.SHORT_NAME);
    }
}
