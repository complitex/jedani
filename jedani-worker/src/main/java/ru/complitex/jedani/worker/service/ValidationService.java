package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.RewardMapper;
import ru.complitex.jedani.worker.mapper.SaleMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class ValidationService implements Serializable {
    @Inject
    private RewardMapper rewardMapper;

    @Inject
    private SaleMapper saleMapper;

    @Inject
    private PaymentService paymentService;

    public List<Reward> validateRewardCharged() {
        List<Reward> rewards = new ArrayList<>();

        rewardMapper.getRewards(FilterWrapper.of(new Reward().setRewardStatus(RewardStatus.ESTIMATED)))
                .forEach(reward -> {
                    List<Long> errors = new ArrayList<>();

                    BigDecimal pointSum = rewardMapper.getRewardsPointSum(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), RewardStatus.CHARGED, null, null);

                    if (reward.getTotal() != null) {
                        if (pointSum != null) {
                            if (reward.getTotal().compareTo(ZERO) <= 0) {
                                errors.add(RewardError.TOTAL_LESS_OR_EQUAL_ZERO);
                            } else if (reward.getTotal().compareTo(pointSum) < 0) {
                                errors.add(RewardError.TOTAL_ESTIMATED_LESS_THAN_CHARGED);
                            }
                        }
                    } else if (!Objects.equals(reward.getType(), RewardType.RANK)) {
                        errors.add(RewardError.TOTAL_NULL);
                    }


                    BigDecimal amountSum = rewardMapper.getRewardsAmountSum(reward.getWorkerId(), reward.getType(), reward.getSaleId(), reward.getManagerId(), RewardStatus.CHARGED);

                    if (reward.getAmount() != null) {
                        if (amountSum != null) {
                            if (reward.getAmount().compareTo(ZERO) <= 0) {
                                errors.add(RewardError.AMOUNT_LESS_OR_EQUAL_ZERO);
                            } else if (reward.getAmount().compareTo(amountSum) < 0) {
                                errors.add(RewardError.AMOUNT_ESTIMATED_LESS_THAN_CHARGED);
                            }
                        }
                    } else if (!Objects.equals(reward.getType(), RewardType.RANK)) {
                        errors.add(RewardError.AMOUNT_NULL);
                    }

                    rewardMapper.getRewards(FilterWrapper.of(new Reward()
                            .setType(reward.getType())
                            .setSaleId(reward.getSaleId())
                            .setManagerId(reward.getManagerId())
                            .setRewardStatus(RewardStatus.CHARGED)))
                            .forEach(r -> {
                                if (reward.getSaleId() != null) {
                                    if (!Objects.equals(r.getRate(), reward.getRate())) {
                                        errors.add(RewardError.RATE_NOT_EQUAL);
                                    }

                                    if (!Objects.equals(r.getCrossRate(), reward.getCrossRate())) {
                                        errors.add(RewardError.CROSS_RATE_NOT_EQUAL);
                                    }

                                    if (!Objects.equals(r.getDiscount(), reward.getDiscount())) {
                                        errors.add(RewardError.DISCOUNT_NOT_EQUAL);
                                    }
                                }
                            });


                    if (!errors.isEmpty()) {
                        reward.setErrors(errors);
                        rewards.add(reward);
                    }
                });

        return rewards;
    }

    public List<Reward> validateRewardEstimated() {
        List<Reward> rewards = new ArrayList<>();

        rewardMapper.getRewards(FilterWrapper.of(new Reward().setRewardStatus(RewardStatus.CHARGED)))
                .forEach(reward -> {
                    List<Long> errors = new ArrayList<>();

                    List<Reward> estimated = rewardMapper.getRewards(FilterWrapper.of(new Reward()
                            .setType(reward.getType())
                            .setSaleId(reward.getSaleId())
                            .setManagerId(reward.getManagerId())
                            .setRewardStatus(RewardStatus.ESTIMATED)));

                    if (estimated.isEmpty()) {
                        errors.add(RewardError.ESTIMATED_EMPTY);
                    }

                    if (!errors.isEmpty()) {
                        reward.setErrors(errors);
                        rewards.add(reward);
                    }
                });

        return rewards;
    }

    public List<Reward> validateRewardWithdraw() {
        List<Reward> rewards = new ArrayList<>();

        saleMapper.getSales(FilterWrapper.of(new Sale().setFeeWithdraw(true)))
                .forEach(sale -> {
                    List<Long> errors = new ArrayList<>();

                    Reward reward = new Reward();

                    reward.setSaleId(sale.getObjectId());
                    reward.setRewardStatus(RewardStatus.WITHDRAWN);

                    List<Reward> estimated = rewardMapper.getRewards(FilterWrapper.of(new Reward()
                            .setSaleId(sale.getObjectId())
                            .setRewardStatus(RewardStatus.WITHDRAWN)));

                    if (estimated.isEmpty()) {
                        errors.add(RewardError.WITHDRAW_EMPTY);
                    }

                    if (!errors.isEmpty()) {
                        reward.setErrors(errors);
                        rewards.add(reward);
                    }
                });

        return rewards;
    }

    public List<Reward> validateSales() {
        List<Reward> rewards = new ArrayList<>();

        saleMapper.getSales(new FilterWrapper<>(new Sale()))
                .forEach(sale -> {
                    if (sale.getPersonalRewardPoint() == null || sale.getPersonalRewardPoint().compareTo(ZERO) == 0) {
                        Reward reward = new Reward();

                        reward.setSaleId(sale.getObjectId());

                        reward.setErrors(List.of(sale.getType().equals(SaleType.MYCOOK)
                                ? RewardError.PERSONAL_MYCOOK_REWARD_NOT_EXISTS
                                : RewardError.PERSONAL_RANGE_REWARD_NOT_EXISTS));

                        rewards.add(reward);
                    }

                    if (Objects.equals(sale.getSaleStatus(), SaleStatus.PAID) || Objects.equals(sale.getSaleStatus(), SaleStatus.OVERPAID)) {
                        Long saleId = sale.getObjectId();

                        Reward reward = new Reward();

                        reward.setSaleId(saleId);

                        List<Long> errors = new ArrayList<>();

                        List<Payment> payments = paymentService.getPaymentsBySaleId(saleId);


                        BigDecimal paymentsSum = payments.stream().map(Payment::getPoint).reduce(ZERO, BigDecimal::add);

                        if (paymentsSum.compareTo(sale.getTotal()) < 0) {
                            errors.add(RewardError.PAYMENT_SUM_LESS_THAN_PAID_SALE_TOTAL);
                        }


                        Arrays.asList(
                                sale.getType() == SaleType.MYCOOK
                                                ? RewardType.PERSONAL_MYCOOK
                                                : RewardType.PERSONAL_RANGE,
                                        RewardType.PERSONAL_VOLUME,
                                        RewardType.CULINARY_WORKSHOP,
                                        RewardType.MANAGER_BONUS,
                                        RewardType.MANAGER_MYCOOK,
                                        RewardType.GROUP_VOLUME,
                                        RewardType.STRUCTURE_VOLUME)
                                .forEach(rewardType -> {
                                    BigDecimal estimatedPoint = rewardMapper.getRewardsPointSum(null, rewardType, saleId, null, RewardStatus.ESTIMATED);

                                    if (estimatedPoint == null) {
                                        estimatedPoint = ZERO;
                                    }

                                    BigDecimal chargedPoint = rewardMapper.getRewardsPointSum(null, rewardType, saleId, null, RewardStatus.CHARGED);

                                    if (chargedPoint == null) {
                                        chargedPoint = ZERO;
                                    }

                                    if (estimatedPoint.compareTo(chargedPoint) != 0) {
                                        errors.add(RewardError.ESTIMATED_POINT_NOT_EQUAL_CHARGED);
                                    }


                                    if (rewardType == RewardType.PERSONAL_MYCOOK || rewardType == RewardType.PERSONAL_RANGE) {
                                        BigDecimal withdrawPoint = rewardMapper.getRewardsPointSum(null, rewardType, saleId, null, RewardStatus.WITHDRAWN);

                                        if (withdrawPoint == null) {
                                            withdrawPoint = ZERO;
                                        }

                                        if (sale.isFeeWithdraw() && estimatedPoint.compareTo(withdrawPoint) != 0) {
                                            errors.add(RewardError.ESTIMATED_POINT_NOT_EQUAL_WITHDRAW);
                                        }
                                    }


                                    BigDecimal estimatedAmount = rewardMapper.getRewardsAmountSum(null, rewardType, saleId, null, RewardStatus.ESTIMATED);

                                    if (estimatedAmount == null) {
                                        estimatedAmount = ZERO;
                                    }

                                    BigDecimal chargedAmount = rewardMapper.getRewardsAmountSum(null, rewardType, saleId, null, RewardStatus.CHARGED);

                                    if (chargedAmount == null) {
                                        chargedAmount = ZERO;
                                    }

                                    if (estimatedAmount.compareTo(chargedAmount) != 0) {
                                        errors.add(RewardError.ESTIMATED_AMOUNT_NOT_EQUAL_CHARGED);
                                    }

                                    if (rewardType == RewardType.PERSONAL_MYCOOK || rewardType == RewardType.PERSONAL_RANGE) {
                                        BigDecimal withdrawAmount = rewardMapper.getRewardsAmountSum(null, rewardType, saleId, null, RewardStatus.WITHDRAWN);

                                        if (withdrawAmount == null) {
                                            withdrawAmount = ZERO;
                                        }

                                        if (sale.isFeeWithdraw() && estimatedAmount.compareTo(withdrawAmount) != 0) {
                                            errors.add(RewardError.ESTIMATED_AMOUNT_NOT_EQUAL_WITHDRAW);
                                        }
                                    }
                                });


                        if (sale.getType().equals(SaleType.MYCOOK) &&
                                rewardMapper.getRewardsAmountSum(null, RewardType.PERSONAL_MYCOOK, saleId, null, null) == null) {
                            errors.add(RewardError.PERSONAL_MYCOOK_REWARD_NOT_EXISTS);
                        }

                        if (sale.getType().equals(SaleType.RANGE) &&
                                rewardMapper.getRewardsAmountSum(null, RewardType.PERSONAL_RANGE, saleId, null, null) == null) {
                            errors.add(RewardError.PERSONAL_RANGE_REWARD_NOT_EXISTS);
                        }

//                        if (rewardMapper.getRewardsAmountSum(null, RewardType.PERSONAL_VOLUME, saleId, null, null) == null) {
//                            errors.add(RewardError.PERSONAL_VOLUME_REWARD_NOT_EXISTS);
//                        }

                        if (sale.getCulinaryWorkerId() != null &&
                                rewardMapper.getRewardsAmountSum(null, RewardType.CULINARY_WORKSHOP, saleId, null, null) == null) {
                            errors.add(RewardError.CULINARY_WORKSHOP_REWARD_NOT_EXISTS);
                        }

                        if (sale.getManagerBonusWorkerId() != null &&
                                rewardMapper.getRewardsAmountSum(null, RewardType.MANAGER_BONUS, saleId, null, null) == null) {
                            errors.add(RewardError.MANAGER_BONUS_REWARD_NOT_EXISTS);
                        }

//                        if (rewardMapper.getRewardsAmountSum(null, RewardType.MANAGER_MYCOOK, saleId, null, null) == null) {
//                            errors.add(RewardError.MANAGER_MYCOOK_REWARD_NOT_EXISTS);
//                        }

//                        if (rewardMapper.getRewardsAmountSum(null, RewardType.GROUP_VOLUME, saleId, null, null) == null) {
//                            errors.add(RewardError.GROUP_VOLUME_REWARD_NOT_EXISTS);
//                        }
//
//                        if (rewardMapper.getRewardsAmountSum(null, RewardType.STRUCTURE_VOLUME, saleId, null, null) == null) {
//                            errors.add(RewardError.STRUCTURE_VOLUME_REWARD_NOT_EXISTS);
//                        }


                        if (!errors.isEmpty()) {
                            reward.setErrors(errors);
                            rewards.add(reward);
                        }
                    }
                });

        return rewards;
    }
}
