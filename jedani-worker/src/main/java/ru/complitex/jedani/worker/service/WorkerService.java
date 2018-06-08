package ru.complitex.jedani.worker.service;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2018 15:19
 */
public class WorkerService implements Serializable {

    @Inject
    private UserMapper userMapper;

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private WorkerMapper workerMapper;

    public Worker getWorker(String login){
        Long userId = userMapper.getUserId(login);

        if (userId != null){
            return  workerMapper.getWorkerByUserId(userId);
        }

        return null;
    }

    public String getWorkerFio(Domain worker, Locale locale){
        return "" +
                domainMapper.getDomain(LastName.ENTITY_NAME, worker.getAttribute(Worker.LAST_NAME).getNumber())
                        .getValueText(LastName.NAME, locale) + " " +
                domainMapper.getDomain(FirstName.ENTITY_NAME, worker.getAttribute(Worker.FIRST_NAME).getNumber())
                        .getValueText(FirstName.NAME, locale) + " " +
                domainMapper.getDomain(MiddleName.ENTITY_NAME, worker.getAttribute(Worker.MIDDLE_NAME).getNumber())
                        .getValueText(MiddleName.NAME, locale);
    }
}
