package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.entity.Transaction;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 11:50
 */
abstract class AcceptModal extends StorageModal {
    private AbstractDomainAutoComplete nomenclature;

    AcceptModal(String markupId) {
        super(markupId);

        getContainer().add(new FormGroupPanel("nomenclature",
                nomenclature = new NomenclatureAutoComplete(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(getModel(), Transaction.NOMENCLATURE))));
        nomenclature.setRequired(true);
        nomenclature.setLabel(new ResourceModel("nomenclature"));

        getContainer().add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(), Transaction.QUANTITY))
                .setRequired(true).setType(Long.class));
    }

    @Override
    protected String getFocusMarkupId() {
        return nomenclature.getAutoCompleteTextField().getMarkupId(true);
    }
}
