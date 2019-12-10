package ru.complitex.jedani.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.ExchangeRate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Anatoly A. Ivanov
 * 29.03.2019 20:13
 */
@ApplicationScoped
public class ExchangeRateSchedulerService {
    private Logger log = LoggerFactory.getLogger(getClass());

    private ScheduledFuture future;

    @Inject
    private DomainService domainService;

    @Inject
    private ExchangeRateService exchangeRateService;

    private Map<ExchangeRate, LocalDateTime> updatedMap = new HashMap<>();

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        List<ExchangeRate> exchangeRates = domainService.getDomains(ExchangeRate.class, FilterWrapper.of(new ExchangeRate()));

        future = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            exchangeRates.forEach(exchangeRate -> {
                try {
                    LocalDateTime updated = updatedMap.get(exchangeRate);

                    if (updated == null || LocalDateTime.now().isAfter(updated.plusDays(1))){
                        if (exchangeRateService.loadValues(exchangeRate)){
                            updated = LocalDateTime.now().with(LocalTime.MIN);

                            updatedMap.put(exchangeRate, updated);

                            log.info("exchange rate updated {} {}", updated, exchangeRate.getName());
                        }
                    }
                } catch (Exception e) {
                    log.error("error update exchange rates ", e);
                }
            });
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        future.cancel(false);
    }
}
