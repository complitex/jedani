package ru.complitex.jedani.worker.page.payout;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.panel.InputPanel;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.entity.Account;
import ru.complitex.jedani.worker.entity.Payout;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.service.AccountService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class PayoutPanel extends Panel {
    @Inject
    private WorkerService workerService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    @Inject
    private AccountService accountService;

    private Table<Account> table;

    public PayoutPanel(String id, Long currencyId) {
        super(id);

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        List<IColumn<Account, Sort>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());

        columns.add(new DomainColumn<>(entityService.getEntityAttribute(Account.ENTITY_NAME, Account.DATE)));

        columns.add(new AbstractDomainColumn<>(entityService.getEntityAttribute(Account.ENTITY_NAME, Account.PERIOD)) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                Period period = periodMapper.getPeriod(rowModel.getObject().getPeriodId());

                cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
            }
        });

        columns.add(new AbstractDomainColumn<>("region", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                Long regionId = workerService.getRegionId(rowModel.getObject().getWorkerId());

                cellItem.add(new Label(componentId, Attributes.capitalize(domainService.getTextValue(Region.ENTITY_NAME, regionId, Region.NAME))));
            }
        });

        columns.add(new AbstractDomainColumn<>("jId", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                String jid = domainService.getText(Worker.ENTITY_NAME, rowModel.getObject().getWorkerId(), Worker.J_ID);

                cellItem.add(new Label(componentId, jid));
            }
        });

        columns.add(new AbstractDomainColumn<>("worker", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
            }
        });

        columns.add(new DomainColumn<>(entityService.getEntityAttribute(Account.ENTITY_NAME, Account.BALANCE)));

        columns.add(new AbstractDomainColumn<>("paid", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                Account account = rowModel.getObject();

                BigDecimal paid = account.getPaid();

                Long periodId = periodMapper.getActualPeriodId();

                if (periodId.equals(account.getPeriodId())) {
                    paid = paid.add(accountService.getPaid(periodId, currencyId, rowModel.getObject().getWorkerId()));
                }

                cellItem.add(new Label(componentId, paid));
            }
        });

        columns.add(new AbstractDomainColumn<>("amount", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                cellItem.add(new InputPanel(componentId, new TextField<>(InputPanel.COMPONENT_ID,
                        new PropertyModel<>(rowModel, "map.amount"), BigDecimal.class)
                        .add(OnChangeAjaxBehavior.onChange(t -> {}))));
            }
        });

        columns.add(new AbstractDomainColumn<>("payout", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Account>> cellItem, String componentId, IModel<Account> rowModel) {
                cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Account>(LinkPanel.COMPONENT_ID, Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        BigDecimal amount = (BigDecimal) rowModel.getObject().getMap().get("amount");

                        if (amount != null) {
                            Payout payout = new Payout();

                            payout.setWorkerId(rowModel.getObject().getWorkerId());
                            payout.setDate(Dates.currentDate());
                            payout.setPeriodId(periodMapper.getActualPeriod().getObjectId());
                            payout.setCurrencyId(currencyId);
                            payout.setAmount(amount);

                            domainService.save(payout);

                            success(getString("info_paid"));

                            rowModel.getObject().getMap().put("amount", null);

                            target.add(feedback, table);
                        }
                    }
                }.setIconType(GlyphIconType.ok)));
            }
        });

        Provider<Account> provider = new Provider<>(FilterWrapper.of(new Account()
                .setCurrencyId(currencyId)
                .setPeriodId(periodMapper.getActualPeriodId()))){

            @Override
            public List<Account> getList() {
                return domainService.getDomains(Account.class, getFilterState());
            }

            @Override
            public Long getCount() {
                return domainService.getDomainsCount(getFilterState());
            }
        };

        EntityAttribute balanceEntityAttribute = entityService.getEntityAttribute(Account.ENTITY_NAME, Account.BALANCE);

        provider.setSort(new Sort(balanceEntityAttribute.getValueType().getKey(), balanceEntityAttribute), SortOrder.DESCENDING);

        FilterForm<FilterWrapper<Account>> form = new FilterForm<>("form", provider);
        form.setOutputMarkupId(true);
        add(form);

        table = new Table<>("table", columns, provider,15, PayoutPanel.class.getName()) {
            @Override
            protected Component newPagingLeft(String id) {
                return new PeriodPanel(id) {
                    @Override
                    protected void onChange(AjaxRequestTarget target, Period period) {
                        provider.getFilterState().getObject().setPeriodId(period.getObjectId());

                        update(target);
                    }
                };
            }

            @Override
            protected AbstractToolbar newFooter(Table<Account> table) {
                return new PayoutSummary(table);
            }
        };
        form.add(table);

        Form<?> payoutForm = new Form<>("payoutForm");
        add(payoutForm);

        PayoutModal payoutModal = new PayoutModal("payoutModal")
                .onUpdate(t -> t.add(feedback, table));

        payoutForm.add(payoutModal);

        add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                payoutModal.create(currencyId, target);
            }
        });
    }
}
