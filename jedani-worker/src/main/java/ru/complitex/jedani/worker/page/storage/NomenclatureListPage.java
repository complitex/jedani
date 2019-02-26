package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.panel.SelectPanel;
import ru.complitex.domain.component.form.AttributeSelectList;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.TypeSelect;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.NomenclatureType;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 16:10
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class NomenclatureListPage extends DomainListModalPage<Nomenclature> {
    @Inject
    private DomainService domainService;

    public NomenclatureListPage() {
        super(Nomenclature.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Nomenclature.NAME)); //todo required

        list.add(entity.getEntityAttribute(Nomenclature.CODE));

        list.add(entity.getEntityAttribute(Nomenclature.COUNTRIES).withReference(Country.ENTITY_NAME, Country.NAME));

        return list;
    }

    @Override
    protected Component getEditComponent(String componentId, Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(Nomenclature.COUNTRIES)) {
            return new AttributeSelectList(componentId, Model.of(attribute), Country.ENTITY_NAME, Country.NAME,  true);
        }

        if (attribute.getEntityAttributeId().equals(Nomenclature.TYPE)){
            return new SelectPanel(componentId, new TypeSelect(SelectPanel.SELECT_COMPONENT_ID,
                    NumberAttributeModel.of(attribute), NomenclatureType.MYCOOK, NomenclatureType.BASE_ASSORTMENT));
        }

        return null;
    }

    @Override
    protected boolean validate(Domain<Nomenclature> domain) {
        List<Nomenclature> domains =  domainService.getDomains(Nomenclature.class,
                FilterWrapper.of((Nomenclature) new Nomenclature().setText(Nomenclature.CODE,
                        domain.getText(Nomenclature.CODE))));

        if (domains.stream().anyMatch(n -> !Objects.equals(n.getObjectId(), domain.getObjectId()) &&
                        Objects.equals(domain.getText(Nomenclature.CODE), n.getText(Nomenclature.CODE)))){
            error(getString("error_unique_nomenclature_code"));

            return false;
        }

        return true;
    }
}
