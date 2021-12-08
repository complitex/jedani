package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 14:19
 */
public class Sale extends Domain {
    public static final String ENTITY_NAME = "sale";

    public static final long SELLER_WORKER = 1;
    public static final long BUYER_FIRST_NAME = 2;
    public static final long BUYER_MIDDLE_NAME = 3;
    public static final long BUYER_LAST_NAME = 4;
    public static final long DATE = 5;
    public static final long TYPE = 6;
    public static final long SAS_REQUEST = 7;
    public static final long PROMOTION = 8;
    public static final long STORAGE = 9;
    public static final long CONTRACT = 10;
    public static final long TOTAL = 11;
    public static final long TOTAL_LOCAL = 12;
    public static final long INSTALLMENT_MONTHS = 13;
    public static final long INITIAL_PAYMENT = 14;
    public static final long STATUS = 15;
    public static final long FOR_YOURSELF = 16;
    public static final long MANAGER_BONUS_WORKER = 17;
    public static final long CULINARY_WORKER = 18;
    public static final long PERSONAL_REWARD_POINT = 19;
    public static final long MANAGER_BONUS_REWARD_POINT = 20;
    public static final long CULINARY_REWARD_POINT = 21;
    public static final long FEE_WITHDRAW = 22;
    public static final long PERIOD = 23;


    public static final String FILTER_SELLER_WORKER = "sellerWorker";
    public static final String FILTER_REGION = "region";
    public static final String FILTER_MONTH = "month";
    public static final String FILTER_ACTUAL = "actual";
    public static final String FILTER_PERIOD = "period";

    public Sale() {
        super(ENTITY_NAME);

        setUseDateAttribute(true);
    }

    public Long getSellerWorkerId(){
        return getNumber(SELLER_WORKER);
    }

    public Sale setSellerWorkerId(Long sellerWorkerId){
        setNumber(SELLER_WORKER, sellerWorkerId);

        return this;
    }

    public Long getBuyerFirstName(){
        return getNumber(BUYER_FIRST_NAME);
    }

    public void setBuyerFirstName(Long firstNameId){
        setNumber(BUYER_FIRST_NAME, firstNameId);
    }

    public Long getBuyerMiddleName(){
        return getNumber(BUYER_MIDDLE_NAME);
    }

    public void setBuyerMiddleName(Long middleNameId){
        setNumber(BUYER_MIDDLE_NAME, middleNameId);
    }

    public Long getBuyerLastName(){
        return getNumber(BUYER_LAST_NAME);
    }

    public void setBuyerLastName(Long lastNameId){
        setNumber(BUYER_LAST_NAME, lastNameId);
    }

    public Date getDate(){
        return getDate(DATE);
    }

    public Sale setDate(Date date){
        setDate(DATE, date);

        return this;
    }

    public Long getType(){
        return getNumber(TYPE);
    }

    public void setType(Long type){
        setNumber(TYPE, type);
    }

    public boolean isSasRequest(){
        return isBoolean(SAS_REQUEST);
    }

    public Long getInstallmentMonths(){
        return getNumber(INSTALLMENT_MONTHS);
    }

    public void setInstallmentMonths(Long months){
        setNumber(INSTALLMENT_MONTHS, months);
    }

    public Long getStorageId(){
        return getNumber(STORAGE);
    }

    public void setStorageId(Long storageId){
        setNumber(STORAGE, storageId);
    }

    public void setTotalLocal(BigDecimal total){
        setDecimal(TOTAL_LOCAL, total);
    }

    public BigDecimal getTotalLocal(){
        return getDecimal(TOTAL_LOCAL);
    }

    public BigDecimal getInitialPayment(){
        return getDecimal(INITIAL_PAYMENT);
    }

    public void setInitialPayment(BigDecimal initialPayment){
        setDecimal(INITIAL_PAYMENT, initialPayment);
    }

    public void setTotal(BigDecimal total){
        setDecimal(TOTAL, total);
    }

    public BigDecimal getTotal(){
        return getDecimal(TOTAL);
    }

    public String getContract(){
        return getText(CONTRACT);
    }

    public Sale setContract(String contract){
        setText(CONTRACT, contract);

        return this;
    }

    public Sale setSaleStatus(Long saleStatus){
        setNumber(STATUS, saleStatus);

        return this;
    }

    public Long getSaleStatus(){
        return getNumber(STATUS);
    }

    public boolean isForYourself(){
        return isBoolean(FOR_YOURSELF);
    }

    public void setForYourself(Boolean forYourself){
        setBoolean(FOR_YOURSELF, forYourself);
    }

    public Long getManagerBonusWorkerId(){
        return getNumber(MANAGER_BONUS_WORKER);
    }

    public void setManagerBonusWorkerId(Long managerBonusWorkerId){
        setNumber(MANAGER_BONUS_WORKER, managerBonusWorkerId);
    }

    public Long getCulinaryWorkerId(){
        return getNumber(CULINARY_WORKER);
    }

    public void setCulinaryWorkerId(Long culinaryWorkerId){
        setNumber(CULINARY_WORKER, culinaryWorkerId);
    }

    public BigDecimal getPersonalRewardPoint(){
        return getDecimal(PERSONAL_REWARD_POINT);
    }

    public void setPersonalRewardPoint(BigDecimal rewardPoint){
        setDecimal(PERSONAL_REWARD_POINT, rewardPoint);
    }

    public BigDecimal getManagerBonusRewardPoint(){
        return getDecimal(MANAGER_BONUS_REWARD_POINT);
    }

    public void setManagerBonusRewardPoint(BigDecimal managerBonusRewardPoint){
        setDecimal(MANAGER_BONUS_REWARD_POINT, managerBonusRewardPoint);
    }

    public BigDecimal getCulinaryRewardPoint(){
        return getDecimal(CULINARY_REWARD_POINT);
    }

    public void setCulinaryRewardPoint(BigDecimal culinaryRewardPoint){
        setDecimal(CULINARY_REWARD_POINT, culinaryRewardPoint);
    }

    public boolean isFeeWithdraw(){
        return isBoolean(FEE_WITHDRAW);
    }

    public Sale setFeeWithdraw(Boolean feeWithdraw){
        setBoolean(FEE_WITHDRAW, feeWithdraw);

        return this;
    }

    public Long getPeriodId(){
        return getNumber(PERIOD);
    }

    public void setPeriodId(Long periodId){
        setNumber(PERIOD, periodId);
    }

    @Override
    public String toString() {
        return "Sale{} " + super.toString();
    }
}
