package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.domain.component.form.AbstractDomainAutoComplete;
import ru.complitex.domain.entity.Domain;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 27.06.2018 14:58
 */
public class WorkerAutoComplete extends AbstractDomainAutoComplete {
    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private WorkerService workerService;

    public WorkerAutoComplete(String id, IModel<Long> model, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(id, null,  model, onChange);
    }

    public WorkerAutoComplete(String id, IModel<Long> model) {
        this(id,  model, null);
    }

    @Override
    protected Domain getDomain(Long objectId) {
        return workerMapper.getWorker(objectId);
    }

    @Override
    protected Domain getFilterObject(String input) {
        return null;
    }

    @Override
    protected List<? extends Domain> getDomains(String input) {
        return workerMapper.getWorkers(input);
    }

    @Override
    protected String getTextValue(Domain domain) {
        return workerService.getWorkerLabel(domain);
    }
}
