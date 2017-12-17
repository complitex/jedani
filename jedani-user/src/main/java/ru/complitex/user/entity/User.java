package ru.complitex.user.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 12:36
 */
public class User implements Serializable{
    public static final long ENTITY_ID = 11;

    private Long id;
    private String login;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
