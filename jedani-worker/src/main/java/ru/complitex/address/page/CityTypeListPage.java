package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.CityType;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:20
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityTypeListPage extends DomainListPage<CityType>{
    public CityTypeListPage() {
        super(CityType.class, CityTypeEditPage.class);
    }

    @Override
    protected List<Long> getEntityAttributeIds() {
        return Arrays.asList(CityType.NAME, CityType.SHORT_NAME);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(CityType.SHORT_NAME).setDisplayLowerCase(true);

        return entity.getAttributes();
    }
}
