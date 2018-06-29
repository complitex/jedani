package ru.complitex.user.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public boolean hasRole(String role){
        if (userGroups == null) {
            return false;
        }

        return userGroups.stream().anyMatch(ug -> Objects.equals(ug.getName(), role));
    }

    public void addRole(String role){
        if (!hasRole(role)){
            if (userGroups == null){
                userGroups = new ArrayList<>();
            }

            userGroups.add(new UserGroup(login, role));
        }
    }

    public void removeRole(String role){
        if (userGroups != null) {
            userGroups.removeIf(userGroup -> userGroup.getName().equals(role));
        }
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
