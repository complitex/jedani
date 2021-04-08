package ru.complitex.jedani.worker.component;

import org.apache.wicket.model.IModel;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.jedani.worker.entity.Worker;

public class FormGroupWorker extends FormGroupPanel {
    public FormGroupWorker(String id, IModel<Worker> domainModel, Long entityAttributeId) {
        super(id, new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID, domainModel, entityAttributeId));
    }
}
