package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 17:24
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class StorageProductPage extends ProductEditPage{

    public StorageProductPage(PageParameters parameters) {
        super(parameters);

        setBackPage(StoragePage.class);
        setBackPageParameters(new PageParameters().add("id", parameters.get("storage_id").toLongObject()));
    }
}
