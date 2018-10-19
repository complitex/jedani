package ru.complitex.jedani.worker.page.storage;

import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.entity.Storage;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:47
 */
public class StorageListPage extends DomainListPage {
    public StorageListPage() {
        super(Storage.ENTITY_NAME, StorageEditPage.class);
    }
}
