package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 9:32
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.STRUCTURE_ADMINISTRATORS})
public class CityListPage extends DomainListModalPage<City> {
    @Inject
    private DomainService domainService;

    public CityListPage() {
        super(City.class, Region.class, Region.NAME);
    }

    protected List<EntityAttribute> getEntityAttributes(Entity entity){
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(City.CITY_TYPE).withReference(CityType.class, CityType.NAME).setRequired(true));
        list.add(entity.getEntityAttribute(City.NAME).setRequired(true));
        list.add(entity.getEntityAttribute(City.SHORT_NAME));

        return list;
    }

    @Override
    protected boolean checkUnique(Domain domain) {
        String name = domain.getText(City.NAME);
        String shortName = domain.getText(City.SHORT_NAME);

        return domainService.getDomains(City.class, FilterWrapper.of(new City().setName(name))).size() == 0 &&
                (shortName == null || domainService.getDomains(City.class, FilterWrapper.of(new City().setShortName(shortName))).size() == 0);
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
