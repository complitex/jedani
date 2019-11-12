package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.exception.PeriodException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.SaleMapper;

import javax.inject.Inject;
import java.math.BigDecimal;

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

    @Inject
    private SaleMapper saleMapper;

    @Inject
    private PaymentService paymentService;

    @Inject
    private RewardService rewardService;

    public boolean hasPeriod(Period period){
        return periodMapper.hasPeriod(period);
    }

    @Transactional(rollbackFor = PeriodException.class)
    public void save(Period period) throws PeriodException {
        try {
            domainService.save(period);

            saleMapper.getSales(FilterWrapper.of(new Sale()).put(Sale.FILTER_ACTIVE, true)).forEach(s -> {
                if (s.getPersonalRewardPoint() != null){
                    Reward reward = rewardService.getPersonalReward(s, s.getPersonalRewardPoint());

                    if (reward.isAccrued()){
                        reward.setDate(Dates.currentDate());
                        reward.setPeriodId(period.getObjectId());

                        domainService.save(reward);
                    }
                }

                if (s.getMkManagerBonusRewardPoint() != null){
                    Reward reward = rewardService.getMkManagerBonusReward(s, s.getMkManagerBonusRewardPoint());

                    if (reward.isAccrued()){
                        reward.setDate(Dates.currentDate());
                        reward.setPeriodId(period.getObjectId());

                        domainService.save(reward);
                    }
                }

                Reward personalVolumeReward = rewardService.getPersonalVolumeReward(s, period.getOperatingMonth());

                if (personalVolumeReward.getPoint().compareTo(BigDecimal.ZERO) > 0){
                    personalVolumeReward.setDate(Dates.currentDate());
                    personalVolumeReward.setPeriodId(period.getObjectId());

                    domainService.save(personalVolumeReward);
                }

                Reward culinaryWorkshopReward = rewardService.getCulinaryWorkshopReward(s);

                if (culinaryWorkshopReward.isAccrued()){
                    culinaryWorkshopReward.setDate(Dates.currentDate());
                    culinaryWorkshopReward.setPeriodId(period.getObjectId());

                    domainService.save(culinaryWorkshopReward);
                }
            });

            period.setPeriodClose(Dates.currentDate());

            domainService.save(period);
        } catch (Exception e) {
            log.error("period service error ", e);

            throw new PeriodException(e.getMessage());
        }
    }

}
