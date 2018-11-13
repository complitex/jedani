package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.entity.Transaction;

/**
 * @author Anatoly A. Ivanov
 * 7.11.2018 18:23
 */
public class StorageWithdrawModal extends StorageAbstractModal{
    private Long productId;

    StorageWithdrawModal(String markupId) {
        super(markupId);

        TextFieldFormGroup<Long> quantity = new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                Transaction.QUANTITY));
        quantity.getTextField().setType(Long.class);
        add(quantity);

        add(new TextFieldFormGroup<>("comments", new NumberAttributeModel(getModel(), Transaction.COMMENTS)));
    }

    void open(Long productId, AjaxRequestTarget target){
        this.productId = productId;

        open(target);
    }

    @Override
    void action() {
        getSession().info(productId + " " +getModelObject().getNumber(Transaction.QUANTITY) + " " + getModelObject().getNumber(Transaction.COMMENTS));

    }
}
