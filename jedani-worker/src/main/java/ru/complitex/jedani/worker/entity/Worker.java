package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 3:29
 */
public class Worker extends Domain{
    public static final long ENTITY_ID = 20;

    public static final long J_ID = 1;
    public static final long FIRST_NAME = 2;
    public static final long MIDDLE_NAME = 3;
    public static final long LAST_NAME = 4;
    public static final long BIRTHDAY = 5;
    public static final long REGION_IDS = 6;
    public static final long CITY_IDS = 7;
    public static final long PHONE = 8;
    public static final long EMAIL = 9;
    public static final long CONTACT_INFO = 10;
    public static final long MANAGER_RANK_ID = 11;
    public static final long CREATED_AT = 12;
    public static final long UPDATED_AT = 13;
    public static final long INVOLVED_AT = 14;
    public static final long MK_STATUS = 15;
    public static final long FIRED_STATUS = 16;
    public static final long OLD_PARENT_ID = 17;
    public static final long OLD_CHILD_ID = 18;
    public static final long ANCESTRY = 19;
    public static final long FULL_ANCESTRY_PATH = 20;
    public static final long DEPTH_LEVEL = 21;
    public static final long ANCESTRY_DEPTH = 22;
    public static final long RESET_PASSWORD_TOKEN = 23;
    public static final long RESET_PASSWORD_SEND_AT = 24;
    public static final long REMEMBER_CREATED_AT = 25;

    public Worker() {
        super("worker");
    }

    public Worker(Domain domain) {
        super(domain, "worker");
    }

    public void init(){
        setDate(INVOLVED_AT, new Date());
        setNumber(LAST_NAME, null);
        setNumber(FIRST_NAME, null);
        setNumber(MIDDLE_NAME, null);
        setDate(BIRTHDAY, null);
        setJson(PHONE, null);
        setText(EMAIL, null);
        setJson(REGION_IDS, null);
        setJson(CITY_IDS, null);
    }
}
