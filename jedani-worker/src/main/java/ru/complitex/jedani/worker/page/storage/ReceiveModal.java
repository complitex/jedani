package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Transfer;
import ru.complitex.jedani.worker.entity.TransferRelocationType;
import ru.complitex.jedani.worker.entity.TransferType;
import ru.complitex.jedani.worker.mapper.TransferMapper;
import ru.complitex.jedani.worker.service.StorageService;
import ru.complitex.jedani.worker.util.Nomenclatures;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 16.11.2018 15:20
 */
class ReceiveModal extends StorageModal {
    @Inject
    private DomainService domainService;

    @Inject
    private TransferMapper transferMapper;

    @Inject
    private StorageService storageService;

    private Transfer transfer;

    public ReceiveModal(String markupId, Long storageId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, storageId, onUpdate);

        getContainer().add(new Label("nomenclature", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Nomenclatures.getNomenclatureLabel(transfer.getNomenclatureId(), domainService);
            }
        }));

        getContainer().add(new Label("quantity", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return transfer.getQuantity() + "";
            }
        }));
    }

    void open(Transfer transfer, AjaxRequestTarget target){
        this.transfer = transfer;

        open(target);
    }

    void open(Product product, Long relocationType, AjaxRequestTarget target){
        List<Transfer> transfers = transferMapper.getTransfers(FilterWrapper.of(
                new Transfer()).put(Transfer.FILTER_STORAGE_TO_ID, product.getParentId())
                .put(relocationType == TransferRelocationType.GIFT ? Transfer.FILTER_RECEIVING_GIFT : Transfer.FILTER_RECEIVING, true));

        if (!transfers.isEmpty()){
            Transfer transfer = transfers.get(0);

            if (Objects.equals(transfer.getType(), TransferType.RELOCATION) &&
                    Objects.equals(transfer.getStorageIdTo(), product.getParentId()) &&
                    Objects.equals(transfer.getRelocationType(), relocationType) &&
                    transfer.getEndDate() == null
            ) {
                this.transfer = transfer;

                open(target);
            }
        }
    }

    public Transfer getTransfer() {
        return transfer;
    }

    void action(AjaxRequestTarget target) {
        storageService.receive(getTransfer());

        success(getString("info_received"));

        close(target);

        onUpdate(target);
    }
}
