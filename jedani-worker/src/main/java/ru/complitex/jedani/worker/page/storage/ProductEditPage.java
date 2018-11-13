package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.util.Storages;
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
        return new DomainAutoComplete(componentId, entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.CITY_ID),
                new PropertyModel<>(product, "parentId")){
            @Override
            protected Domain getFilterObject(String input) {
                Storage storage = new Storage();

                Attribute cityId = storage.getOrCreateAttribute(Storage.CITY_ID);
                cityId.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.CITY_ID));
                cityId.getEntityAttribute().setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME));
                cityId.setText(input);

                Attribute workerIds = storage.getOrCreateAttribute(Storage.WORKER_IDS);
                workerIds.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.WORKER_IDS));
                workerIds.getEntityAttribute().setReferenceEntityAttribute(entityService.getEntityAttribute(Worker.ENTITY_NAME, Worker.J_ID));
                workerIds.setText(input);

                return storage;
            }

            @Override
            protected String getTextValue(Domain domain) {
                return Storages.getStorageLabel(domain, domainService, nameService);
            }

            @Override
            protected Domain getDomain(IModel<Long> model) {
                return domainService.getDomain(Storage.class, model.getObject());
            }
        };
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
