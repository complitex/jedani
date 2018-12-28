package ru.complitex.jedani.worker.page.promotion;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.Country;
import ru.complitex.common.wicket.form.DateTextFieldFormGroup;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.common.wicket.util.ComponentUtil;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextValueModel;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.entity.Promotion;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 20:01
 */
public class PromotionModal extends Modal<Promotion> {
    private WebMarkupContainer container;

    public PromotionModal(String markupId) {
        super(markupId, new Model<>());

        header(new ResourceModel("headerCreate"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(false);
        add(container);

        NotificationPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        container.add(new DateTextFieldFormGroup("begin", new DateAttributeModel(getModel(), Promotion.BEGIN)));
        container.add(new DateTextFieldFormGroup("end", new DateAttributeModel(getModel(), Promotion.END)));
        container.add(new DomainAutoCompleteFormGroup("country", Country.ENTITY_NAME, Country.NAME,
                new NumberAttributeModel(getModel(), Promotion.COUNTRY)).setRequired(true));
        container.add(new TextFieldFormGroup<>("name", new TextValueModel(getModel(), Promotion.NAME,
                Locales.getSystemLocaleId())).setRequired(true));
        //todo file, nomenclatures, eur



        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                PromotionModal.this.action(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                container.visitChildren(((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(ComponentUtil.getAjaxParent(object));
                    }
                }));
            }
        }.setLabel(new ResourceModel("action")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PromotionModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    void create(AjaxRequestTarget target){
        setModelObject(new Promotion());


        open(target);
    }

    void edit(Promotion promotion, AjaxRequestTarget target){
        setModelObject(promotion);

        open(target);
    }

    private void open(AjaxRequestTarget target){
        container.setVisible(true);
        target.add(container);
        appendShowDialogJavaScript(target);
    }

    private void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent)c).clearInput());
    }

    private void action(AjaxRequestTarget target){

    }
}
