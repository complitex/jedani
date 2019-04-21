package ru.complitex.jedani.worker.page.price;

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
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.form.DateTextFieldFormGroup;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.DecimalAttributeModel;
import ru.complitex.domain.model.DomainParentModel;
import ru.complitex.domain.page.AbstractDomainEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Price;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 21.04.2019 22:02
 */
public class PriceEditModal extends AbstractDomainEditModal<Price> {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    private IModel<Price> priceModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private Long currentUserId;
    private SerializableConsumer<AjaxRequestTarget> onChange;

    public PriceEditModal(String markupId, Long currentUserId, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(markupId);

        this.currentUserId = currentUserId;
        this.onChange = onChange;

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        header(new ResourceModel("header"));

        priceModel = Model.of(new Price());

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true)
                .setVisible(false);
        add(container);

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        feedback.showRenderedMessages(false);
        container.add(feedback);

        container.add(new FormGroupPanel("nomenclature", new NomenclatureAutoComplete(FormGroupPanel.COMPONENT_ID,
                DomainParentModel.of(priceModel)).setRequired(true)));

        container.add(new DateTextFieldFormGroup("begin", DateAttributeModel.of(priceModel, Price.DATE_BEGIN))
                .setRequired(true));
        container.add(new FormGroupTextField<>("price", DecimalAttributeModel.of(priceModel, Price.PRICE),
                BigDecimal.class).setRequired(true));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                PriceEditModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PriceEditModal.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    private void cancel(AjaxRequestTarget target) {
        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());

        container.setVisible(false);
        target.add(container);

        appendCloseDialogJavaScript(target);
    }

    private void save(AjaxRequestTarget target) {
        Price price = new Price();

        price.copy(priceModel.getObject(), true);

        if (price.getObjectId() == null){
            Long count = domainService.getDomainsCount(FilterWrapper.of(new Price().setParentId(priceModel.getObject().getParentId())));

            if (count > 0) {
                error(getString("error_price_exists"));
                target.add(feedback);

                return;
            }
        }else{
            Price prevPrice = domainService.getDomain(Price.class, price.getObjectId());

            if (price.getDate(Price.DATE_BEGIN).compareTo(prevPrice.getDate(Price.DATE_BEGIN)) <= 0){
                error(getString("error_begin_date_before"));
                target.add(feedback);

                return;
            }

            LocalDateTime endDateTime = price.getDate(Price.DATE_BEGIN).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime()
                    .minusDays(1).with(LocalTime.MAX);

            prevPrice.setDate(Price.DATE_END, Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant()));
            prevPrice.setEndDate(Dates.currentDate());
            prevPrice.setStatus(Status.INACTIVE);

            prevPrice.setUserId(currentUserId);

            domainService.updateDomain(prevPrice);
        }

        price.setUserId(currentUserId);
        price.setParentEntityId(entityService.getEntity(Nomenclature.ENTITY_NAME).getId());
        price.setStartDate(Dates.currentDate());
        price.setEndDate(null);

        domainService.insertDomain(price);

        getSession().success(getString("info_saved"));

        appendCloseDialogJavaScript(target);

        if (onChange != null){
            onChange.accept(target);
        }
    }

    @Override
    public void edit(Price price, AjaxRequestTarget target) {
        priceModel.setObject(price);

        container.setVisible(true);
        target.add(container);

        appendShowDialogJavaScript(target);
    }
}
