package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 9:32
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityListPage extends DomainListPage{
    public CityListPage() {
        super("city", "region", Region.NAME, CityEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(City.CITY_TYPE_ID, City.NAME, City.SHORT_NAME);
    }
}
