package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.page.DomainEditPage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 4:00
 */
public class CityEditPage extends DomainEditPage{
    public CityEditPage(PageParameters parameters) {
        super("city", parameters, CityListPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(City.NAME, City.SHORT_NAME);
    }

    @Override
    protected String getParentEntityName() {
        return Region.ENTITY_NAME;
    }

    @Override
    protected Long getParentEntityAttributeId() {
        return Region.NAME;
    }
}
