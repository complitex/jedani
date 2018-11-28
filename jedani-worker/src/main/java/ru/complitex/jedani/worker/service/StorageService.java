package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Transaction;
import ru.complitex.jedani.worker.entity.TransactionType;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 15.11.2018 16:11
 */
public class StorageService implements Serializable {
    @Inject
    private DomainMapper domainMapper;

    @Inject
    private EntityService entityService;

    @Transactional
    public void accept(Transaction transaction){
        domainMapper.insertDomain(transaction);

        List<Domain> products = domainMapper.getDomains(FilterWrapper.of(new Product()
                .setParentId(transaction.getNumber(Transaction.STORAGE_ID_TO))
                .setNumber(Product.NOMENCLATURE_ID, transaction.getNumber(Transaction.NOMENCLATURE_ID))));

        if (products.size() > 1){
            throw new RuntimeException("Nomenclature product not unique for storage");
        }else if (products.size() == 1){
            Domain domain = products.get(0);

            domain.setNumber(Product.QUANTITY, domain.getNumber(Product.QUANTITY) +
                    transaction.getNumber(Transaction.QUANTITY));

            domainMapper.updateDomain(domain);
        }else {
            Product product = new Product();

            product.setParentId(transaction.getNumber(Transaction.STORAGE_ID_TO));
            product.setParentEntityId(entityService.getEntity(Storage.ENTITY_NAME).getId());
            product.setNumber(Product.NOMENCLATURE_ID, transaction.getNumber(Transaction.NOMENCLATURE_ID));
            product.setNumber(Product.QUANTITY, transaction.getNumber(Transaction.QUANTITY));

            domainMapper.insertDomain(product);
        }
    }

    @Transactional
    public void sell(Transaction transaction) {
        transaction.setNumber(Transaction.TYPE, TransactionType.SELL);

    }

    @Transactional
    public void transfer(Transaction transaction) {
        transaction.setNumber(Transaction.TYPE, TransactionType.TRANSFER);

    }

    @Transactional
    public void withdraw(Transaction transaction) {
        transaction.setNumber(Transaction.TYPE, TransactionType.WITHDRAW);

    }

    @Transactional
    public void receive(Transaction transaction) {

    }
}
