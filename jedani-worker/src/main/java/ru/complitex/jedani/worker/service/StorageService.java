package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        storage.setType(StorageType.PERSONAL);

        domainService.save(storage);

        return storage;
    }

    @Transactional
    public void accept(Long storageId, Transfer transfer){
        Transfer t = new Transfer();

        t.setNomenclatureId(transfer.getNomenclatureId());
        t.setQuantity(transfer.getQuantity());
        t.setType(TransferType.ACCEPT);
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

    private Transfer newTransfer(Product product, Transfer transfer, Long transactionType){
        Transfer t = new Transfer();

        t.setNomenclatureId(product.getNomenclatureId());
        t.setQuantity(transfer.getQuantity());
        t.setType(transactionType);
        t.setRelocationType(transfer.getRelocationType());
        t.setRecipientType(transfer.getRecipientType());
        t.setStorageIdFrom(product.getParentId());

        return t;
    }

    @Transactional
    public void sell(Product product, Transfer transfer) {
        Transfer t = newTransfer(product, transfer, TransferType.SELL);

        switch (t.getRecipientType().intValue()){
            case (int) TransferRecipientType.WORKER:
                t.setWorkerIdTo(transfer.getWorkerIdTo());

                break;
            case (int) TransferRecipientType.CLIENT:
                t.setLastNameIdTo(transfer.getLastNameIdTo());
                t.setFirstNameIdTo(transfer.getFirstNameIdTo());
                t.setMiddleNameIdTo(transfer.getMiddleNameIdTo());

                break;
        }

        t.setSerialNumber(transfer.getSerialNumber());

        domainService.save(t);


        Product p = domainService.getDomain(Product.class, product.getObjectId(), true);

        p.setQuantity(p.getQuantity() - t.getQuantity());

        domainService.save(p);
    }

    @Transactional
    public void relocate(Product product, Transfer transfer) {
        Transfer t = newTransfer(product, transfer, TransferType.RELOCATION);
        t.setWorkerIdTo(transfer.getWorkerIdTo());

        switch (t.getRecipientType().intValue()){
            case (int) TransferRecipientType.STORAGE:
                t.setStorageIdTo(transfer.getStorageIdTo());

                break;
            case (int) TransferRecipientType.WORKER:
                Storage storage;

                List<Storage> storages = domainService.getDomains(Storage.class, FilterWrapper.of((Storage) new Storage()
                        .setParentId(t.getWorkerIdTo())));

                if (storages.size() == 1){
                    storage = storages.get(0);
                }else if (storages.isEmpty()){
                    storage = new Storage();

                    storage.setParentEntityId(entityService.getEntity(Worker.ENTITY_NAME).getId());
                    storage.setParentId(t.getWorkerIdTo());
                    storage.setType(StorageType.PERSONAL);

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

        switch (t.getRelocationType().intValue()){
            case (int) TransferRelocationType.RELOCATION:
                pFrom.setSendingQuantity(pFrom.getSendingQuantity() + qty);
                pTo.setNumber(Product.RECEIVING_QUANTITY, pTo.getReceivingQuantity() + qty);

                break;
            case (int) TransferRelocationType.GIFT:
                pFrom.setGiftSendingQuantity(pFrom.getGiftSendingQuantity() + qty);
                pTo.setGiftReceivingQuantity(pTo.getReceivingQuantity() + qty);

                break;
        }

        domainService.save(pFrom);
        domainService.save(pTo);
    }

    @Transactional
    public void withdraw(Product product, Transfer transfer) {
        Transfer t = newTransfer(product, transfer, TransferType.WITHDRAW);

        t.setComments(transfer.getComments());

        domainService.save(t);

        Product p = getProduct(t.getStorageIdFrom(), t.getNomenclatureId());

        Long qty = t.getQuantity();

        switch (t.getRelocationType().intValue()){
            case (int) TransferRelocationType.RELOCATION:
                p.setQuantity(p.getQuantity() - qty);

                break;
            case (int) TransferRelocationType.GIFT:
                p.setGiftQuantity(p.getGiftQuantity() - qty);

                break;
        }

        domainService.save(p);
    }

    @Transactional
    public void receive(Transfer transfer) {
        Product pFrom = getProduct(transfer.getStorageIdFrom(), transfer.getNomenclatureId());
        Product pTo = getProduct(transfer.getStorageIdTo(), transfer.getNomenclatureId());

        Long qty = transfer.getQuantity();

        switch (transfer.getRelocationType().intValue()){
            case (int) TransferRelocationType.RELOCATION:
                pFrom.setSendingQuantity(pFrom.getSendingQuantity() - qty);
                pTo.setReceivingQuantity(pTo.getReceivingQuantity() - qty);
                pTo.setQuantity(pTo.getQuantity() + qty);

                break;
            case (int) TransferRelocationType.GIFT:
                pFrom.setGiftSendingQuantity(pFrom.getGiftSendingQuantity() - qty);
                pTo.setGiftReceivingQuantity(pTo.getGiftReceivingQuantity() - qty);
                pTo.setGiftQuantity(pTo.getGiftQuantity() + qty);

                break;
        }

        domainService.save(pFrom);
        domainService.save(pTo);

        transfer.setEndDate(new Date());
        domainService.save(transfer);
    }

    public Long getCountryId(Long storageId){
        Storage storage = domainService.getDomain(Storage.class, storageId);

        if (Objects.equals(storage.getType(), StorageType.REAL)){
            if (storage.getCityId() == null){
                return null;
            }

            City city = domainService.getDomain(City.class, storage.getCityId());
            Region region = domainService.getDomain(Region.class, city.getParentId());

            return region.getParentId();
        }else if (Objects.equals(storage.getType(), StorageType.PERSONAL)){
            Worker worker = domainService.getDomain(Worker.class, storage.getParentId());

            if (worker == null){
                return null;
            }

            Long regionId = domainService.getParentId(City.ENTITY_NAME, worker.getCityId());

            if (regionId != null) {
                return domainService.getParentId(Region.ENTITY_NAME, regionId);
            }
        }

        return null;
    }
}
