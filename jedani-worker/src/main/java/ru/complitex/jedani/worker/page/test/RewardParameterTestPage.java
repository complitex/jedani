package ru.complitex.jedani.worker.page.test;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.page.BasePage;
import ru.complitex.jedani.worker.service.cache.RewardParameterCacheService;

import javax.inject.Inject;
import java.util.stream.LongStream;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation(ADMINISTRATORS)
public class RewardParameterTestPage extends BasePage {
    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private RewardParameterCacheService parameterCacheService;

    public RewardParameterTestPage() {
        StringBuilder stringBuilder = new StringBuilder();

        periodMapper.getPeriods().forEach(period -> {
            LongStream.range(1, 60).forEach(l ->
                    stringBuilder
                            .append(l)
                            .append(" ")
                            .append(period.getObjectId())
                            .append(" ")
                            .append(parameterCacheService.getParameter(l, period.getObjectId()))
                            .append("</br>"));

            stringBuilder.append("</br>");
        });

        add(new Label("test", stringBuilder.toString()).setEscapeModelStrings(false));
    }
}
