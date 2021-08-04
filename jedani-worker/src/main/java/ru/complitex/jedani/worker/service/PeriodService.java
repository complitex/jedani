package ru.complitex.jedani.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.exception.AccountException;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 09.11.2019 5:30 PM
 */
public class PeriodService implements Serializable {
    private Logger log = LoggerFactory.getLogger(PeriodService.class);

    @Inject
    private DomainService domainService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardService rewardService;

    @Inject
    private AccountService accountService;

    public void closeOperatingMonth(Period period, boolean calculateRewards, boolean updateAccounts, Long workerId) throws RewardException, AccountException {
        period.setWorkerId(workerId);
        period.setCloseTimestamp(Dates.currentDate());

        domainService.save(period);

        Period actualPeriod = new Period();

        actualPeriod.setOperatingMonth(Dates.nextMonth(period.getOperatingMonth()));

        domainService.save(actualPeriod);

        if (calculateRewards){
            rewardService.calculateRewards();
        }

        if (updateAccounts){
            accountService.updateAccounts();
        }
    }

    public List<Period> getPeriods(){
        return periodMapper.getPeriods();
    }

}
