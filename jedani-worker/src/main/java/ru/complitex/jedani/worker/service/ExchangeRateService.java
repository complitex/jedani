package ru.complitex.jedani.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.mapper.AttributeMapper;
import ru.complitex.jedani.worker.entity.ExchangeRate;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 27.02.2019 19:47
 */
public class ExchangeRateService implements Serializable {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private AttributeMapper attributeMapper;

    public String[] getValue(String uri, String uriDateParam, String uriDateFormat, LocalDate localDate,
                             String xpathDate, String xpathValue){
        try {
            HttpURLConnection.setFollowRedirects(false);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(uriDateFormat);

            String uriDate = uri + (uri.contains("?") ? "&" : "?") + uriDateParam + "=" + localDate.format(dateTimeFormatter);

            Document document = factory.newDocumentBuilder().parse(uriDate);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            return new String[]{xPath.evaluate(xpathDate, document), xPath.evaluate(xpathValue, document)};
        } catch (Exception e) {
            log.error("error get value", e);

            return null;
        }
    }

    public boolean loadValues(ExchangeRate exchangeRate){
        List<Attribute> attributes = attributeMapper.getHistoryAttributes(exchangeRate.getEntityName(),
                exchangeRate.getObjectId(), ExchangeRate.VALUE);

        Map<Date, Attribute> map = attributes.stream().collect(Collectors.toMap(Attribute::getStartDate, a -> a));

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        for (LocalDate date = tomorrow.minusDays(365); date.isBefore(tomorrow); date = date.plusDays(1)){

            ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
            Date startDate = Date.from(date.atTime(LocalTime.MIN).toInstant(zoneOffset));
            Date endDate = Date.from(date.atTime(LocalTime.MAX).toInstant(zoneOffset));

            if (map.get(startDate) == null){
                Attribute a = new Attribute();

                a.setEntityName(exchangeRate.getEntityName());
                a.setObjectId(exchangeRate.getObjectId());
                a.setEntityAttributeId(ExchangeRate.VALUE);
                a.setStartDate(startDate);
                a.setEndDate(endDate);
                a.setStatus(Status.SYNC);

                String[] values = getValue(exchangeRate.getUriXml(), exchangeRate.getUriDateParam(),
                        exchangeRate.getUriDateFormat(), date, exchangeRate.getXpathDate(),
                        exchangeRate.getXpathValue());

                if (values != null && !values[1].isEmpty()){
                    a.setText(values[1].replace(',', '.'));

                    attributeMapper.insertAttribute(a, startDate);

                    map.put(startDate, a);
                }else{
                    return false;
                }
            }
        }

        return true;
    }

    public List<Attribute> getExchangeRateHistories(FilterWrapper<Attribute> filterWrapper){
        return attributeMapper.getHistoryAttributes(filterWrapper);
    }

    public Long getExchangeRateHistoriesCount(FilterWrapper<Attribute> filterWrapper){
        return attributeMapper.getHistoryAttributesCount(filterWrapper);
    }


}
