package ru.complitex.user.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 12:36
 */
public class User implements Serializable{
    public static final long ENTITY_ID = 10;

    private Long id;
    private String login;
    private String password;

    private String confirmPassword;

    private List<UserGroup> userGroups;

    public User() {
    }

    public User(String login) {
        this.login = login;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }
}
