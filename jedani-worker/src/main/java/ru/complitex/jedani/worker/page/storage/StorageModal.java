package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.wicket.util.ComponentUtil;
import ru.complitex.jedani.worker.entity.Transaction;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 14:10
 */
abstract class StorageModal extends Modal<Transaction> {
    private FeedbackPanel feedback;

    private BootstrapAjaxButton actionButton;

    StorageModal(String markupId) {
        super(markupId, Model.of(new Transaction()));

        header(LoadableDetachableModel.of(() -> getString("header")));
        setVisible(false);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        addButton(actionButton = new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                StorageModal.this.action(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                StorageModal.this.visitChildren(((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(ComponentUtil.getAjaxParent(object));
                    }
                }));

//                target.add(feedback);
            }
        }.setLabel(LoadableDetachableModel.of(() -> getString("action"))));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                StorageModal.this.close(target);
            }
        }.setLabel(LoadableDetachableModel.of(() -> getString("cancel"))));
    }

    void open(AjaxRequestTarget target){
        Transaction transaction = new Transaction();
        transaction.setNumber(Transaction.QUANTITY, 1L);
        setModelObject(transaction);

        setVisible(true);
        target.add(this);
        appendShowDialogJavaScript(target);
    }

    abstract void action(AjaxRequestTarget target);

    FeedbackPanel getFeedback() {
        return feedback;
    }

    BootstrapAjaxButton getActionButton() {
        return actionButton;
    }
}
