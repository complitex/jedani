package ru.complitex.jedani.worker.page.price;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupTextField;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.model.DateAttributeModel;
import ru.complitex.domain.model.DecimalAttributeModel;
import ru.complitex.domain.model.DomainParentModel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.page.AbstractDomainEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.component.NomenclatureAutoComplete;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Price;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 21.04.2019 22:02
 */
public class PriceModal extends AbstractDomainEditModal<Price> {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    @Inject
    private UserMapper userMapper;

    private IModel<Price> priceModel;

    private WebMarkupContainer container;
    private NotificationPanel feedback;

    private Long currentUserId;
    private SerializableConsumer<AjaxRequestTarget> onChange;

    public PriceModal(String markupId, Long currentUserId, SerializableConsumer<AjaxRequestTarget> onChange) {
        super(markupId);

        this.currentUserId = currentUserId;
        this.onChange = onChange;

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
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

        container.add(new FormGroupDomainAutoComplete("country", Country.ENTITY_NAME, Country.NAME,
                NumberAttributeModel.of(priceModel, Price.COUNTRY)){
            @Override
            public boolean isEnabled() {
                return priceModel.getObject().getObjectId() == null || priceModel.getObject().getNumber(Price.COUNTRY) == null;
            }
        }.setRequired(true));

        container.add(new FormGroupPanel("nomenclature", new NomenclatureAutoComplete(FormGroupPanel.COMPONENT_ID,
                DomainParentModel.of(priceModel)){
            @Override
            protected Domain getFilterObject(String input) {
                Domain domain = super.getFilterObject(input);

                domain.setNumber(Nomenclature.COUNTRIES, priceModel.getObject().getCountyId());

                return domain;
            }
        }.setRequired(true)));

        container.add(new FormGroupDateTextField("begin", DateAttributeModel.of(priceModel, Price.DATE_BEGIN))
                .setRequired(true));
        container.add(new FormGroupTextField<>("price", DecimalAttributeModel.of(priceModel, Price.PRICE),
                BigDecimal.class).setRequired(true));

        Provider<Price> provider = new Provider<Price>(FilterWrapper.of(new Price()).sort("id", false)) {
            @Override
            public Iterator<? extends Price> iterator(long first, long count) {
                FilterWrapper<Price> filterWrapper = getFilterState();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);

                return domainService.getDomains(Price.class, filterWrapper).iterator();
            }

            @Override
            public long size() {
                Price price = getFilterState().getObject();

                price.setStatus(Status.INACTIVE);
                price.setObjectId(priceModel.getObject().getObjectId());

                return domainService.getDomainsCount(getFilterState());
            }
        };

        WebMarkupContainer historyContainer = new WebMarkupContainer("historyContainer"){
            @Override
            public boolean isVisible() {
                return priceModel.getObject().getObjectId() != null && provider.size() > 0;
            }
        };
        container.add(historyContainer);

        FilterForm<FilterWrapper<Price>> historyForm = new FilterForm<>("historyForm", provider);
        historyContainer.add(historyForm);

        List<IColumn<Price, SortProperty>> columns = new ArrayList<>();

        columns.add(new AbstractDomainColumn<Price>("id") {
            @Override
            public void populateItem(Item<ICellPopulator<Price>> cellItem, String componentId, IModel<Price> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getId()));
            }

            @Override
            public String getCssClass() {
                return "domain-id-column";
            }
        });

        columns.add(new AbstractDomainColumn<Price>("dateBegin") {
            @Override
            public void populateItem(Item<ICellPopulator<Price>> cellItem, String componentId, IModel<Price> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getDate(Price.DATE_BEGIN)));
            }
        });

        columns.add(new AbstractDomainColumn<Price>("dateEnd") {
            @Override
            public void populateItem(Item<ICellPopulator<Price>> cellItem, String componentId, IModel<Price> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getDate(Price.DATE_END)));
            }
        });

        columns.add(new AbstractDomainColumn<Price>("price") {
            @Override
            public void populateItem(Item<ICellPopulator<Price>> cellItem, String componentId, IModel<Price> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getDecimal(Price.PRICE)));
            }
        });

        columns.add(new AbstractDomainColumn<Price>("startDate") {
            @Override
            public void populateItem(Item<ICellPopulator<Price>> cellItem, String componentId, IModel<Price> rowModel) {
                cellItem.add(new DateTimeLabel(componentId, rowModel.getObject().getStartDate())
                        .add(AttributeAppender.append("style", "white-space: nowrap")));
            }
        });

        columns.add(new AbstractDomainColumn<Price>("user") {
            @Override
            public void populateItem(Item<ICellPopulator<Price>> cellItem, String componentId, IModel<Price> rowModel) {
                User user = userMapper.getUser(rowModel.getObject().getUserId());

                cellItem.add(new Label(componentId, user != null ? user.getLogin() : ""));
            }
        });

        historyForm.add(new Table<>("history", columns, provider, historyForm, 5, "PriceModal"));

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                PriceModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PriceModal.this.cancel(target);
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

        Nomenclature nomenclature = domainService.getDomain(Nomenclature.class, price.getNomenclatureId());

        if (!nomenclature.getCountryIds().contains(price.getCountyId())){
            error(getString("error_no_country_nomenclature"));
            target.add(feedback);

            return;
        }

        Long count = domainService.getDomainsCount(FilterWrapper.of((Price) new Price()
                .setParentId(priceModel.getObject().getParentId())
                .setNumber(Price.COUNTRY, price.getNumber(Price.COUNTRY))));

        if (price.getObjectId() == null && count > 0) {
            error(getString("error_price_exists"));
            target.add(feedback);

            return;
        }

        if (price.getObjectId() != null){
            Price prevPrice = domainService.getDomain(Price.class, price.getObjectId());

            if (price.getDate(Price.DATE_BEGIN).compareTo(prevPrice.getDate(Price.DATE_BEGIN)) < 0){
                error(getString("error_begin_date_before"));
                target.add(feedback);

                return;
            }

            prevPrice.setDate(Price.DATE_END,  price.getDate(Price.DATE_BEGIN));
            prevPrice.setEndDate(Dates.currentDate());
            prevPrice.setStatus(Status.INACTIVE);

            prevPrice.setUserId(currentUserId);

            domainService.update(prevPrice);
        }

        price.setUserId(currentUserId);
        price.setParentEntityId(entityService.getEntity(Nomenclature.ENTITY_NAME).getId());
        price.setStartDate(Dates.currentDate());
        price.setEndDate(null);

        domainService.insert(price);

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
