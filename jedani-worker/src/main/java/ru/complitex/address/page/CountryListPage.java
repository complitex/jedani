package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
@AuthorizeInstantiation(JedaniRoles.ADMINISTRATORS)
public class CountryListPage extends DomainListModalPage<Country> {
    public CountryListPage() {
        super(Country.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Country.NAME).setRequired(true));
        list.add(entity.getEntityAttribute(Country.SHORT_NAME));
        list.add(entity.getEntityAttribute(Country.CURRENCY)
                .withReference(Currency.ENTITY_NAME, Currency.NAME));
        list.add(entity.getEntityAttribute(Country.EXCHANGE_RATE_EUR)
                .withReference(ExchangeRate.ENTITY_NAME, ExchangeRate.NAME, StringType.DEFAULT));

        return list;
    }
}
