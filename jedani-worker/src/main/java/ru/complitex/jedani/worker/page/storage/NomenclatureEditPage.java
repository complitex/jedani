package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 16:11
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class NomenclatureEditPage extends DomainEditPage {
    public NomenclatureEditPage(PageParameters parameters) {
        super(Nomenclature.ENTITY_NAME, parameters, NomenclatureListPage.class);
    }
}
