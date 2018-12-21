package ru.complitex.jedani.worker.service;

import org.apache.wicket.util.lang.Objects;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.service.DomainNodeService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.name.service.NameService;
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
    private WorkerMapper workerMapper;

    @Inject
    private DomainNodeService domainNodeService;

    @Inject
    private NameService nameService;


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

    public String getWorkerLabel(Long workerId){
        if (workerId == null){
            return "";
        }

        return getWorkerLabel(workerMapper.getWorker(workerId));
    }

    public String getWorkerLabel(Domain worker){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + " " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME))) + " " +
                Attributes.capitalize(nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME))) + " " +
                Attributes.capitalize(nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME)));
    }

    public String getSimpleWorkerLabel(Long workerId){
        if (workerId == null){
            return "";
        }

        return getSimpleWorkerLabel(workerMapper.getWorker(workerId));
    }

    public String getSimpleWorkerLabel(Domain worker){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + " " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME)));
    }
}
