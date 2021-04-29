package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;

import javax.inject.Inject;
import java.util.Date;

public class SaleRemoveModal extends Modal<Sale> {
    @Inject
    private DomainService domainService;

    private IModel<Sale> saleModel;

    private Component saleDateLabel;

    public SaleRemoveModal(String markupId) {
        super(markupId);

        saleModel = Model.of(new Sale());

        setBackdrop(Backdrop.FALSE);

        header(new ResourceModel("header"));

        add(saleDateLabel = new DateTimeLabel("saleDate", new LoadableDetachableModel<Date>() {
            @Override
            protected Date load() {
                return saleModel.getObject().getDate();
            }
        }).setOutputMarkupId(true));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                domainService.delete(saleModel.getObject());

                domainService.getDomains(SaleItem.class, FilterWrapper.of((SaleItem) new SaleItem()
                        .setParentId(saleModel.getObject().getObjectId())))
                        .forEach(si -> {
                            domainService.delete(si);
                        });

                getSession().success(getString("info_deleted"));

                appendCloseDialogJavaScript(target);

                SaleRemoveModal.this.onUpdate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                getSession().error(getString("error_deleted"));

                SaleRemoveModal.this.onUpdate(target);

                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("delete")).add(new AjaxIndicatorAppender()));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                appendCloseDialogJavaScript(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    public void delete(AjaxRequestTarget target, Sale sale){
        saleModel.setObject(sale);

        target.add(saleDateLabel);

        appendShowDialogJavaScript(target);
    }

    protected void onUpdate(AjaxRequestTarget target){}
}
