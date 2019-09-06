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
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 19.02.2019 20:26
 */
public class SaleService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    @Inject
    private StorageService storageService;

    @Transactional(rollbackFor = SaleException.class)
    public void sale(Sale sale, List<SaleItem> saleItems) throws SaleException {
        domainService.getDomains(SaleItem.class, FilterWrapper.of((SaleItem) new SaleItem().setParentId(sale.getObjectId())))
                .forEach(si -> {
                    if (saleItems.stream().noneMatch(si0 -> Objects.equals(si.getObjectId(), si0.getObjectId()))){
                        domainService.delete(si);
                    }
                });

        //Sale

        Entity saleEntity = entityService.getEntity(Sale.ENTITY_NAME);

        domainService.save(sale);

        for (int i = 0, size = saleItems.size(); i < size; i++) {
            SaleItem s = saleItems.get(i);

            //Product

            Product filter = new Product();

            filter.setParentId(sale.getStorageId());
            filter.setNomenclatureId(s.getNomenclatureId());

            List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of(filter));

            if (products.isEmpty()){
                accept(sale.getStorageId(), s.getNomenclatureId());

                products = domainService.getDomains(Product.class, FilterWrapper.of(filter));
            }

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

    private void accept(Long storageId, Long nomenclatureId){
        Transaction transaction = new Transaction();

        transaction.setNomenclatureId(nomenclatureId);
        transaction.setQuantity(0L);

        storageService.accept(storageId, transaction);
    }

    public boolean validateQuantity(Sale sale, SaleItem saleItem){
        Product filter = new Product();

        filter.setParentId(sale.getStorageId());
        filter.setNomenclatureId(saleItem.getNomenclatureId());

        List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of(filter));

        return !products.isEmpty() &&
                products.get(0).getQuantity() - products.get(0).getReserveQuantity() > saleItem.getQuantity();
    }
}
