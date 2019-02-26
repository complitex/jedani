package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.address.entity.City;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.util.Storages;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 08.11.2018 18:41
 */
public class StorageAutoComplete extends AbstractDomainAutoComplete {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private NameService nameService;

    public StorageAutoComplete(String id, IModel<Long> model, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(id, Storage.ENTITY_NAME, model, onChange);
    }

    public StorageAutoComplete(String id, IModel<Long> model) {
        super(id, Storage.ENTITY_NAME, model, null);
    }

    @Override
    protected Domain getFilterObject(String input) {
        Storage storage = new Storage();

        Attribute cityId = storage.getOrCreateAttribute(Storage.CITY);
        cityId.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.CITY,
                City.ENTITY_NAME, City.NAME));
        cityId.setText(input);

        Attribute workerIds = storage.getOrCreateAttribute(Storage.WORKERS);
        workerIds.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.WORKERS,
                Worker.ENTITY_NAME, Worker.J_ID));
        workerIds.setText(input);

        return storage;
    }

    @Override
    protected Domain getDomain(Long objectId) {
        return domainService.getDomain(Storage.class, objectId);
    }

    @Override
    protected String getTextValue(Domain domain) {
        return Storages.getStorageLabel(domain, domainService, nameService);
    }
}