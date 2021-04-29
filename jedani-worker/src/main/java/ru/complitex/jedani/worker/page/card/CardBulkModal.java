package ru.complitex.jedani.worker.page.card;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.jedani.worker.entity.Card;
import ru.complitex.jedani.worker.service.CardService;

import javax.inject.Inject;
import java.util.Date;

public class CardBulkModal extends Modal<Card> {
    @Inject
    private CardService cardService;

    private IModel<Long> countModel;
    private IModel<Date> dateModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    public CardBulkModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);

        header(new ResourceModel("header"));

        countModel = new Model<>();
        dateModel = new Model<>();

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        container.add(new FormGroupTextField<>("count", countModel, Long.class).setRequired(true));
        container.add(new FormGroupDateTextField("date", dateModel).setRequired(true));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                CardBulkModal.this.generate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("generate")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CardBulkModal.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void open(AjaxRequestTarget target){
        countModel.setObject(null);
        dateModel.setObject(Dates.currentDate());

        target.add(container);

        appendShowDialogJavaScript(target);
    }

    private void generate(AjaxRequestTarget target){
        cardService.generate(countModel.getObject(), dateModel.getObject());

        getSession().success(getString("info_generated"));

        appendCloseDialogJavaScript(target);

        onUpdate(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());
    }

    private void cancel(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());
    }

    protected void onUpdate(AjaxRequestTarget target){

    }

}
