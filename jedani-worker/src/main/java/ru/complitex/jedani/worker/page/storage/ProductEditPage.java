package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 22.10.2018 16:17
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ProductEditPage extends DomainEditPage<Product> {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private NameService nameService;

    public ProductEditPage(PageParameters parameters) {
        super(Product.class, parameters, ProductListPage.class);
    }

    @Override
    protected Component getComponent(Attribute attribute) {
        if (Objects.equals(attribute.getEntityAttributeId(), Product.NOMENCLATURE_ID)){
           return new DomainAutoComplete<Domain>(COMPONENT_WICKET_ID, Domain.class,
                   entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME),
                   new PropertyModel<>(attribute, "number")){
               @Override
               protected List<Domain> getDomains(String input) {
                   Nomenclature nomenclature = new Nomenclature();

                   nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
                   nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

                   return domainMapper.getDomains(FilterWrapper.of(nomenclature)
                           .setFilter("search")
                           .limit(0L, 10L));
               }

               @Override
               protected String getTextValue(Domain domain) {
                   return domain.getText(Nomenclature.CODE) + " " + domain.getValueText(Nomenclature.NAME);
               }
           };
        }

        if (attribute.getEntityAttributeId().equals(Product.STORAGE_ID) ||
                attribute.getEntityAttributeId().equals(Product.STORAGE_INTO_ID)){
            return new DomainAutoComplete<Domain>(COMPONENT_WICKET_ID, Domain.class,
                    entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.CITY_ID),
                    new PropertyModel<>(attribute, "number")){
                @Override
                protected List<Domain> getDomains(String input) {
                    Storage storage = new Storage();

                    Attribute cityId = storage.getOrCreateAttribute(Storage.CITY_ID);
                    cityId.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.CITY_ID));
                    cityId.getEntityAttribute().setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME));
                    cityId.setText(input);

                    Attribute workerIds = storage.getOrCreateAttribute(Storage.WORKER_IDS);
                    workerIds.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.WORKER_IDS));
                    workerIds.getEntityAttribute().setReferenceEntityAttribute(entityService.getEntityAttribute(Worker.ENTITY_NAME, Worker.J_ID));
                    workerIds.setText(input);

                    return domainMapper.getDomains(FilterWrapper.of(storage)
                            .setFilter("search")
                            .limit(0L, 10L));
                }

                @Override
                protected String getTextValue(Domain domain) {
                    Domain city = domainMapper.getDomain(City.ENTITY_NAME, domain.getNumber(Storage.CITY_ID));

                    String workers = domain.getNumberValues(Storage.WORKER_IDS).stream()
                            .map(id -> domainMapper.getDomain(Worker.ENTITY_NAME, id))
                            .map(w -> w.getText(Worker.J_ID) + " " + nameService.getLastName(w.getNumber(Worker.LAST_NAME)))
                            .collect(Collectors.joining(", "));

                    return domain.getObjectId() + " "  + city.getValueText(City.NAME) + " " + workers;
                }

                @Override
                protected Domain getDomain(IModel<Long> model) {
                    return domainMapper.getDomain(Storage.ENTITY_NAME, model.getObject(), false, true);
                }
            };

        }

        return null;
    }
}
