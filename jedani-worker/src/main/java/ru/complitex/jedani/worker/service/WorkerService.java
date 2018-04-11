package ru.complitex.jedani.worker.service;

import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2018 15:19
 */
public class WorkerService {
    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private DomainMapper domainMapper;

    public List<Worker> getWorkerTree(Long workerId){


        return null;
    }

    public Worker getWorker(String login){
        Long userId = userMapper.getUserId(login);

        if (userId != null){
            Domain domain = domainMapper.getDomainByParentId("worker", userId);

            if (domain != null){
                return new Worker(domain);
            }
        }

        return null;
    }
}
