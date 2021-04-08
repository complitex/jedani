package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.form.AbstractDomainAutoCompleteList;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.util.Nomenclatures;

public class NomenclatureAutoCompleteList extends AbstractDomainAutoCompleteList<Nomenclature> {
    public NomenclatureAutoCompleteList(String id, String entityName, IModel<Attribute> model) {
        super(id, Nomenclature.class, model);
    }

    @Override
    protected String getTextValue(Nomenclature domain) {
        return Nomenclatures.getNomenclatureLabel(domain);
    }

    @Override
    protected Nomenclature getFilterObject(String input) {
        Nomenclature nomenclature = new Nomenclature();

        nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
        nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

        return nomenclature;
    }
}
