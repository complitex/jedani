package ru.complitex.user.entity;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 12:38
 */
public class UserGroup implements Serializable{
    private Long id;
    private String login;
    private String name;

    public UserGroup() {
    }

    public UserGroup(String login, String name) {
        this.login = login;
        this.name = name;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
