package ru.complitex.jedani.worker.security;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 10.04.2018 15:43
 */
public class JedaniRoles {
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String ADMINISTRATORS = "ADMINISTRATORS";
    public static final String STRUCTURE_ADMINISTRATORS = "STRUCTURE_ADMINISTRATORS";
    public static final String USERS = "USERS";

    public static List<String> getRoles(){
        return Arrays.asList(USERS, STRUCTURE_ADMINISTRATORS, ADMINISTRATORS);
    }
}
