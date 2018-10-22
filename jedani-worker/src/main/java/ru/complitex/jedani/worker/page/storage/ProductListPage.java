package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.security.JedaniRoles;

/**
 * @author Anatoly A. Ivanov
 * 22.10.2018 16:16
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ProductListPage extends DomainListPage<Product> {
    public ProductListPage() {
        super(Product.class, ProductEditPage.class);
    }
}
