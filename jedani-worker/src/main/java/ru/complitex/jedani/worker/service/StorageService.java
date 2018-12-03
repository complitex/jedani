package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 15.11.2018 16:11
 */
public class StorageService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    @Transactional
    public void accept(Long storageId, Transaction transaction){
        Transaction t = new Transaction();

        t.copyNumber(Transaction.NOMENCLATURE, transaction);
        t.copyNumber(Transaction.QUANTITY, transaction);
        t.setNumber(Transaction.TYPE, TransactionType.ACCEPT);
        t.setNumber(Transaction.STORAGE_TO, storageId);

        domainService.save(t);

        Product product;

        if (isProductExist(storageId, t.getNumber(Transaction.NOMENCLATURE))){
            product = getProduct(storageId, t.getNumber(Transaction.NOMENCLATURE));

            product.setNumber(Product.QUANTITY, product.getNumber(Product.QUANTITY) +
                    t.getNumber(Transaction.QUANTITY));
        }else {
            product = new Product();

            product.setParentId(t.getNumber(Transaction.STORAGE_TO));
            product.setParentEntityId(entityService.getEntity(Storage.ENTITY_NAME).getId());
            product.setNumber(Product.NOMENCLATURE, t.getNumber(Transaction.NOMENCLATURE));
            product.setNumber(Product.QUANTITY, t.getNumber(Transaction.QUANTITY));
        }

        domainService.save(product);
    }

    private Product getProduct(Long storageId, Long nomenclatureId){
        List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of((Product) new Product()
                .setParentId(storageId)
                .setNumber(Product.NOMENCLATURE, nomenclatureId)), true);

        if (products.size() > 1){
            throw new RuntimeException("Nomenclature product not unique for storage");
        }else if (products.size() == 1){
            return products.get(0);
        }

        throw new RuntimeException("Product not found");
    }

    private boolean isProductExist(Long storageId, Long nomenclatureId){
        return domainService.getDomainsCount(FilterWrapper.of((Product) new Product()
                .setParentId(storageId)
                .setNumber(Product.NOMENCLATURE, nomenclatureId))) > 0;
    }

    private Transaction newTransaction(Product product, Transaction transaction, Long transactionType){
        Transaction t = new Transaction();

        t.setNumber(Transaction.NOMENCLATURE, product.getNumber(Product.NOMENCLATURE));
        t.copyNumber(Transaction.QUANTITY, transaction);
        t.setNumber(Transaction.TYPE, transactionType);
        t.copyNumber(Transaction.TRANSFER_TYPE, transaction);
        t.copyNumber(Transaction.RECIPIENT_TYPE, transaction);
        t.setNumber(Transaction.STORAGE_FROM, product.getParentId());

        return t;
    }

    @Transactional
    public void sell(Product product, Transaction transaction) {
        Transaction t = newTransaction(product, transaction, TransactionType.SELL);

        switch (t.getNumber(Transaction.RECIPIENT_TYPE).intValue()){
            case (int) RecipientType.WORKER:
                t.copyNumber(Transaction.WORKER_TO, transaction);

                break;
            case (int) RecipientType.CLIENT:
                t.copyNumber(Transaction.LAST_NAME_TO, transaction);
                t.copyNumber(Transaction.FIRST_NAME_TO, transaction);
                t.copyNumber(Transaction.MIDDLE_NAME_TO, transaction);

                break;
        }

        t.copyText(Transaction.SERIAL_NUMBER, transaction);

        domainService.save(t);


        Product p = domainService.getDomain(Product.class, product.getObjectId(), true);

        p.setNumber(Product.QUANTITY, p.getNumber(Product.QUANTITY) - t.getNumber(Transaction.QUANTITY));

        domainService.save(p);
    }

    @Transactional
    public void transfer(Product product, Transaction transaction) {
        Transaction t = newTransaction(product, transaction, TransactionType.TRANSFER);
        t.copyNumber(Transaction.WORKER_TO, transaction);

        switch (t.getNumber(Transaction.RECIPIENT_TYPE).intValue()){
            case (int) RecipientType.STORAGE:
                t.copyNumber(Transaction.STORAGE_TO, transaction);

                break;
            case (int) RecipientType.WORKER:
                Storage storage;

                List<Storage> storages = domainService.getDomains(Storage.class, FilterWrapper.of((Storage) new Storage()
                        .setParentId(t.getNumber(Transaction.WORKER_TO))));

                if (storages.size() == 1){
                    storage = storages.get(0);
                }else if (storages.isEmpty()){
                    storage = new Storage();

                    storage.setParentEntityId(entityService.getEntity(Worker.ENTITY_NAME).getId());
                    storage.setParentId(t.getNumber(Transaction.WORKER_TO));
                    storage.setNumber(Storage.TYPE, StorageType.VIRTUAL);

                    domainService.save(storage);
                } else {
                    throw new RuntimeException("More than one worker storage for worker");
                }

                t.setNumber(Transaction.STORAGE_TO, storage.getObjectId());

                break;
        }

        domainService.save(t);


        Product pFrom = domainService.getDomain(Product.class, product.getObjectId(), true);
        Product pTo;

        Long storageId = t.getNumber(Transaction.STORAGE_TO);

        if (!isProductExist(storageId, t.getNumber(Transaction.NOMENCLATURE))){
            pTo = new Product();

            pTo.setParentEntityId(entityService.getEntity(Storage.ENTITY_NAME).getId());
            pTo.setParentId(storageId);
            pTo.setNumber(Product.NOMENCLATURE, t.getNumber(Transaction.NOMENCLATURE));
            pTo.setNumber(Product.QUANTITY, 0L);

            domainService.save(product);
        }else{
            pTo = getProduct(storageId, t.getNumber(Transaction.NOMENCLATURE));
        }


        Long qty = t.getNumber(Transaction.QUANTITY);

        pFrom.setNumber(Product.QUANTITY, pFrom.getNumber(Product.QUANTITY) - qty);

        switch (t.getNumber(Transaction.TRANSFER_TYPE).intValue()){
            case (int) TransferType.TRANSFER:
                pFrom.setNumber(Product.SENDING, pFrom.getNumber(Product.SENDING, 0L) + qty);
                pTo.setNumber(Product.RECEIVING, pTo.getNumber(Product.RECEIVING, 0L) + qty);

                break;
            case (int) TransferType.GIFT:
                pFrom.setNumber(Product.GIFT_SENDING, pFrom.getNumber(Product.GIFT_SENDING, 0L) + qty);
                pTo.setNumber(Product.GIFT_RECEIVING, pTo.getNumber(Product.GIFT_RECEIVING, 0L) + qty);

                break;
        }

        domainService.save(pFrom);
        domainService.save(pTo);
    }

    @Transactional
    public void withdraw(Product product, Transaction transaction) {
        Transaction t = newTransaction(product, transaction, TransactionType.WITHDRAW);

        t.copyText(Transaction.COMMENTS, transaction);

        domainService.save(t);

        Product p = getProduct(t.getNumber(Transaction.STORAGE_FROM), t.getNumber(Transaction.NOMENCLATURE));

        Long qty = t.getNumber(Transaction.QUANTITY);

        switch (t.getNumber(Transaction.TRANSFER_TYPE).intValue()){
            case (int) TransferType.TRANSFER:
                p.setNumber(Product.QUANTITY, p.getNumber(Product.QUANTITY) - qty);

                break;
            case (int) TransferType.GIFT:
                p.setNumber(Product.GIFT_QUANTITY, p.getNumber(Product.GIFT_QUANTITY) - qty);

                break;
        }

        domainService.save(p);
    }

    @Transactional
    public void receive(Transaction transaction) {
        Product pFrom = getProduct(transaction.getNumber(Transaction.STORAGE_FROM), transaction.getNumber(Transaction.NOMENCLATURE));
        Product pTo = getProduct(transaction.getNumber(Transaction.STORAGE_TO), transaction.getNumber(Transaction.NOMENCLATURE));

        Long qty = transaction.getNumber(Transaction.QUANTITY);

        switch (transaction.getNumber(Transaction.TRANSFER_TYPE).intValue()){
            case (int) TransferType.TRANSFER:
                pFrom.setNumber(Product.SENDING, pFrom.getNumber(Product.SENDING, 0L) - qty);
                pTo.setNumber(Product.RECEIVING, pTo.getNumber(Product.RECEIVING, 0L) - qty);
                pTo.setNumber(Product.QUANTITY, pTo.getNumber(Product.QUANTITY) + qty);

                break;
            case (int) TransferType.GIFT:
                pFrom.setNumber(Product.GIFT_SENDING, pFrom.getNumber(Product.GIFT_SENDING, 0L) - qty);
                pTo.setNumber(Product.GIFT_RECEIVING, pTo.getNumber(Product.GIFT_RECEIVING, 0L) - qty);
                pTo.setNumber(Product.GIFT_QUANTITY, pTo.getNumber(Product.GIFT_QUANTITY, 0L) + qty);

                break;
        }

        domainService.save(pFrom);
        domainService.save(pTo);

        transaction.setEndDate(new Date());
        domainService.save(transaction);
    }
}
