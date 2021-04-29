package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.SaleDecision;
import ru.complitex.jedani.worker.entity.SaleItem;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class SaleDecisionRemoveModal extends Modal<SaleDecision> {
    @Inject
    private DomainService domainService;

    private final IModel<SaleDecision> saleDecisionModel;

    private final WebMarkupContainer container;

    public SaleDecisionRemoveModal(String markupId) {
        super(markupId);

        saleDecisionModel = Model.of(new SaleDecision());

        setBackdrop(Backdrop.FALSE);

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        container.add(new Label("name", LoadableDetachableModel.of(() -> saleDecisionModel.getObject().getName())));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                Long saleItemsCount = domainService.getDomainsCount(FilterWrapper.of(new SaleItem()
                        .setSaleDecisionId(saleDecisionModel.getObject().getObjectId())));

                if (saleItemsCount == 0) {
                    domainService.delete(saleDecisionModel.getObject());

                    getSession().success(getString("info_deleted"));
                } else {
                    getSession().error(getString("error_delete_has_sale"));
                }

                appendCloseDialogJavaScript(target);
                SaleDecisionRemoveModal.this.onUpdate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                getSession().error(getString("error_deleted"));
                appendCloseDialogJavaScript(target);
                SaleDecisionRemoveModal.this.onUpdate(target);
            }
        }.setLabel(new ResourceModel("delete")).add(new AjaxIndicatorAppender()));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void open(SaleDecision saleDecision, AjaxRequestTarget target){
        saleDecisionModel.setObject(saleDecision);

        target.add(container);
        appendShowDialogJavaScript(target);
    }

    protected void onUpdate(AjaxRequestTarget target){}
}
