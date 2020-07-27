package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.DomainNode;
import ru.complitex.domain.entity.Status;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 3:29
 */
public class Worker extends DomainNode<Worker> {
    public static final String ENTITY_NAME = "worker";

    public static final long ENTITY_ID = 20;

    public static final long J_ID = 1;
    public static final long FIRST_NAME = 2;
    public static final long MIDDLE_NAME = 3;
    public static final long LAST_NAME = 4;
    public static final long BIRTHDAY = 5;
    public static final long REGIONS = 6;
    public static final long CITIES = 7;
    public static final long PHONE = 8;
    public static final long EMAIL = 9;
    public static final long CONTACT_INFO = 10;
    public static final long POSITION = 11;
    public static final long CREATED_AT = 12;
    public static final long UPDATED_AT = 13;
    public static final long REGISTRATION_DATE = 14;
    public static final long MK_STATUS = 15;
    public static final long STATUS = 16;
    public static final long MANAGER_ID = 17;
    public static final long RESET_PASSWORD_TOKEN = 18;
    public static final long RESET_PASSWORD_SEND_AT = 19;
    public static final long REMEMBER_CREATED_AT = 20;
    public static final long TYPE = 21;
    public static final long RANK = 22;
    public static final long POINT = 23;

    public static final long IMPORT_ID = 100;
    public static final long IMPORT_ANCESTRY = 101;
    public static final long IMPORT_OLD_PARENT_ID = 102;
    public static final long IMPORT_OLD_CHILD_ID = 103;
    public static final long IMPORT_MANAGER_RANK_ID = 104;

    private Long subWorkerCount;

    public static final String FILTER_REGION_IDS = "regionIds";

    public Worker() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }

    public Worker(Long objectId) {
        super(ENTITY_NAME, objectId);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }

    public Worker(Domain<Worker> domain) {
        super(domain, ENTITY_NAME);

        setUseDateAttribute(true);
        setUseNumberValue(true);
    }

    public Worker(Long left, Long right){
        this();

        setLeft(left);
        setRight(right);
    }

    public Worker(Long left, Long right, Long level){
        this();

        setLeft(left);
        setRight(right);
        setLevel(level);
    }

    public Worker(Long objectId, Long left, Long right, Long level){
        this();

        setObjectId(objectId);
        setLeft(left);
        setRight(right);
        setLevel(level);
    }

    public void init() {
        setDate(REGISTRATION_DATE, new Date());
        setNumber(LAST_NAME, null);
        setNumber(FIRST_NAME, null);
        setNumber(MIDDLE_NAME, null);
        setDate(BIRTHDAY, null);
        getOrCreateAttribute(PHONE);
        setText(EMAIL, null);
        getOrCreateAttribute(REGIONS);
        getOrCreateAttribute(CITIES);
        setNumber(POSITION, null);
        setNumber(MK_STATUS, null);
        setLeft(0L);
        setRight(0L);
        setLevel(0L);
    }

    public Worker setStatus(Status status) {
        super.setStatus(status);

        return this;
    }

    public Long getSubWorkerCount() {
        return subWorkerCount;
    }

    public void setSubWorkerCount(Long subWorkerCount) {
        this.subWorkerCount = subWorkerCount;
    }

    public String getJId(){
        return getText(J_ID);
    }

    public void setJId(String jId){
        setText(J_ID, jId);
    }

    public Long getFistNameId(){
        return getNumber(FIRST_NAME);
    }

    public void setFirstNameId(Long firstNameId){
        setNumber(FIRST_NAME, firstNameId);
    }

    public Long getMiddleNameId(){
        return getNumber(MIDDLE_NAME);
    }

    public void setMiddleNameId(Long middleNameId){
        setNumber(MIDDLE_NAME, middleNameId);
    }

    public Long getLastNameId(){
        return getNumber(LAST_NAME);
    }

    public void setLastNameId(Long lastNameId){
        setNumber(LAST_NAME, lastNameId);
    }

    public Date getBirthday(){
        return getDate(BIRTHDAY);
    }

    public String getEmail(){
        return getText(EMAIL);
    }

    public Long getPosition(){
        return getNumber(POSITION);
    }

    public Date getRegistrationDate(){
        return getDate(REGISTRATION_DATE);
    }

    public Long getManagerId(){
        return getNumber(MANAGER_ID);
    }

    public Worker setManagerId(Long managerId){
        setNumber(MANAGER_ID, managerId);

        return this;
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public Worker setType(Long type){
        setNumber(TYPE, type);

        return this;
    }

    public boolean isParticipant(){
        return getType() == null || getType().equals(WorkerType.PK);
    }

    public boolean isRegionalLeader(){
        return Objects.equals(getNumber(POSITION), Position.POSITION_REGIONAL_LEADER);
    }

    public String getRegionIdsString(){
        return getNumberValuesString(REGIONS);
    }

    public List<Long> getRegionIds(){
        return getNumberValues(REGIONS);
    }

    public List<Long> getCityIds(){
        return getNumberValues(CITIES);
    }

    public Long getMkStatus(){
        return getNumber(MK_STATUS);
    }

    public void setMkStatus(Long mkStatus){
        setNumber(MK_STATUS, mkStatus);
    }


    public Long getWorkerStatus(){
        return getNumber(STATUS);
    }

    public Worker setWorkerStatus(Long workerStatus){
        setNumber(STATUS, workerStatus);

        return this;
    }
}
