package ru.complitex.name.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.entity.MiddleName;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.12.2017 17:32
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class MiddleNameListPage extends DomainListPage<MiddleName> {
    public MiddleNameListPage() {
        super(MiddleName.class, MiddleNameEditPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(MiddleName.NAME).setDisplayCapitalize(true);

        return entity.getAttributes();
    }
}