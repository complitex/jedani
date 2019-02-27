package ru.complitex.jedani.worker.page.catalog;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Currency;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 20:32
 */
public class CurrencyListPage extends DomainListModalPage<Currency> {
    public CurrencyListPage() {
        super(Currency.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Currency.CODE).setStringType(StringType.DEFAULT);

        return super.getEntityAttributes(entity);
    }

    @Override
    protected List<EntityAttribute> getEditEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Currency.CODE).setStringType(StringType.DEFAULT);

        return super.getEditEntityAttributes(entity);
    }
}
