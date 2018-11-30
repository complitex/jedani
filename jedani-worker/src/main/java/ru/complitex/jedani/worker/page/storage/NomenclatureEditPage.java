package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.component.form.AttributeSelectList;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 16:11
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class NomenclatureEditPage extends DomainEditPage<Nomenclature> {
    public NomenclatureEditPage(PageParameters parameters) {
        super(Nomenclature.class, parameters, NomenclatureListPage.class);
    }

    @Override
    protected Component getComponent(String componentId, Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(Nomenclature.COUNTRIES)) {
            return new AttributeSelectList(componentId, Model.of(attribute), Country.ENTITY_NAME, Country.NAME,  true);
        }

        return null;
    }
}
