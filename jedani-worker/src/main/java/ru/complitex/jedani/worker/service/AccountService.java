package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.AccountException;
import ru.complitex.jedani.worker.mapper.PayoutMapper;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.RewardMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * @author Ivanov Anatoliy
 */
public class AccountService implements Serializable {
    private final static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Inject
    private DomainService domainService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardMapper rewardMapper;

    @Inject
    private WorkerService workerService;

    @Inject
    private PayoutMapper payoutMapper;

    public Account updateAccount(Long workerId, Date date, Long periodId, Long currencyId, BigDecimal charged, BigDecimal paid, BigDecimal withdrawn) {
        List<Account> accounts = domainService.getDomains(Account.class, FilterWrapper.of(new Account().setWorkerId(workerId).setPeriodId(periodId)));

        Account account;

        if (!accounts.isEmpty()){
            account = accounts.get(0);
        } else {
            account = new Account();

            account.setWorkerId(workerId);
            account.setPeriodId(periodId);
            account.setCurrencyId(currencyId);
            account.setBalance(BigDecimal.ZERO);
        }

        account.setDate(date);

        account.setCharged(charged);
        account.setPaid(paid);
        account.setWithdrawn(withdrawn);

        domainService.save(account);

        return account;
    }

    public void openAccount(Long workerId, Date date, Long periodId, Long currencyId, BigDecimal balance) {
        Account account = new Account();

        account.setWorkerId(workerId);
        account.setDate(date);
        account.setPeriodId(periodId);
        account.setCurrencyId(currencyId);
        account.setBalance(balance);

        domainService.save(account);
    }

    @Transactional(rollbackFor = AccountException.class)
    public void updateAccounts() throws AccountException {
        try {
            Period previousPeriod = periodMapper.getPeriod(Dates.previusMonth(periodMapper.getActualPeriod().getOperatingMonth()));

            Map<Long, List<Reward>> rewardMap = rewardMapper.getRewards(FilterWrapper.of(new Reward().setPeriodId(previousPeriod.getObjectId())))
                    .stream()
                    .collect(Collectors.groupingBy(Reward::getWorkerId));

            Date date = Dates.currentDate();

            rewardMap.forEach((workerId, rewards) -> {
                BigDecimal charged = ZERO;
                BigDecimal withdrawn = ZERO;
                BigDecimal spent = ZERO;

                Long currencyId = workerService.getCurrencyId(workerId);

                for (Reward reward : rewards) {
                    if (reward.getRewardStatus() == RewardStatus.CHARGED){
                        charged = charged.add(reward.getAmount());
                    } else if (reward.getRewardStatus() == RewardStatus.WITHDRAWN){
                        withdrawn = withdrawn.add(reward.getAmount());
                    }
                }

                BigDecimal paid = payoutMapper.getPayouts(FilterWrapper.of(new Payout()
                        .setPeriodId(previousPeriod.getObjectId())
                        .setCurrencyId(currencyId)))
                        .stream()
                        .map(Payout::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Account account = updateAccount(workerId, date, previousPeriod.getObjectId(), currencyId, charged, paid, withdrawn);

                BigDecimal balance = account.getBalance().add(charged).subtract(paid).subtract(spent);

                openAccount(workerId, date, periodMapper.getActualPeriod().getObjectId(), currencyId, balance);
            });
        } catch (Exception e) {
            log.error("error update account", e);

            throw new AccountException(e.getMessage());
        }
    }

    public BigDecimal getBalance(Long workerId, Long periodId){
        List<Account> accounts = domainService.getDomains(Account.class,
                FilterWrapper.of(new Account()
                        .setWorkerId(workerId)
                        .setPeriodId(periodId)));

        return !accounts.isEmpty() ? accounts.get(0).getBalance() : BigDecimal.ZERO;
    }

    public BigDecimal getCharged(Long periodId, Long currencyId) {
        return domainService.getDomains(Account.class, FilterWrapper.of(new Account()
                .setPeriodId(periodId)
                .setCurrencyId(currencyId)))
                .stream()
                .map(Account::getCharged)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPaid(Long periodId, Long currencyId) {
        return domainService.getDomains(Account.class, FilterWrapper.of(new Account()
                .setPeriodId(periodId)
                .setCurrencyId(currencyId)))
                .stream()
                .map(Account::getPaid)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPaid(Long periodId, Long currencyId, Long workerId) {
        return payoutMapper.getPayouts(FilterWrapper.of(new Payout()
                .setWorkerId(workerId)
                .setPeriodId(periodId)
                .setCurrencyId(currencyId)))
                .stream()
                .map(Payout::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
