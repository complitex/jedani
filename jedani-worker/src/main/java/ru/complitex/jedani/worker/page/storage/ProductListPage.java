package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.10.2018 16:16
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class ProductListPage extends DomainListPage<Product> {
    @Inject
    private EntityService entityService;

    @Inject
    private NameService nameService;

    @Inject
    private DomainService domainService;

    public ProductListPage() {
        super(Product.class, ProductEditPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Product.NOMENCLATURE)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME)));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Product, SortProperty>> columns) {

    }


}
