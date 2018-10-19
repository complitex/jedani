package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.WorkerAutoCompleteList;
import ru.complitex.jedani.worker.entity.Storage;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:48
 */
public class StorageEditPage extends DomainEditPage<Storage> {
    @Inject
    private EntityService entityService;

    public StorageEditPage(PageParameters parameters) {
        super(Storage.class, parameters, StorageListPage.class);
    }

    @Override
    protected void onAttribute(Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(Storage.CITY_ID)){
            attribute.getEntityAttribute().setDisplayCapitalize(true);
            attribute.getEntityAttribute().setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME));
        }
    }

    @Override
    protected Component getComponent(Attribute attribute) {
        if (attribute.getEntityAttributeId().equals(Storage.WORKER_IDS)){
            return new WorkerAutoCompleteList(COMPONENT_WICKET_ID, Model.of(attribute));
        }

        return null;
    }
}
