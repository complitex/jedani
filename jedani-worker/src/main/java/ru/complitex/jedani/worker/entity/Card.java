package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.Date;

public class Card extends Domain<Card> {
    public static final String ENTITY_NAME = "card";

    public static final long NUMBER = 1;
    public static final long DATE = 2;
    public static final long WORKER = 3;
    public static final long INDEX = 4;

    public Card() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public String getNumber(){
        return getText(NUMBER);
    }

    public Card setNumber(String cardNumber){
        setText(NUMBER, cardNumber);

        return this;
    }

    public Date getDate(){
        return getDate(DATE);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public void setWorkerId(Long workerId){
        setNumber(WORKER, workerId);
    }

    public Long getIndex(){
        return getNumber(INDEX);
    }

    public void setIndex(Long index){
        setNumber(INDEX, index);
    }
}
