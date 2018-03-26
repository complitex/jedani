package ru.complitex.jedani.worker.service;

import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2018 15:19
 */
public class WorkerService {
    @Inject
    private WorkerMapper workerMapper;

    List<Worker> getWorkerTree(Long workerId){


        return null;
    }
}
