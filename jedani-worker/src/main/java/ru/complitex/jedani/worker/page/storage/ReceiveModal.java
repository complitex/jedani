package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.TransactionMapper;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 16.11.2018 15:20
 */
public abstract class ReceiveModal extends StorageModal {
    @Inject
    private DomainService domainService;

    @Inject
    private TransactionMapper transactionMapper;

    private Transaction transaction;

    public ReceiveModal(String markupId) {
        super(markupId);

        getContainer().add(new Label("nomenclature", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                Nomenclature nomenclature = domainService.getDomain(Nomenclature.class,
                        transaction.getNumber(Transaction.NOMENCLATURE));

                return nomenclature.getText(Nomenclature.CODE) + " " +
                        Attributes.capitalize(nomenclature.getTextValue(Nomenclature.NAME));
            }
        }));

        getContainer().add(new Label("quantity", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return transaction.getNumber(Transaction.QUANTITY) + "";
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

            if (Objects.equals(transaction.getNumber(Transaction.TYPE), TransactionType.TRANSFER) &&
                    Objects.equals(transaction.getNumber(Transaction.STORAGE_TO), product.getParentId()) &&
                    Objects.equals(transaction.getNumber(Transaction.TRANSFER_TYPE), transferType) &&
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
}
