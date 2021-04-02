package ru.complitex.jedani.worker.service;

import org.apache.wicket.util.lang.Objects;
import org.mybatis.cdi.Transactional;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Country;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.service.DomainService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2018 15:19
 */
public class WorkerService implements Serializable {

    @Inject
    private UserMapper userMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private WorkerNodeService workerNodeService;

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

    public Worker getWorkerByJId(String JId){
        List<Worker> list = workerMapper.getWorkers(FilterWrapper.of(new Worker().setJId(JId)).setFilter(FilterWrapper.FILTER_EQUAL));

        if (!list.isEmpty()){
            return list.get(0);
        }

        return null;
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

    public String getLastName(Worker worker){
        return Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME)));
    }

    public String getFirstName(Worker worker){
        return Attributes.capitalize(nameService.getFirstName(worker.getNumber(Worker.FIRST_NAME)));
    }

    public String getMiddleName(Worker worker){
        return Attributes.capitalize(nameService.getMiddleName(worker.getNumber(Worker.MIDDLE_NAME)));
    }

    public String getWorkerFio(Worker worker){
        if (worker == null){
            return "";
        }

        return getLastName(worker) + " " + getFirstName(worker) + " " + getMiddleName(worker);
    }

    public List<String> getRegions(Worker worker){
        City city = domainService.getDomain(City.class, worker.getCityId());

        String regionName = domainService.getTextValue(Region.ENTITY_NAME, city.getParentId(), Region.NAME);

        return Collections.singletonList(regionName);
    }

    public List<String> getCities(Worker worker){
        City city = domainService.getDomain(City.class, worker.getCityId());

        String cityName = domainService.getTextValue(City.ENTITY_NAME, city.getObjectId(), City.NAME);

        return Collections.singletonList(cityName);
    }

    public String getSimpleWorkerLabel(Long workerId){
        if (workerId == null){
            return "";
        }

        return getSimpleWorkerLabel(workerMapper.getWorker(workerId));
    }

    public String getSimpleWorkerLabel(Domain<?> worker){
        return Objects.defaultIfNull(worker.getText(Worker.J_ID), "") + ", " +
                Attributes.capitalize(nameService.getLastName(worker.getNumber(Worker.LAST_NAME)));
    }

    public void save(Worker worker){
        if (worker.getObjectId() == null) {
            domainService.insert(worker);
        }else {
            domainService.update(worker);
        }
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
                    domainService.save(w);
                });

        workerMapper.getWorkers(FilterWrapper.of(new Worker().setManagerId(worker.getObjectId())
                .setStatus(Status.ARCHIVE))).forEach(w -> {
            w.setManagerId(worker.getManagerId());
            w.setWorkerStatus(WorkerStatus.MANAGER_CHANGED);
            domainService.save(w);
        });

        worker.setEndDate(Dates.currentDate());
        worker.setStatus(Status.ARCHIVE);

        domainService.save(worker);

        workerNodeService.rebuildIndex();
    }

    @Transactional
    public void save(User user, Long workerId){
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

    public String getNewJId(){
        return workerMapper.getNewJId();
    }

    public boolean isExistJId(String jId){
        return workerMapper.isExistJId(null, jId);
    }

    public User getUser(Worker worker){
        return userMapper.getUser(worker.getParentId());
    }

    public Long getCurrencyId(Long workerId) {
        Long cityId = domainService.getNumber(Worker.ENTITY_NAME, workerId, Worker.CITY);

        City city = domainService.getDomain(City.class, cityId);

        Region region = domainService.getDomain(Region.class, city.getParentId());

        return domainService.getDomain(Country.class, region.getParentId()).getNumber(Country.CURRENCY);
    }

    public Long getRegionId(Worker worker) {
        return domainService.getParentId(City.ENTITY_NAME, worker.getCityId());
    }
}
