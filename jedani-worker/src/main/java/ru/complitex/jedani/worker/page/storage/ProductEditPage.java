package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.component.StorageAutoCompete;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 22.10.2018 16:17
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ProductEditPage extends DomainEditPage<Product> {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private NameService nameService;

    public ProductEditPage(PageParameters parameters) {
        super(Product.class, parameters, ProductListPage.class);
    }

    @Override
    protected String getParentEntityName() {
        return Storage.ENTITY_NAME;
    }

    @Override
    protected DomainAutoComplete getParentComponent(String componentId, Entity parentEntity, Product product) {
        return new StorageAutoCompete(componentId, new PropertyModel<>(product, "parentId"));
    }

    @Override
    protected Component getComponent(String componentId, Attribute attribute) {
        if (Objects.equals(attribute.getEntityAttributeId(), Product.NOMENCLATURE_ID)){
            return new DomainAutoComplete(componentId,
                    entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME),
                    new PropertyModel<>(attribute, "number")){
                @Override
                protected Domain getFilterObject(String input) {
                    Nomenclature nomenclature = new Nomenclature();

                    nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
                    nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

                    return nomenclature;
                }

                @Override
                protected String getTextValue(Domain domain) {
                    return domain.getText(Nomenclature.CODE) + " " + Attributes.capitalize(domain.getValueText(Nomenclature.NAME));
                }
            };
        }

        return null;
    }

    @Override
    protected boolean validate(Product product) {


        return true;
    }
}
