package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.jedani.worker.page.BasePage;

import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 16.07.2019 17:27
 */
public abstract class AbstractEditModal<T> extends Modal<T> {
    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private SerializableConsumer<AjaxRequestTarget> onUpdate;

    public AbstractEditModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        super.add(container);

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

    public void edit(T object, AjaxRequestTarget target) {
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

    @SuppressWarnings("unchecked")
    public <M extends AbstractEditModal> M onUpdate(SerializableConsumer<AjaxRequestTarget> onUpdate) {
        this.onUpdate = onUpdate;

        return (M) this;
    }

    public SerializableConsumer<AjaxRequestTarget> getOnUpdate() {
        return onUpdate;
    }

    public MarkupContainer add(Component... children){
        if (container != null) {
            container.add(children);
        } else {
            super.add(children);
        }

        return this;
    }

    protected BasePage getBasePage(){
        Page page = getPage();

        if (page instanceof BasePage){
            return ((BasePage) page);
        }

        return null;
    }

    protected boolean isAdmin(){
        return Objects.requireNonNull(getBasePage()).isAdmin();
    }

    protected boolean isStructureAdmin(){
        return Objects.requireNonNull(getBasePage()).isStructureAdmin();
    }
}
