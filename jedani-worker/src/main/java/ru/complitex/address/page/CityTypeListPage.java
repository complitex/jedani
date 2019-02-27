package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.CityType;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:20
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CityTypeListPage extends DomainListModalPage<CityType> {
    public CityTypeListPage() {
        super(CityType.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(CityType.NAME));
        list.add(entity.getEntityAttribute(CityType.SHORT_NAME).setStringType(StringType.LOWER_CASE));

        return list;
    }
}
