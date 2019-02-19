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
import java.util.Optional;

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
        //Sale

        Entity saleEntity = entityService.getEntity(Sale.ENTITY_NAME);

        domainService.save(sale);

        for (int i = 0, size = saleItems.size(); i < size; i++) {
            SaleItem s = saleItems.get(i);

            //Validation

            Product filter = new Product();

            filter.setParentId(s.getNumber(SaleItem.STORAGE));
            filter.setNumber(Product.NOMENCLATURE, s.getNumber(SaleItem.NOMENCLATURE));

            List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of(filter));

            if (products.isEmpty() || products.get(0).getNumber(Product.QUANTITY) < s.getNumber(SaleItem.QUANTITY)) {
                throw new SaleException("Количество товара на складе меньше " + s.getNumber(SaleItem.QUANTITY) +
                        " для позиции № " + (i+1));
            }

            //Product

            Product product = products.get(0);

            product.setNumber(Product.RESERVE, Optional.ofNullable(product.getNumber(Product.RESERVE))
                    .orElse(0L) + s.getNumber(SaleItem.QUANTITY));

            domainService.save(product);

            //Transaction

            Transaction t = new Transaction();

            t.setNumber(Transaction.NOMENCLATURE, s.getNumber(SaleItem.NOMENCLATURE));
            t.setNumber(Transaction.QUANTITY, s.getNumber(SaleItem.QUANTITY));
            t.setNumber(Transaction.TYPE, s.getNumber(TransactionType.RESERVE));
            t.setNumber(Transaction.STORAGE_FROM, s.getNumber(SaleItem.STORAGE));
            t.setNumber(Transaction.FIRST_NAME_TO, sale.getNumber(Sale.BUYER_FIRST_NAME));
            t.setNumber(Transaction.MIDDLE_NAME_TO, sale.getNumber(Sale.BUYER_MIDDLE_NAME));
            t.setNumber(Transaction.LAST_NAME_TO, sale.getNumber(Sale.BUYER_LAST_NAME));

            domainService.save(t);

            //Sale Item

            s.setParentEntityId(saleEntity.getId());
            s.setParentId(sale.getObjectId());

            domainService.save(s);
        }
    }
}
