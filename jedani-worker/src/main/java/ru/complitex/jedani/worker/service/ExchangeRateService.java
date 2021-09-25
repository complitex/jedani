package ru.complitex.jedani.worker.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.ExchangeRate;
import ru.complitex.jedani.worker.entity.Rate;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 19:47
 */
public class ExchangeRateService implements Serializable {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    public String[] getValue(String uri, String uriDateParam, String uriDateFormat, LocalDate localDate,
                             String xpathDate, String xpathValue){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(uriDateFormat);

            String uriDate = uri + (uri.contains("?") ? "&" : "?") + uriDateParam + "=" + localDate.format(dateTimeFormatter);

            OkHttpClient client = new OkHttpClient.Builder().build();

            Response response = client.newCall(new Request.Builder().url(uriDate).get().build()).execute();

            if (!response.isSuccessful()){
                response = client.newCall(new Request.Builder().url(uriDate).get().build()).execute();
            }

            Document document = factory.newDocumentBuilder().parse(Objects.requireNonNull(response.body()).byteStream());

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            return new String[]{xPath.evaluate(xpathDate, document), xPath.evaluate(xpathValue, document)};
        } catch (Exception e) {
            log.error("error get rate: {}", e.getLocalizedMessage());

            return null;
        }
    }

    public boolean loadValues(ExchangeRate exchangeRate){
        List<Rate> rates = domainService.getDomains(Rate.class, FilterWrapper.of(
                (Rate) new Rate().setParentId(exchangeRate.getObjectId())));

        Map<Date, Rate> map = new HashMap<>();

        rates.forEach(r -> {
             map.put(Dates.atStartOfDay(r.getDate()), r);
         });

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        boolean updated = false;

        Entity exchangeRateEntity = entityService.getEntity(ExchangeRate.ENTITY_NAME);

        for (LocalDate localDate = tomorrow.minusDays(365); localDate.isBefore(tomorrow); localDate = localDate.plusDays(1)){

            Date date = Date.from(localDate.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());

            if (map.get(date) == null){
                Rate rate = new Rate();

                rate.setParentId(exchangeRate.getObjectId());
                rate.setParentEntityId(exchangeRateEntity.getId());
                rate.setDate(date);

                String[] values = getValue(exchangeRate.getUriXml(), exchangeRate.getUriDateParam(),
                        exchangeRate.getUriDateFormat(), localDate, exchangeRate.getXpathDate(),
                        exchangeRate.getXpathValue());

                if (values != null && !values[1].isEmpty()){
                    rate.setRate(new BigDecimal(values[1].replace(',', '.')));

                    domainService.save(rate);

                    map.put(date, rate);

                    updated = true;
                }
            }
        }

        return updated;
    }

    public BigDecimal getExchangeRate(Long countryId, Date date){
        Rate rate = new Rate();

        rate.setParentId(domainService.getNumber(Country.ENTITY_NAME, countryId, Country.EXCHANGE_RATE_EUR));
        rate.setDate(date);
        rate.setFilter(Rate.DATE, Attribute.FILTER_AFTER_OR_EQUAL_DATE);

        List<Rate> rates = domainService.getDomains(Rate.class, FilterWrapper.of(rate)
                        .sort("date", rate.getAttribute(Rate.DATE), false)
                        .limit(0L, 1L));

        if (!rates.isEmpty()){
            return rates.get(0).getRate();
        }

        return null;
    }

    public BigDecimal getMonthAverageExchangeRate(Long countryId, Date month){
        List<Rate> rates = domainService.getDomains(Rate.class, FilterWrapper.of((Rate) new Rate()
                .setDate(month)
                .setParentId(domainService.getNumber(Country.ENTITY_NAME, countryId, Country.EXCHANGE_RATE_EUR))
                .setFilter(Rate.DATE, Attribute.FILTER_SAME_MONTH)));

        if (!rates.isEmpty()){
            return rates.stream().map(Rate::getRate).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(rates.size()), 5, RoundingMode.HALF_EVEN);
        }

        return null;
    }
}
