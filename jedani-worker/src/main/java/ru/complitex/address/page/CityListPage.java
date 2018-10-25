package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 9:32
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityListPage extends DomainListPage<City>{
    @Inject
    private EntityService entityService;

    public CityListPage() {
        super(City.class, Region.ENTITY_NAME, Region.NAME, CityEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(City.CITY_TYPE_ID, City.NAME, City.SHORT_NAME);
    }

    protected List<EntityAttribute> getEntityAttributes(Entity entity){
        entity.getEntityAttribute(City.CITY_TYPE_ID)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(CityType.ENTITY_NAME, CityType.NAME));

        return entity.getAttributes();
    }
}
