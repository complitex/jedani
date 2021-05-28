package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerType;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2018 14:58
 */
public class WorkerAutoComplete extends AbstractDomainAutoComplete<Worker> {
    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private WorkerService workerService;

    public WorkerAutoComplete(String id, IModel<Long> model) {
        super(id, Worker.class,  model);
    }

    public WorkerAutoComplete(String id, IModel<Worker> domainModel, Long entityAttributeId){
        this(id, NumberAttributeModel.of(domainModel, entityAttributeId));
    }

    @Override
    protected Worker getDomain(Long objectId) {
        return workerMapper.getWorker(objectId);
    }

    @Override
    protected Worker getFilterObject(String input) {
        return null;
    }

    @Override
    protected List<Worker> getDomains(String input) {
        return workerMapper.getWorkers(input, getWorkerType());
    }

    @Override
    protected String getTextValue(Worker worker) {
        return workerService.getWorkerLabel(worker);
    }

    protected Long getWorkerType() {
        return WorkerType.PK;
    }
}
