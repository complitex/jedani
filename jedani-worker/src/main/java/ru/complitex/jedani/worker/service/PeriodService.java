package ru.complitex.jedani.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
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
    private Logger log = LoggerFactory.getLogger(PeriodService.class);

    @Inject
    private DomainService domainService;

    @Inject
    private PeriodMapper periodMapper;

    public boolean hasPeriods(){
        return domainService.getDomainsCount(FilterWrapper.of(new Period())) > 0;
    }

    public boolean hasPeriod(Period period){
        return periodMapper.hasPeriod(period);
    }

    public void save(Period period){
        domainService.save(period);
    }

    public Period getActualPeriod(){
        return periodMapper.getActualPeriod();
    }

    public void closeOperatingMonth(Long workerId){
        Period period = getActualPeriod();

        period.setWorkerId(workerId);
        period.setCloseTimestamp(Dates.currentDate());

        save(period);


        Period actualPeriod = new Period();

        actualPeriod.setOperatingMonth(Dates.nextMonth(period.getOperatingMonth()));

        save(actualPeriod);
    }
}
