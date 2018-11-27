package ru.complitex.jedani.worker.page.storage;

import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.component.form.FormGroupPanel;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Transaction;
import ru.complitex.jedani.worker.entity.TransactionType;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 11:50
 */
abstract class AcceptModal extends StorageModal {
    @Inject
    private EntityService entityService;

    private Long storageId;

    AcceptModal(String markupId, Long storageId) {
        super(markupId);

        this.storageId = storageId;

        add(new FormGroupPanel("nomenclature", new DomainAutoComplete(FormGroupPanel.COMPONENT_ID,
                entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME),
                new NumberAttributeModel(getModel(), Transaction.NOMENCLATURE_ID)){
            @Override
            protected Domain getFilterObject(String input) {
                Nomenclature nomenclature = new Nomenclature();

                nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
                nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

                return nomenclature;
            }

            @Override
            protected String getTextValue(Domain domain) {
                if (domain == null){
                    return "";
                }

                return domain.getText(Nomenclature.CODE) + " " + Attributes.capitalize(domain.getValueText(Nomenclature.NAME));
            }
        }));

        TextFieldFormGroup<Long> quantity = new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                Transaction.QUANTITY));
        quantity.getTextField().setType(Long.class);
        add(quantity);
    }

    @Override
    protected void beforeAction() {
        Transaction transaction = getModelObject();

        transaction.setNumber(Transaction.STORAGE_ID_TO, storageId);
        transaction.setNumber(Transaction.TYPE, TransactionType.ACCEPT);
    }
}