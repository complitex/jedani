package ru.complitex.domain.component.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public class ModalPanel<T> extends Panel {
    private Modal<T> modal;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    public ModalPanel(String markupId) {
        super(markupId);

        Form form = new Form("form");
        add(form);

        modal = new Modal<T>("modal");
        modal.setBackdrop(Modal.Backdrop.FALSE);
        modal.header(new ResourceModel("header"));
        form.add(modal);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        modal.add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        modal.addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ModalPanel.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        modal.addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ModalPanel.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));

    }

    public WebMarkupContainer getContainer() {
        return container;
    }

    protected void save(AjaxRequestTarget target) {

    }

    protected void cancel(AjaxRequestTarget target) {

    }
}
