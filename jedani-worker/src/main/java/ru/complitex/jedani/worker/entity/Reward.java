package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.Date;

public class Reward extends Domain<Reward> {
    public static final String ENTITY_NAME = "reward";

    public static final long DATE = 1;
    public static final long WORKER = 2;
    public static final long POINT = 3;
    public static final long TYPE = 4;
    public static final long RANK = 5;
    public static final long DETAIL = 6;

    public Reward() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public void setDate(Date date){
        setDate(DATE, date);
    }

    public Long getWorkerId(){
        return getNumber(WORKER);
    }
}
