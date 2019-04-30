package ru.complitex.jedani.worker.page.worker;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerType;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 30.04.2019 21:43
 */
public class WorkerRemoveModal extends Modal<Worker> {
    @Inject
    private WorkerService workerService;

    private IModel<Worker> workerModel;

    private Component workerLabel;

    public WorkerRemoveModal(String markupId) {
        super(markupId);

        workerModel = Model.of(new Worker());

        setBackdrop(Backdrop.FALSE);

        header(new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return getString(Objects.equals(workerModel.getObject().getType(), WorkerType.USER)
                        ? "header_user" : "header_participant");
            }
        });

        add(workerLabel= new Label("worker", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return getString(Objects.equals(workerModel.getObject().getType(), WorkerType.USER)
                        ? "delete_user" : "delete_participant") + " " +
                        workerService.getWorkerLabel(workerModel.getObject());
            }
        }).setOutputMarkupId(true));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                workerService.delete(workerModel.getObject());

                getSession().success(getString("info_deleted"));

                appendCloseDialogJavaScript(target);

                WorkerRemoveModal.this.onUpdate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                getSession().error(getString("error_deleted"));

                WorkerRemoveModal.this.onUpdate(target);

                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("delete")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void delete(AjaxRequestTarget target, Worker worker){
        workerModel.setObject(worker);

        target.add(workerLabel, get("dialog:header:header-label"));

        appendShowDialogJavaScript(target);
    }

    protected void onUpdate(AjaxRequestTarget target){}

}
