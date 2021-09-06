package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.STRUCTURE_ADMINISTRATORS})
public class RegionListPage extends DomainListModalPage<Region> {
    @Inject
    private DomainService domainService;

    public RegionListPage() {
        super(Region.class, Country.class, Country.NAME);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Region.NAME).setRequired(true);

        return entity.getEntityAttributes(Region.NAME, Region.SHORT_NAME);
    }

    @Override
    protected boolean checkUnique(Domain domain) {
        String name = domain.getText(Region.NAME);
        String shortName = domain.getText(Region.SHORT_NAME);

        return domainService.getDomains(Region.class, FilterWrapper.of(new Region().setName(name))).size() == 0 &&
                (shortName == null || domainService.getDomains(Region.class, FilterWrapper.of(new Region().setShortName(shortName))).size() == 0);
    }

    @Override
    protected boolean isEditEnabled() {
        return isAdmin();
    }

    @Override
    protected boolean isCreateEnabled() {
        return isAdmin();
    }
}
