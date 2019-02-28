package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Transaction;
import ru.complitex.jedani.worker.entity.TransactionType;
import ru.complitex.jedani.worker.entity.TransferType;
import ru.complitex.jedani.worker.mapper.TransactionMapper;
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
    private TransactionMapper transactionMapper;

    @Inject
    private StorageService storageService;

    private Transaction transaction;

    public ReceiveModal(String markupId, Long storageId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, storageId, onUpdate);

        getContainer().add(new Label("nomenclature", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return Nomenclatures.getNomenclatureLabel(transaction.getNomenclatureId(), domainService);
            }
        }));

        getContainer().add(new Label("quantity", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return transaction.getQuantity() + "";
            }
        }));
    }

    void open(Transaction transaction, AjaxRequestTarget target){
        this.transaction = transaction;

        open(target);
    }

    void open(Product product, Long transferType, AjaxRequestTarget target){
        List<Transaction> transactions = transactionMapper.getTransactions(FilterWrapper.of(
                new Transaction()).add(Transaction.FILTER_STORAGE_TO_ID, product.getParentId())
                .add(transferType == TransferType.GIFT ? Transaction.FILTER_RECEIVING_GIFT : Transaction.FILTER_RECEIVING, true));

        if (!transactions.isEmpty()){
            Transaction transaction = transactions.get(0);

            if (Objects.equals(transaction.getType(), TransactionType.TRANSFER) &&
                    Objects.equals(transaction.getStorageIdTo(), product.getParentId()) &&
                    Objects.equals(transaction.getTransferType(), transferType) &&
                    transaction.getEndDate() == null
            ) {
                this.transaction = transaction;

                open(target);
            }
        }
    }

    public Transaction getTransaction() {
        return transaction;
    }

    void action(AjaxRequestTarget target) {
        storageService.receive(getTransaction());

        success(getString("info_received"));

        close(target);

        onUpdate(target);
    }
}
