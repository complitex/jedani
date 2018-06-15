package ru.complitex.address.page;

import ru.complitex.address.entity.Region;
import ru.complitex.domain.page.DomainListPage;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
public class RegionListPage extends DomainListPage{
    public RegionListPage() {
        super("region", RegionEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Region.NAME, Region.SHORT_NAME);
    }
}
