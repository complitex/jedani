package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.RewardMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public List<Reward> validateRewardChargedSum() {
        List<Reward> rewards = new ArrayList<>();

        rewardMapper.getRewards(FilterWrapper.of(new Reward().setRewardStatus(RewardStatus.ESTIMATED)))
                .forEach(reward -> {
                    List<Long> errors = new ArrayList<>();

                    BigDecimal pointSum = rewardMapper.getRewardsPointSum(reward.getType(), reward.getSaleId(), reward.getManagerId(), RewardStatus.CHARGED);

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


                    BigDecimal amountSum = rewardMapper.getRewardsAmountSum(reward.getType(), reward.getSaleId(), reward.getManagerId(), RewardStatus.CHARGED);

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
}
