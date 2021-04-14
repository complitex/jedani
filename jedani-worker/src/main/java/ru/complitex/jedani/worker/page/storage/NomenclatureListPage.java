package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.Model;
import ru.complitex.address.entity.Country;
import ru.complitex.common.wicket.panel.SelectPanel;
import ru.complitex.domain.component.form.AttributeSelectList;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.component.TypeSelect;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.NomenclatureType;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 16:10
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class NomenclatureListPage extends DomainListModalPage<Nomenclature> {
     public NomenclatureListPage() {
        super(Nomenclature.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Nomenclature.NAME)); //todo required
        list.add(entity.getEntityAttribute(Nomenclature.CODE).setStringType(StringType.UPPER_CASE));
        list.add(entity.getEntityAttribute(Nomenclature.COUNTRIES).withReference(Country.class, Country.NAME));

        return list;
    }

    @Override
    protected List<EntityAttribute> getEditEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Nomenclature.TYPE).setRequired(true));
        list.add(entity.getEntityAttribute(Nomenclature.NAME).setRequired(true));
        list.add(entity.getEntityAttribute(Nomenclature.CODE).setStringType(StringType.UPPER_CASE));
        list.add(entity.getEntityAttribute(Nomenclature.COUNTRIES).withReference(Country.class, Country.NAME));

        return list;
    }

    @Override
    protected Component newEditComponent(String componentId, Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(Nomenclature.COUNTRIES)) {
            return new AttributeSelectList(componentId, Model.of(attribute), Country.ENTITY_NAME, Country.NAME,  true);
        }

        if (attribute.getEntityAttributeId().equals(Nomenclature.TYPE)){
            return new SelectPanel(componentId, new TypeSelect(SelectPanel.SELECT_COMPONENT_ID,
                    NumberAttributeModel.of(attribute), NomenclatureType.MYCOOK, NomenclatureType.BASE_ASSORTMENT)
                    .add(new CssClassNameAppender("form-control")));
        }

        return null;
    }

    @Override
    protected Nomenclature newDomain() {
        Nomenclature nomenclature = new Nomenclature();

        nomenclature.setType(NomenclatureType.BASE_ASSORTMENT);

        return nomenclature;
    }
}
