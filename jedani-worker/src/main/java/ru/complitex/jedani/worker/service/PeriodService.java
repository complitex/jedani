package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.mapper.PeriodMapper;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 09.11.2019 5:30 PM
 */
public class PeriodService {
    @Inject
    private DomainService domainService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardService rewardService;

    public boolean hasPeriod(Period period){
        return periodMapper.hasPeriod(period);
    }

    @Transactional
    public void save(Period period){
        domainService.save(period);

        //todo reward service calc rewards
        //todo commit rollback



        period.setPeriodClose(Dates.currentDate());

        domainService.save(period);
    }

}
