package ru.complitex.jedani.worker.page.storage;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.entity.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:47
 */
public class StorageListPage extends DomainListPage<Storage> {
    public StorageListPage() {
        super(Storage.class, StorageEditPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Storage.CITY_ID).setDisplayCapitalize(true));
        list.add(entity.getEntityAttribute(Storage.WORKER_IDS).setDisplayCapitalize(true));

        return list;
    }
}
