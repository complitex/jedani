package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.SaleException;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 19.02.2019 20:26
 */
public class SaleService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    @Transactional(rollbackFor = SaleException.class)
    public void sale(Sale sale, List<SaleItem> saleItems) throws SaleException {
        //Init
        sale.setObjectId(null);
        saleItems.forEach(s -> s.setObjectId(null));

        //Sale

        Entity saleEntity = entityService.getEntity(Sale.ENTITY_NAME);

        domainService.save(sale);

        for (int i = 0, size = saleItems.size(); i < size; i++) {
            SaleItem s = saleItems.get(i);

            //Validation

            Product filter = new Product();

            filter.setParentId(sale.getStorageId());
            filter.setNomenclatureId(s.getNomenclatureId());

            List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of(filter));

            if (products.isEmpty() || products.get(0).getQuantity() - products.get(0).getReserveQuantity() <
                    s.getQuantity()) {
                throw new SaleException("Количество товара на складе меньше " + s.getQuantity() +
                        " для позиции № " + (i+1));
            }

            //Product

            Product product = products.get(0);

            product.setReserveQuantity(product.getReserveQuantity() + s.getQuantity());

            domainService.save(product);

            //Transaction

            Transaction t = new Transaction();

            t.setNomenclatureId(s.getNomenclatureId());
            t.setQuantity(s.getQuantity());
            t.setType(TransactionType.RESERVE);
            t.setStorageIdFrom(sale.getStorageId());
            t.setFirstNameIdTo(sale.getBuyerFirstName());
            t.setMiddleNameIdTo(sale.getBuyerMiddleName());
            t.setLastNameIdTo(sale.getBuyerLastName());

            domainService.save(t);

            //Sale Item

            s.setParentEntityId(saleEntity.getId());
            s.setParentId(sale.getObjectId());

            domainService.save(s);
        }
    }
}
