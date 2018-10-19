package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;
import ru.complitex.jedani.worker.entity.Storage;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:48
 */
public class StorageEditPage extends DomainEditPage<Storage> {
    public StorageEditPage(PageParameters parameters) {
        super(Storage.class, parameters, StorageListPage.class);
    }
}
