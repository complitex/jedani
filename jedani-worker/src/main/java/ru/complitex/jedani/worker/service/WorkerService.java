package ru.complitex.jedani.worker.service;

import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.service.DomainNodeService;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.Serializable;

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

    @Inject
    private DomainNodeService domainNodeService;


    public Worker getWorker(String login){
        Long userId = userMapper.getUserId(login);

        if (userId != null){
            return  workerMapper.getWorkerByUserId(userId);
        }

        return null;
    }

    public void rebuildIndex(){
        domainNodeService.rebuildRootIndex(Worker.ENTITY_NAME, 1L, Worker.MANAGER_ID);
    }

    public void moveIndex(Worker manager, Worker worker){
        if (!worker.getId().equals(manager.getId()) && (worker.getLeft() >= manager.getLeft() ||
                worker.getRight() <= manager.getRight())) {
            domainNodeService.move(manager, worker);
        } else {
            rebuildIndex();
        }
    }
}
