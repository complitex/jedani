package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class RegionListPage extends DomainListPage{
    public RegionListPage() {
        super("region", "country", Country.NAME, RegionEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(Region.NAME, Region.SHORT_NAME);
    }
}
