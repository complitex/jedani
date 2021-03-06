package ru.complitex.jedani.worker.page.catalog;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 20:32
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CurrencyListPage extends DomainListModalPage<Currency> {
    public CurrencyListPage() {
        super(Currency.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Currency.SHORT_NAME).setStringType(StringType.LOWER_CASE);
        entity.getEntityAttribute(Currency.SYMBOL).setStringType(StringType.DEFAULT);
        entity.getEntityAttribute(Currency.CODE).setStringType(StringType.DEFAULT);

        return super.getEntityAttributes(entity);
    }

    @Override
    protected List<EntityAttribute> getEditEntityAttributes(Entity entity) {
        entity.getEntityAttribute(Currency.SHORT_NAME).setStringType(StringType.LOWER_CASE);
        entity.getEntityAttribute(Currency.SYMBOL).setStringType(StringType.DEFAULT);
        entity.getEntityAttribute(Currency.CODE).setStringType(StringType.DEFAULT);

        return super.getEditEntityAttributes(entity);
    }
}
