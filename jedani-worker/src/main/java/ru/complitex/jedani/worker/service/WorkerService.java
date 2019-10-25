package ru.complitex.jedani.worker.service;

import org.apache.wicket.util.lang.Objects;
import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.service.DomainNodeService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.UserHistory;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerStatus;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.name.service.NameService;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.UUID;

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

    @Inject
    private NameService nameService;


    public Worker getWorker(String login){
        Long userId = userMapper.getUserId(login);

        if (userId != null){
            return  workerMapper.getWorkerByUserId(userId);
        }

        return null;
    }

    public Worker getWorker(Long workerId){
        return workerMapper.getWorker(workerId);
    }

    @Transactional
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
        if (worker == null){
            return "";
        }

        return ((worker.getText(Worker.J_ID) != null ? worker.getText(Worker.J_ID) + ", " : "") +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME))) + " " +
                Attributes.capitalize(nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME))) + " " +
                Attributes.capitalize(nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME)))).trim();
    }

    public String getSimpleWorkerLabel(Long workerId){
        if (workerId == null){
            return "";
        }

        return getSimpleWorkerLabel(workerMapper.getWorker(workerId));
    }

    public String getSimpleWorkerLabel(Domain worker){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + ", " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME)));
    }

    @Transactional
    public void delete(Worker worker){
        User user = userMapper.getUser(worker.getParentId());
        user.setPassword(UUID.randomUUID().toString());
        userMapper.updateUserPassword(user);

        workerMapper.getWorkers(FilterWrapper.of(new Worker().setManagerId(worker.getObjectId())))
                .forEach(w -> {
                    w.setManagerId(worker.getManagerId());
                    w.setWorkerStatus(WorkerStatus.MANAGER_CHANGED);
                    domainMapper.updateDomain(w);
                });

        workerMapper.getWorkers(FilterWrapper.of(new Worker().setManagerId(worker.getObjectId())
                .setStatus(Status.ARCHIVE))).forEach(w -> {
            w.setManagerId(worker.getManagerId());
            w.setWorkerStatus(WorkerStatus.MANAGER_CHANGED);
            domainMapper.updateDomain(w);
        });

        worker.setStatus(Status.ARCHIVE);
        domainMapper.updateDomain(worker);

        rebuildIndex();
    }

    @Transactional
    public void insert(User user, Long workerId){
        userMapper.insert(user);

        workerMapper.insert(new UserHistory(user, workerId));
    }

    @Transactional
    public void updateUserLogin(User user, Long workerId){
        userMapper.updateUserLogin(user);

        workerMapper.insert(new UserHistory(user.getId(), workerId).setLogin(user.getLogin()));
    }

    @Transactional
    public void updateUserPassword(User user, Long workerId){
        userMapper.updateUserPassword(user);

        workerMapper.insert(new UserHistory(user.getId(), workerId).setPassword(user.getPassword()));
    }

    @Transactional
    public void updateUserGroups(User user, Long workerId){
        userMapper.updateUserGroups(user);

        workerMapper.insert(new UserHistory(user.getId(), workerId).setUserGroups(user.getUserGroups()));
    }

    public boolean isExistJId(String jId){
        return workerMapper.isExistJId(null, jId);
    }
}
