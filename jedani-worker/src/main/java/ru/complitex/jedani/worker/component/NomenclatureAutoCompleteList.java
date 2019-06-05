package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.form.AbstractDomainAutoCompleteList;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.util.Nomenclatures;

public class NomenclatureAutoCompleteList extends AbstractDomainAutoCompleteList {
    public NomenclatureAutoCompleteList(String id, String entityName, IModel<Attribute> model) {
        super(id, entityName, model);
    }

    @Override
    protected String getTextValue(Domain domain) {
        return Nomenclatures.getNomenclatureLabel(domain);
    }

    @Override
    protected Domain getFilterObject(String input) {
        Nomenclature nomenclature = new Nomenclature();

        nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
        nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

        return nomenclature;
    }
}
