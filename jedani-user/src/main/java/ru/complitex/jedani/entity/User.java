package ru.complitex.jedani.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 20.11.2017 16:42
 */
public class User implements Serializable{
    private Integer id;
    private String email;
    private String encryptedPassword;
    private String ancestry;
    private String jId;
    private String resetPasswordToken;
    private Date resetPasswordSendAt;
    private Date rememberCreatedAt;
    private Date createdAt;
    private Date updatedAt;
    private Integer mkStatus;
    private String firstName;
    private String secondName;
    private String lastName;
    private String phone;
    private Integer cityId;
    private Integer managerRankId;
    private Date involvedAt;
    private String fullAncestryPath;
    private String depthLevel;
    private Integer ancestryDepth;
    private String contactInfo;
    private Date birthday;
    private Boolean firedStatus;
    private Integer oldParentId;
    private String oldChildId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }

    public String getJId() {
        return jId;
    }

    public void setJId(String jId) {
        this.jId = jId;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Date getResetPasswordSendAt() {
        return resetPasswordSendAt;
    }

    public void setResetPasswordSendAt(Date resetPasswordSendAt) {
        this.resetPasswordSendAt = resetPasswordSendAt;
    }

    public Date getRememberCreatedAt() {
        return rememberCreatedAt;
    }

    public void setRememberCreatedAt(Date rememberCreatedAt) {
        this.rememberCreatedAt = rememberCreatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getMkStatus() {
        return mkStatus;
    }

    public void setMkStatus(Integer mkStatus) {
        this.mkStatus = mkStatus;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getManagerRankId() {
        return managerRankId;
    }

    public void setManagerRankId(Integer managerRankId) {
        this.managerRankId = managerRankId;
    }

    public Date getInvolvedAt() {
        return involvedAt;
    }

    public void setInvolvedAt(Date involvedAt) {
        this.involvedAt = involvedAt;
    }

    public String getFullAncestryPath() {
        return fullAncestryPath;
    }

    public void setFullAncestryPath(String fullAncestryPath) {
        this.fullAncestryPath = fullAncestryPath;
    }

    public String getDepthLevel() {
        return depthLevel;
    }

    public void setDepthLevel(String depthLevel) {
        this.depthLevel = depthLevel;
    }

    public Integer getAncestryDepth() {
        return ancestryDepth;
    }

    public void setAncestryDepth(Integer ancestryDepth) {
        this.ancestryDepth = ancestryDepth;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getFiredStatus() {
        return firedStatus;
    }

    public void setFiredStatus(Boolean firedStatus) {
        this.firedStatus = firedStatus;
    }

    public Integer getOldParentId() {
        return oldParentId;
    }

    public void setOldParentId(Integer oldParentId) {
        this.oldParentId = oldParentId;
    }

    public String getOldChildId() {
        return oldChildId;
    }

    public void setOldChildId(String oldChildId) {
        this.oldChildId = oldChildId;
    }
}
