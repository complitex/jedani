package ru.complitex.jedani.worker.entity;

import ru.complitex.user.entity.User;
import ru.complitex.user.entity.UserGroup;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 25.07.2019 20:03
 */
public class UserHistory implements Serializable {
    private String id;
    private Long userId;
    private String login;
    private String password;
    private Long workerId;
    private String group;
    private Date date;

    public UserHistory(Long userId, Long workerId) {
        this.userId = userId;
        this.workerId = workerId;
    }

    public UserHistory(User user, Long workerId) {
        userId = user.getId();
        login = user.getLogin();
        password = user.getPassword();

        this.workerId = workerId;

        if (user.getUserGroups() != null){
            group = user.getUserGroups().stream().map(UserGroup::getName).collect(Collectors.joining(","));
        }
    }

    public UserHistory setUserGroups(List<UserGroup> userGroups){
        if (userGroups != null) {
            group = userGroups.stream().map(UserGroup::getName).collect(Collectors.joining(","));
        }

        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public UserHistory setLogin(String login) {
        this.login = login;

        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserHistory setPassword(String password) {
        this.password = password;

        return this;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
