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

    public Storage createVirtualStorage(Long workerId){
        Storage storage = new Storage();

        storage.setParentEntityId(entityService.getEntity(Worker.ENTITY_NAME).getId());
        storage.setParentId(workerId);
        storage.setType(StorageType.VIRTUAL);

        domainService.save(storage);

        return storage;
    }

    @Transactional
    public void accept(Long storageId, Transaction transaction){
        Transaction t = new Transaction();

        t.setNomenclatureId(transaction.getNomenclatureId());
        t.setQuantity(transaction.getQuantity());
        t.setType(TransactionType.ACCEPT);
        t.setStorageIdTo(storageId);

        domainService.save(t);

        Product product;

        if (isProductExist(storageId, t.getNomenclatureId())){
            product = getProduct(storageId, t.getNomenclatureId());

            product.setQuantity(product.getQuantity() + t.getQuantity());
        }else {
            product = new Product();

            product.setParentId(t.getStorageIdTo());
            product.setParentEntityId(entityService.getEntity(Storage.ENTITY_NAME).getId());
            product.setNomenclatureId(t.getNomenclatureId());
            product.setQuantity(t.getQuantity());
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

        t.setNomenclatureId(product.getNomenclatureId());
        t.setQuantity(transaction.getQuantity());
        t.setType(transactionType);
        t.setTransferType(transaction.getTransferType());
        t.setRecipientType(transaction.getRecipientType());
        t.setStorageIdFrom(product.getParentId());

        return t;
    }

    @Transactional
    public void sell(Product product, Transaction transaction) {
        Transaction t = newTransaction(product, transaction, TransactionType.SELL);

        switch (t.getRecipientType().intValue()){
            case (int) RecipientType.WORKER:
                t.setWorkerIdTo(transaction.getWorkerIdTo());

                break;
            case (int) RecipientType.CLIENT:
                t.setLastNameIdTo(transaction.getLastNameIdTo());
                t.setFirstNameIdTo(transaction.getFirstNameIdTo());
                t.setMiddleNameIdTo(transaction.getMiddleNameIdTo());

                break;
        }

        t.setSerialNumber(transaction.getSerialNumber());

        domainService.save(t);


        Product p = domainService.getDomain(Product.class, product.getObjectId(), true);

        p.setQuantity(p.getQuantity() - t.getQuantity());

        domainService.save(p);
    }

    @Transactional
    public void transfer(Product product, Transaction transaction) {
        Transaction t = newTransaction(product, transaction, TransactionType.TRANSFER);
        t.setWorkerIdTo(transaction.getWorkerIdTo());

        switch (t.getRecipientType().intValue()){
            case (int) RecipientType.STORAGE:
                t.setStorageIdTo(transaction.getStorageIdTo());

                break;
            case (int) RecipientType.WORKER:
                Storage storage;

                List<Storage> storages = domainService.getDomains(Storage.class, FilterWrapper.of((Storage) new Storage()
                        .setParentId(t.getWorkerIdTo())));

                if (storages.size() == 1){
                    storage = storages.get(0);
                }else if (storages.isEmpty()){
                    storage = new Storage();

                    storage.setParentEntityId(entityService.getEntity(Worker.ENTITY_NAME).getId());
                    storage.setParentId(t.getWorkerIdTo());
                    storage.setType(StorageType.VIRTUAL);

                    domainService.save(storage);
                } else {
                    throw new RuntimeException("More than one worker storage for worker");
                }

                t.setStorageIdTo(storage.getObjectId());

                break;
        }

        domainService.save(t);


        Product pFrom = domainService.getDomain(Product.class, product.getObjectId(), true);
        Product pTo;

        Long storageId = t.getStorageIdTo();

        if (!isProductExist(storageId, t.getNomenclatureId())){
            pTo = new Product();

            pTo.setParentEntityId(entityService.getEntity(Storage.ENTITY_NAME).getId());
            pTo.setParentId(storageId);
            pTo.setNomenclatureId(t.getNomenclatureId());
            pTo.setQuantity(0L);

            domainService.save(product);
        }else{
            pTo = getProduct(storageId, t.getNomenclatureId());
        }


        Long qty = t.getQuantity();

        pFrom.setQuantity( pFrom.getQuantity() - qty);

        switch (t.getTransferType().intValue()){
            case (int) TransferType.TRANSFER:
                pFrom.setSendingQuantity(pFrom.getSendingQuantity() + qty);
                pTo.setNumber(Product.RECEIVING_QUANTITY, pTo.getReceivingQuantity() + qty);

                break;
            case (int) TransferType.GIFT:
                pFrom.setGiftSendingQuantity(pFrom.getGiftSendingQuantity() + qty);
                pTo.setGiftReceivingQuantity(pTo.getReceivingQuantity() + qty);

                break;
        }

        domainService.save(pFrom);
        domainService.save(pTo);
    }

    @Transactional
    public void withdraw(Product product, Transaction transaction) {
        Transaction t = newTransaction(product, transaction, TransactionType.WITHDRAW);

        t.setComments(transaction.getComments());

        domainService.save(t);

        Product p = getProduct(t.getStorageIdFrom(), t.getNomenclatureId());

        Long qty = t.getQuantity();

        switch (t.getTransferType().intValue()){
            case (int) TransferType.TRANSFER:
                p.setQuantity(p.getQuantity() - qty);

                break;
            case (int) TransferType.GIFT:
                p.setGiftQuantity(p.getGiftQuantity() - qty);

                break;
        }

        domainService.save(p);
    }

    @Transactional
    public void receive(Transaction transaction) {
        Product pFrom = getProduct(transaction.getStorageIdFrom(), transaction.getNomenclatureId());
        Product pTo = getProduct(transaction.getStorageIdTo(), transaction.getNomenclatureId());

        Long qty = transaction.getQuantity();

        switch (transaction.getTransferType().intValue()){
            case (int) TransferType.TRANSFER:
                pFrom.setSendingQuantity(pFrom.getSendingQuantity() - qty);
                pTo.setReceivingQuantity(pTo.getReceivingQuantity() - qty);
                pTo.setQuantity(pTo.getQuantity() + qty);

                break;
            case (int) TransferType.GIFT:
                pFrom.setGiftSendingQuantity(pFrom.getGiftSendingQuantity() - qty);
                pTo.setGiftReceivingQuantity(pTo.getGiftReceivingQuantity() - qty);
                pTo.setGiftQuantity(pTo.getGiftQuantity() + qty);

                break;
        }

        domainService.save(pFrom);
        domainService.save(pTo);

        transaction.setEndDate(new Date());
        domainService.save(transaction);
    }
}
