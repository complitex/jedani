package ru.complitex.jedani.worker.page.worker;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.domain.service.DomainService;
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

    @Inject
    private DomainService domainService;

    private final IModel<Worker> workerModel = Model.of(new Worker());

    private final Component key;
    private final Component copy;

    public WorkerInviteModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);

        header(new LoadableDetachableModel<>() {
            @Override
            protected String load() {
                return getString("header");
            }
        });

        IModel<String> inviteModel = new LoadableDetachableModel<>() {
            @Override
            protected String load() {
                return "https://stru.jedani-mycook.com/invite/" + inviteService.encodeKey(workerModel.getObject().getJId());
            }
        };

        add(key = new Label("key", inviteModel).setOutputMarkupId(true));

        add(copy = new WebMarkupContainer("copy"){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                tag.put("onclick", "copyToClipboard('" + inviteModel.getObject()  +"')");
            }
        }.setOutputMarkupId(true));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("ok")));
    }

    public void invite(Long workerId, AjaxRequestTarget target){
        Worker worker = domainService.getDomain(Worker.class, workerId);

        workerModel.setObject(worker);

        target.add(key, copy);

        appendShowDialogJavaScript(target);
    }
}
