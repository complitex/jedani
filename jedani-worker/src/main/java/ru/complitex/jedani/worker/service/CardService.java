package ru.complitex.jedani.worker.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Card;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class CardService implements Serializable {
    @Inject
    private DomainService domainService;

    private static AtomicLong maxIndex = new AtomicLong(0);

    private LuhnCheckDigit luhnCheckDigit = new LuhnCheckDigit();

    public Card createCard(){
        try {
            List<Card> cards = domainService.getDomains(Card.class, FilterWrapper.of(new Card())
                    .sort(ValueType.NUMBER.getKey(), new EntityAttribute(Card.ENTITY_NAME, Card.INDEX), false)
                    .limit(0L,1L));

            long index =  !cards.isEmpty() ? Optional.of(cards.get(0).getIndex()).orElse(0L) + 1 : 1;

            if (index <= maxIndex.get()){
                index = maxIndex.incrementAndGet();
            }else{
                maxIndex.set(index);
            }

            String code = getCode(index);

            String check = luhnCheckDigit.calculate(code);

            String number = getNumber(code, check);

            Card card = new Card();

            card.setNumber(number);
            card.setDate(Dates.currentDate());
            card.setIndex(index);

            return card;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getNumber(String code, String check) {
        return new StringBuilder(code).insert(4, check).toString();
    }

    private String getCode(long index) {
        return StringUtils.leftPad(index + "", 5, "0");
    }

    public boolean isValid(String cardNumber){
        String check = cardNumber.charAt(4) + "";
        String code = new StringBuilder(cardNumber).deleteCharAt(4).toString();

        try {
            return luhnCheckDigit.calculate(code).equals(check);
        } catch (CheckDigitException e) {
            return false;
        }
    }

    public boolean isSame(String cardNumber, Long index){
        try {
            String code = getCode(index);

            String check = luhnCheckDigit.calculate(code);

            return getNumber(code, check).equals(cardNumber);
        } catch (CheckDigitException e) {
            return false;
        }
    }

    public boolean isExists(Card card){
        List<Card> cards = domainService.getDomains(Card.class, FilterWrapper.of(new Card().setNumber(card.getNumber())));

        return cards.stream().anyMatch(c -> !Objects.equals(c.getObjectId(), card.getObjectId()));
    }

    public boolean isWorkerExists(Long workerId){
        return !domainService.getDomains(Card.class, FilterWrapper.of(new Card().setWorkerId(workerId))).isEmpty();

    }

    public void save(Card card){
        if (!isValid(card.getNumber())) {
            throw new RuntimeException("card number is not valid " + card.getNumber());
        }

        if (isExists(card)) {
            throw new RuntimeException("card number is exist " + card.getNumber());
        }

        domainService.save(card);
    }

    @Transactional
    public void generate(Long count, Date date){
        for (int i = 0; i < count; ++i){
            save(createCard());
        }
    }
}
