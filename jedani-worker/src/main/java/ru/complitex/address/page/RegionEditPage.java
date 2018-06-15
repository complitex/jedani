package ru.complitex.address.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.page.DomainEditPage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.12.2017 0:21
 */
public class RegionEditPage extends DomainEditPage{
    public RegionEditPage(PageParameters parameters) {
        super("region", parameters, RegionListPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Region.NAME, Region.SHORT_NAME);
    }

    @Override
    protected String getParentEntityName() {
        return Country.ENTITY_NAME;
    }

    @Override
    protected Long getParentEntityAttributeId() {
        return Country.NAME;
    }
}
