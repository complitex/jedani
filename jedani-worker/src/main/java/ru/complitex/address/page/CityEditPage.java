package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 4:00
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityEditPage extends DomainEditPage<City>{
    public CityEditPage(PageParameters parameters) {
        super(City.class, parameters, CityListPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(City.CITY_TYPE, City.NAME, City.SHORT_NAME);
    }

    @Override
    protected String getParentEntityName() {
        return Region.ENTITY_NAME;
    }

    @Override
    protected Long getParentEntityAttributeId() {
        return Region.NAME;
    }

    @Override
    protected void onAttribute(Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(City.CITY_TYPE)){
            attribute.getEntityAttribute().withReference(CityType.ENTITY_NAME, CityType.NAME);
        }
    }
}
