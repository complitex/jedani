package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.wicket.util.Wickets;
import ru.complitex.jedani.worker.entity.RecipientType;
import ru.complitex.jedani.worker.entity.Transaction;

/**
 * @author Anatoly A. Ivanov
 * 06.11.2018 14:10
 */
abstract class StorageModal extends Modal<Transaction> {
    private FeedbackPanel feedback;
    private WebMarkupContainer container;

    private BootstrapAjaxButton actionButton;

    private Long storageId;

    private SerializableConsumer<AjaxRequestTarget> onUpdate;

    StorageModal(String markupId, Long storageId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, Model.of(new Transaction()));

        this.storageId = storageId;
        this.onUpdate = onUpdate;

        setBackdrop(Backdrop.FALSE);

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        addButton(actionButton = new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                StorageModal.this.action(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                container.visitChildren(((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(Wickets.getAjaxParent(object));
                    }
                }));
            }
        }.setLabel(new ResourceModel("action")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                StorageModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    void open(AjaxRequestTarget target){
        Transaction transaction = getModelObject();

        transaction.getAttributes().clear();

        transaction.setNumber(Transaction.QUANTITY, 1L);
        transaction.setNumber(Transaction.RECIPIENT_TYPE, RecipientType.WORKER);

        container.setVisible(true);
        target.add(container);
        appendShowDialogJavaScript(target);
    }

    void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent)c).clearInput());
    }

    abstract void action(AjaxRequestTarget target); //todo move action impl

    public WebMarkupContainer getContainer() {
        return container;
    }

    FeedbackPanel getFeedback() {
        return feedback;
    }

    BootstrapAjaxButton getActionButton() {
        return actionButton;
    }

    @Override
    protected String createBasicInitializerScript(String markupId) {
        if (getFocusMarkupId() != null) {
            String focus = "; " +
                    "$('#"+ markupId + "').on('shown.bs.modal', function () {" +
                    "  $('#" + getFocusMarkupId() + "').focus();" +
                    "})";

            return super.createBasicInitializerScript(markupId) + focus;
        }

        return super.createBasicInitializerScript(markupId);
    }

    protected String getFocusMarkupId() {
        return null;
    }

    protected void onUpdate(AjaxRequestTarget target){
        if (onUpdate != null){
            onUpdate.accept(target);
        }
    }
}
