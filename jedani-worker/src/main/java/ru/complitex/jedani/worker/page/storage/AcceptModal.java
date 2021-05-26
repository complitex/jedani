package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.entity.Transfer;
import ru.complitex.jedani.worker.service.StorageService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 11:50
 */
class AcceptModal extends StorageModal {
    @Inject
    private StorageService storageService;

    private final NomenclatureAutoComplete nomenclature;

    AcceptModal(String markupId, Long storageId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, storageId, onUpdate);

        getContainer().add(new FormGroupDateTextField("date", new DateAttributeModel(getModel(), Transfer.DATE)));

        getContainer().add(new FormGroupPanel("nomenclature",
                nomenclature = new NomenclatureAutoComplete(FormGroupPanel.COMPONENT_ID,
                new NumberAttributeModel(getModel(), Transfer.NOMENCLATURE))));
        nomenclature.setRequired(true);
        nomenclature.setLabel(new ResourceModel("nomenclature"));

        getContainer().add(new FormGroupTextField<>("quantity", new NumberAttributeModel(getModel(), Transfer.QUANTITY))
                .setRequired(true).setType(Long.class));
    }

    @Override
    protected String getFocusMarkupId() {
        return nomenclature.getAutoCompleteTextField().getMarkupId(true);
    }

    @Override
    void action(AjaxRequestTarget target) {
        storageService.accept(getStorageId(), getModelObject());

        success(getString("info_accepted"));

        close(target);

        onUpdate(target);
    }
}
