package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.jedani.worker.entity.Reward;

/**
 * @author Anatoly A. Ivanov
 * 16.07.2019 17:27
 */
public class ModalContainer<T> extends Modal<T> {
    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private SerializableConsumer<AjaxRequestTarget> onUpdate;

    public ModalContainer(String markupId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId);

        this.onUpdate = onUpdate;

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
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
        target.add(getContainer());

        appendShowDialogJavaScript(target);
    }

    public void edit(Reward object, AjaxRequestTarget target) {
        target.add(getContainer());

        appendShowDialogJavaScript(target);
    }

    protected void save(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent)c).clearInput());

        if (onUpdate != null) {
            onUpdate.accept(target);
        }
    }

    protected void cancel(AjaxRequestTarget target) {
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent)c).clearInput());
    }
}
