package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import ru.complitex.jedani.worker.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 14:10
 */
abstract class StorageAbstractModal extends Modal<Transaction> {
    private List<Component> ajaxUpdateComponents = new ArrayList<>();

    private FeedbackPanel feedback;

    private BootstrapAjaxButton actionButton;

    StorageAbstractModal(String markupId) {
        super(markupId, Model.of(new Transaction()));

        header(Model.of(getString("header")));
        setVisible(false);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        addButton(actionButton = new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                StorageAbstractModal.this.action();

                StorageAbstractModal.this.close(target);

                ajaxUpdateComponents.forEach(target::add);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        }.setLabel(Model.of(getString("action"))));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                StorageAbstractModal.this.close(target);
            }
        }.setLabel(Model.of(getString("cancel"))));
    }

    void open(AjaxRequestTarget target){
        getModel().setObject(new Transaction());

        setVisible(true);
        target.add(this);
        appendShowDialogJavaScript(target);
    }

    private void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);
    }

    abstract void action();

    void addAjaxUpdate(Component component){
        ajaxUpdateComponents.add(component);
    }

    public FeedbackPanel getFeedback() {
        return feedback;
    }

    public BootstrapAjaxButton getActionButton() {
        return actionButton;
    }
}
