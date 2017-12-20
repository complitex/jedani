package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 3:29
 */
public class Worker extends Domain{
    public static final long ENTITY_ID = 11;

    public static final long J_ID = 1;
    public static final long EMAIL = 2;
    public static final long ANCESTRY = 3;
    public static final long RESET_PASSWORD_TOKEN = 4;
    public static final long RESET_PASSWORD_SEND_AT = 5;
    public static final long REMEMBER_CREATED_AT = 6;
    public static final long CREATED_AT = 7;
    public static final long UPDATED_AT = 8;
    public static final long MK_STATUS = 9;
    public static final long FIRST_NAME = 10;
    public static final long SECOND_NAME = 11;
    public static final long LAST_NAME = 12;
    public static final long PHONE = 13;
    public static final long CITY_ID = 14;
    public static final long MANAGER_RANK_ID = 15;
    public static final long INVOLVED_AT = 16;
    public static final long FULL_ANCESTRY_PATH = 17;
    public static final long DEPTH_LEVEL = 18;
    public static final long ANCESTRY_DEPTH = 19;
    public static final long CONTACT_INFO = 20;
    public static final long BIRTHDAY = 21;
    public static final long FIRED_STATUS = 22;
    public static final long OLD_PARENT_ID = 23;
    public static final long OLD_CHILD_ID = 24;

    public Worker() {
        super("worker");
    }
}
