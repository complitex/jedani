package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Transaction;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 16.11.2018 15:20
 */
public abstract class ReceiveModal extends StorageModal {
    @Inject
    private DomainService domainService;

    private Transaction transaction;

    public ReceiveModal(String markupId) {
        super(markupId);

        add(new Label("nomenclature", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                if (transaction != null){
                    return domainService.getDomain(Nomenclature.class, transaction.getNumber(Transaction.NOMENCLATURE_ID))
                            .getValueText(Nomenclature.NAME);
                }

                return "";
            }
        }));
    }

    void open(Transaction transaction, AjaxRequestTarget target){
        this.transaction = transaction;

        open(target);
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
