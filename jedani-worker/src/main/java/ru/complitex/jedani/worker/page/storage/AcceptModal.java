package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Transaction;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 11:50
 */
abstract class AcceptModal extends StorageModal {
    @Inject
    private EntityService entityService;

    private DomainAutoComplete nomenclature;

    AcceptModal(String markupId) {
        super(markupId);

        getContainer().add(new FormGroupPanel("nomenclature", nomenclature = new DomainAutoComplete(FormGroupPanel.COMPONENT_ID,
                entityService.getEntityAttribute(Nomenclature.ENTITY_NAME, Nomenclature.NAME),
                new NumberAttributeModel(getModel(), Transaction.NOMENCLATURE)){
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

                return Strings.defaultIfEmpty(domain.getText(Nomenclature.CODE), "") + " " +
                        Attributes.capitalize(domain.getValueText(Nomenclature.NAME));
            }
        }.setRequired(true).setLabel(new ResourceModel("nomenclature"))));


        getContainer().add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(), Transaction.QUANTITY))
                .setRequired(true).setType(Long.class));
    }

    @Override
    protected String getFocusMarkupId() {
        return nomenclature.getAutoCompleteTextField().getMarkupId(true);
    }
}
