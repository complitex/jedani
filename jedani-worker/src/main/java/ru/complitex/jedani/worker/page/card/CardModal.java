package ru.complitex.jedani.worker.page.card;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Card;
import ru.complitex.jedani.worker.service.CardService;

import javax.inject.Inject;

public class CardModal extends Modal<Card> {
    @Inject
    private CardService cardService;

    private IModel<Card> cardModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    public CardModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);

        header(new ResourceModel("header"));

        cardModel = Model.of(new Card());

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        container.add(new FormGroupTextField<>("number", TextAttributeModel.of(cardModel, Card.NUMBER)).setRequired(true));
        container.add(new FormGroupDateTextField("date", DateAttributeModel.of(cardModel, Card.DATE)).setRequired(true));
        container.add(new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(cardModel, Card.WORKER)).setRequired(true)));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                CardModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CardModal.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void create(AjaxRequestTarget target){
        cardModel.setObject(cardService.createCard());

        target.add(container);

        appendShowDialogJavaScript(target);
    }

    public void edit(Long cardId, AjaxRequestTarget target){
        Card card = cardService.getCard(cardId);

        cardModel.setObject(card);

        target.add(container);

        appendShowDialogJavaScript(target);
    }

    private void save(AjaxRequestTarget target){
        Card card = cardModel.getObject();

        if (cardService.isWorkerExists(card.getWorkerId())){
            error(getString("error_worker_exists"));

            target.add(feedback);

            return;
        }

        if (!cardService.isValid(card.getNumber())){
            error(getString("error_not_valid"));

            target.add(feedback);

            return;
        }

        if (cardService.isExists(card)){
            error(getString("error_exists"));

            target.add(feedback);

            return;
        }

        if (!cardService.isSame(card.getNumber(), card.getIndex())){
            card.setIndex(null);
        }

        cardService.save(card);

        getSession().success(getString("info_card_saved"));

        appendCloseDialogJavaScript(target);

        onUpdate(target);
    }

    private void cancel(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);
    }

    protected void onUpdate(AjaxRequestTarget target){

    }

}
