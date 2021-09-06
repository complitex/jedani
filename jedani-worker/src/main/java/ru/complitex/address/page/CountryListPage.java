package ru.complitex.address.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Currency;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.12.2017 1:06
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.STRUCTURE_ADMINISTRATORS})
public class CountryListPage extends DomainListModalPage<Country> {
    @Inject
    private DomainService domainService;

    public CountryListPage() {
        super(Country.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Country.NAME).setRequired(true));
        list.add(entity.getEntityAttribute(Country.SHORT_NAME));
        list.add(entity.getEntityAttribute(Country.CURRENCY)
                .withReference(Currency.class, Currency.NAME));
        list.add(entity.getEntityAttribute(Country.EXCHANGE_RATE_EUR)
                .withReference(ExchangeRate.class, ExchangeRate.NAME, StringType.DEFAULT));

        return list;
    }

    @Override
    protected boolean checkUnique(Domain domain) {
        String name = domain.getText(Country.NAME);
        String shortName = domain.getText(Country.SHORT_NAME);

        return domainService.getDomains(Country.class, FilterWrapper.of(new Country().setName(name))).size() == 0 &&
                (shortName == null || domainService.getDomains(Country.class, FilterWrapper.of(new Country().setShortName(shortName))).size() == 0);
    }

    @Override
    protected boolean isEditEnabled() {
        return isAdmin();
    }

    @Override
    protected boolean isCreateEnabled() {
        return isAdmin();
    }
}
