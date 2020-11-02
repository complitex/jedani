package ru.complitex.jedani.worker.page.worker;

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
import ru.complitex.jedani.worker.service.InviteService;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 30.04.2019 21:43
 */
public class WorkerInviteModal extends Modal<Worker> {
    @Inject
    private InviteService inviteService;

    private final IModel<Worker> workerModel = Model.of(new Worker());

    private final Component key;

    public WorkerInviteModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);

        header(new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return getString("header");
            }
        });

        add(key = new Label("key", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return "https://stru.jedani-mycook.com/invite/" + inviteService.encodeKey(workerModel.getObject().getJId());
            }
        }).setOutputMarkupId(true));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void invite(AjaxRequestTarget target, Worker worker){
        workerModel.setObject(worker);

        target.add(key);

        appendShowDialogJavaScript(target);
    }
}
