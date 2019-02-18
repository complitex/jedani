package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapCheckbox;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.util.ComponentUtil;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.model.BooleanAttributeModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.entity.SaleType;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:23
 */
public class SaleModal extends Modal<Sale> {
    private IModel<Sale> saleModel;
    private IModel<List<SaleItem>> mycookModel;
    private IModel<List<SaleItem>> baseAssortmentModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    public SaleModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        saleModel = Model.of(new Sale());
        mycookModel = Model.ofList(new ArrayList<>());
        baseAssortmentModel = Model.ofList(new ArrayList<>());

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true)
                .setVisible(false);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        WebMarkupContainer fioContainer = new WebMarkupContainer("fioContainer");
        fioContainer.setOutputMarkupId(true);
        container.add(fioContainer);

        fioContainer.add(new DomainAutoCompleteFormGroup("lastName", LastName.ENTITY_NAME, LastName.NAME,
                NumberAttributeModel.of(saleModel, Sale.BUYER_LAST_NAME)));
        fioContainer.add(new DomainAutoCompleteFormGroup("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                NumberAttributeModel.of(saleModel, Sale.BUYER_FIRST_NAME)));
        fioContainer.add(new DomainAutoCompleteFormGroup("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                NumberAttributeModel.of(saleModel, Sale.BUYER_MIDDLE_NAME)));

        WebMarkupContainer mycookContainer = new WebMarkupContainer("mycookContainer"){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getOrCreateAttribute(Sale.SALE_TYPE).getNumber(),
                        SaleType.MYCOOK);
            }
        };
        mycookContainer.setOutputMarkupId(true);
        mycookContainer.setOutputMarkupPlaceholderTag(true);
        container.add(mycookContainer);

        WebMarkupContainer baseAssortmentContainer = new WebMarkupContainer("baseAssortmentContainer"){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getOrCreateAttribute(Sale.SALE_TYPE).getNumber(),
                        SaleType.BASE_ASSORTMENT);
            }
        };
        baseAssortmentContainer.setOutputMarkupId(true);
        baseAssortmentContainer.setOutputMarkupPlaceholderTag(true);
        container.add(baseAssortmentContainer);

        FormGroupPanel sasRequest = new FormGroupPanel("sasRequest", new BootstrapCheckbox(FormGroupPanel.COMPONENT_ID,
                BooleanAttributeModel.of(saleModel, Sale.SAS_REQUEST), new ResourceModel("sasRequestLabel"))){
            @Override
            public boolean isVisible() {
                return Objects.equals(saleModel.getObject().getOrCreateAttribute(Sale.SALE_TYPE).getNumber(),
                        SaleType.BASE_ASSORTMENT);
            }
        };
        container.add(sasRequest);

        container.add(new FormGroupSelectPanel("saleType", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                NumberAttributeModel.of(saleModel, Sale.SALE_TYPE),
                Arrays.asList(SaleType.MYCOOK, SaleType.BASE_ASSORTMENT),
                new IChoiceRenderer<Long>() {
                    @Override
                    public Object getDisplayValue(Long object) {
                        switch (object.intValue()){
                            case (int) SaleType.MYCOOK:
                                return getString("mycook");

                            case (int) SaleType.BASE_ASSORTMENT:
                                return getString("baseAssortment");

                            default:
                                return null;
                        }
                    }

                    @Override
                    public String getIdValue(Long object, int index) {
                        return object + "";
                    }

                    @Override
                    public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                        return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                    }
                })
                .with(new BootstrapSelectConfig().withNoneSelectedText(""))
                .setNullValid(false).add(OnChangeAjaxBehavior.onChange(target -> target.add(
                        mycookContainer, baseAssortmentContainer, sasRequest)))));


        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                SaleModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                container.visitChildren(((object, visit) -> {
                    if (object.hasErrorMessage()){
                        target.add(ComponentUtil.getAjaxParent(object));
                    }
                }));
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                SaleModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    private void open(AjaxRequestTarget target){
        container.setVisible(true);
        target.add(container);

        appendShowDialogJavaScript(target);
    }

    private void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());
    }

    void sale(AjaxRequestTarget target){
        saleModel.setObject(new Sale());
        mycookModel.setObject(new ArrayList<>());
        baseAssortmentModel.setObject(new ArrayList<>());

        open(target);
    }

    void edit(){

    }

    private void save(AjaxRequestTarget target){

    }

    protected void onUpdate(AjaxRequestTarget target){

    }
}
