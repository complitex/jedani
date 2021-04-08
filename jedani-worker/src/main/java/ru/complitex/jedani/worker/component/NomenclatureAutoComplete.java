package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.util.Nomenclatures;

public class NomenclatureAutoComplete extends AbstractDomainAutoComplete<Nomenclature> {
    public NomenclatureAutoComplete(String id, IModel<Long> model) {
        super(id, Nomenclature.class, model);
    }

    @Override
    protected Nomenclature getFilterObject(String input) {
        Nomenclature nomenclature = new Nomenclature();

        nomenclature.getOrCreateAttribute(Nomenclature.CODE).setText(input);
        nomenclature.getOrCreateAttribute(Nomenclature.NAME).setText(input);

        return nomenclature;
    }

    @Override
    protected String getTextValue(Nomenclature domain) {
        return Nomenclatures.getNomenclatureLabel(domain);
    }
}
