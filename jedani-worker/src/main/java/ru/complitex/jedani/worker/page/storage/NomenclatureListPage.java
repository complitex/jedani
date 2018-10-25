package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 16:10
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class NomenclatureListPage extends DomainListPage<Nomenclature> {
    @Inject
    private EntityService entityService;

    public NomenclatureListPage() {
        super(Nomenclature.class, NomenclatureEditPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Nomenclature.NAME));

        list.add(entity.getEntityAttribute(Nomenclature.CODE));

        list.add(entity.getEntityAttribute(Nomenclature.COUNTRY_IDS)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Country.ENTITY_NAME, Country.NAME)));

        return list;
    }
}
