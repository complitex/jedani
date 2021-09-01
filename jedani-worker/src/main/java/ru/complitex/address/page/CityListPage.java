package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 9:32
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityListPage extends DomainListModalPage<City> {
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
}
