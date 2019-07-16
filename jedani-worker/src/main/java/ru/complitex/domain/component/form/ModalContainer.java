package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.jedani.worker.entity.Reward;

/**
 * @author Anatoly A. Ivanov
 * 16.07.2019 17:27
 */
public class ModalContainer<T> extends Modal<T> {
    private WebMarkupContainer container;
    private NotificationPanel feedback;

    public ModalContainer(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public WebMarkupContainer getContainer() {
        return container;
    }

    public NotificationPanel getFeedback() {
        return feedback;
    }

    public void create(AjaxRequestTarget target) {
        appendShowDialogJavaScript(target);
    }

    public void edit(Reward object, AjaxRequestTarget target) {
        appendShowDialogJavaScript(target);
    }

    protected void save(AjaxRequestTarget target) {

    }

    protected void cancel(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);
    }
}
