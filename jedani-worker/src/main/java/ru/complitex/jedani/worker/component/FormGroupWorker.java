package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.jedani.worker.entity.Worker;

public class FormGroupWorker extends FormGroupPanel {
    public FormGroupWorker(String id, IModel<Worker> workerModel, Long entityAttributeId) {
        super(id, new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID, workerModel, entityAttributeId));
    }

    public FormGroupWorker(String id, IModel<Long> workerIdModel) {
        super(id, new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID, workerIdModel));
    }
}
