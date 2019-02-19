package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Nomenclature;

public class NomenclatureAutoComplete extends AbstractDomainAutoComplete {
    public NomenclatureAutoComplete(String id, IModel<Long> model, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(id, Nomenclature.ENTITY_NAME, model, onChange);
    }

    public NomenclatureAutoComplete(String id, IModel<Long> model) {
        super(id, Nomenclature.ENTITY_NAME, model, null);
    }

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
                Attributes.capitalize(domain.getTextValue(Nomenclature.NAME));
    }
}
