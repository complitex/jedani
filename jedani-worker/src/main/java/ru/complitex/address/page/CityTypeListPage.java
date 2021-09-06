package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.CityType;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.12.2017 5:20
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.STRUCTURE_ADMINISTRATORS})
public class CityTypeListPage extends DomainListModalPage<CityType> {
    @Inject
    private DomainService domainService;

    public CityTypeListPage() {
        super(CityType.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(CityType.NAME).setRequired(true));
        list.add(entity.getEntityAttribute(CityType.SHORT_NAME).setStringType(StringType.LOWER_CASE));

        return list;
    }

    @Override
    protected boolean checkUnique(Domain domain) {
        String name = domain.getText(CityType.NAME);
        String shortName = domain.getText(CityType.SHORT_NAME);

        return domainService.getDomains(CityType.class, FilterWrapper.of(new CityType().setName(name))).size() == 0 &&
                (shortName== null || domainService.getDomains(CityType.class, FilterWrapper.of(new CityType().setShortName(shortName))).size() == 0);
    }

    @Override
    protected boolean isEditEnabled() {
        return isAdmin();
    }

    @Override
    protected boolean isCreateEnabled() {
        return isAdmin();
    }
}
