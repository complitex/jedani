package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.util.Storages;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 08.11.2018 18:41
 */
public class StorageAutoComplete extends AbstractDomainAutoComplete<Storage> {
    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private StorageMapper storageMapper;

    @Inject
    private NameService nameService;

    public StorageAutoComplete(String id, IModel<Long> model) {
        super(id, Storage.class, model);
    }

    @Override
    protected Storage getFilterObject(String input) {
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
    protected List<Storage> getDomains(String input) {
        Storage storage = new Storage();

        Attribute cityId = storage.getOrCreateAttribute(Storage.CITY);
        cityId.setEntityAttribute(entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.CITY,
                City.ENTITY_NAME, City.NAME));
        cityId.setText(input);

        return storageMapper.getStorages(FilterWrapper.of(storage)
                .setFilter("search")
                .put(Storage.FILTER_WORKER, input)
                .put(Storage.FILTER_WORKERS, input)
                .put(Storage.FILTER_OBJECT_ID, input)
                .limit(0L, 10L));
    }

    @Override
    protected Storage getDomain(Long objectId) {
        return domainService.getDomain(Storage.class, objectId);
    }

    @Override
    protected String getTextValue(Storage storage) {
        return Storages.getStorageLabel(storage, domainService, nameService);
    }
}
