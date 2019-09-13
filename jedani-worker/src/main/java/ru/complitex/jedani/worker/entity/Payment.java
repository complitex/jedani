package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.Date;

public class Payment extends Domain<Payment> {
    public final static String ENTITY_NAME = "payment";

    public final static long WORKER = 1;
    public final static long DATE = 2;
    public final static long PAYMENT = 3;
    public final static long POINT = 4;
    public final static long SALE = 5;
    public final static long RATE = 6;

    public Payment() {
        super(ENTITY_NAME);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }

    public void setWorkerId(Long workerId){
        setNumber(WORKER, workerId);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Long getSaleId(){
        return getNumber(SALE);
    }
}
