package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.RewardMapper;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 15.12.2020 19:00
 */
public class RewardPanel extends DomainListModalPanel<Reward> {
    @Inject
    private RewardMapper rewardMapper;

    @Inject
    private WorkerService workerService;

    @Inject
    private SaleService saleService;

    @Inject
    private PeriodMapper periodMapper;

    @Inject
    private DomainService domainService;

    public RewardPanel(String id, Worker worker) {
        super(id, Reward.class);

        if (isCurrentWorkerFilter()) {
            getFilterWrapper().getObject().setWorkerId(worker.getObjectId());
        }

        if (isActualMonthFilter()){
            getFilterWrapper().put(Reward.FILTER_ACTUAL_MONTH, periodMapper.getActualPeriod().getOperatingMonth());
            getFilterWrapper().put(Reward.FILTER_PERIOD, periodMapper.getActualPeriod().getObjectId());
        }
    }

    protected boolean isCurrentWorkerFilter(){
        return true;
    }

    @Override
    protected boolean isCreateEnabled() {
        return false;
    }

    @Override
    public boolean isEditEnabled() {
        return false;
    }

    protected boolean isActualMonthFilter() {
        return true;
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Reward.DATE));
        list.add(entity.getEntityAttribute(Reward.PERIOD));
        list.add(entity.getEntityAttribute(Reward.SALE));

        if (!isCurrentWorkerFilter()) {
            list.add(entity.getEntityAttribute(Reward.WORKER));
        }

        list.add(entity.getEntityAttribute(Reward.TYPE).withReference(RewardType.class, RewardType.NAME));

        list.add(entity.getEntityAttribute(Reward.POINT));
        list.add(entity.getEntityAttribute(Reward.RATE));
        list.add(entity.getEntityAttribute(Reward.DISCOUNT));
        list.add(entity.getEntityAttribute(Reward.AMOUNT));
        list.add(entity.getEntityAttribute(Reward.DETAIL));

        list.add(entity.getEntityAttribute(Reward.STATUS));

        return list;
    }

    @Override
    protected AbstractDomainColumn<Reward> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Reward.MONTH)){
            return new DomainColumn<>(a, new StringResourceModel("month", this));
        } else if (a.getEntityAttributeId() == Reward.PERIOD){
            return new AbstractDomainColumn<>("period", this) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    Period period = periodMapper.getPeriod(rowModel.getObject().getPeriodId());

                    cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
                }

                @Override
                public Component newFilter(String componentId, Table<Reward> table) {
                    return new TextFilter<>(componentId, Model.of());
                }
            };
        } else if (a.getEntityAttributeId().equals(Reward.SALE)){
            return new AbstractDomainColumn<>("sale", this) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    String sale = "";

                    if (rowModel.getObject().getSaleId() != null) {
                        sale = saleService.getSale(rowModel.getObject().getSaleId()).getContract();
                    }

                    cellItem.add(new Label(componentId, sale));
                }
            };
        } else if (a.getEntityAttributeId().equals(Reward.WORKER)){
            return new AbstractDomainColumn<>("worker", this) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        } else if (a.getEntityAttributeId().equals(Reward.TYPE)){
            return new DomainColumn<>(a);
        } else if (a.getEntityAttributeId().equals(Reward.DETAIL)) {
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    Reward reward = rowModel.getObject();

                    String detail = "";

                    if (reward.getRankId() != null) {
                        detail += getString("rank") + ": " + (domainService.getTextValue(Rank.ENTITY_NAME, reward.getRankId(), Rank.NAME)).toUpperCase() + "\n";
                    }

                    if (reward.getManagerId() != null) {
                        detail += getString("manager") + ": " + workerService.getSimpleWorkerLabel(reward.getManagerId()) + "\n";
                    }

                    if (reward.getManagerRankId() != null) {
                        detail += getString("manager_rank") + ": " + (domainService.getTextValue(Rank.ENTITY_NAME, reward.getManagerRankId(), Rank.NAME)).toUpperCase() + "\n";
                    }

                    if (reward.getSaleVolume() != null && reward.getSaleVolume().compareTo(BigDecimal.ZERO) > 0) {
                        detail += getString("sale_volume") + ": " + reward.getSaleVolume().toPlainString() + "\n";
                    }

                    if (reward.getPaymentVolume() != null && reward.getPaymentVolume().compareTo(BigDecimal.ZERO) > 0) {
                        detail += getString("payment_volume") + ": " + reward.getPaymentVolume().toPlainString() + "\n";
                    }

                    if (reward.getGroupSaleVolume() != null && reward.getGroupSaleVolume().compareTo(BigDecimal.ZERO) > 0) {
                        detail += getString("group_sale_volume") + ": " + reward.getGroupSaleVolume().toPlainString() + "\n";
                    }

                    if (reward.getGroupPaymentVolume() != null && reward.getGroupPaymentVolume().compareTo(BigDecimal.ZERO) > 0) {
                        detail += getString("group_payment_volume") + ": " + reward.getGroupPaymentVolume().toPlainString() + "\n";
                    }

                    if (reward.getStructureSaleVolume() != null && reward.getStructureSaleVolume().compareTo(BigDecimal.ZERO) > 0) {
                        detail += getString("structure_sale_volume") + ": " + reward.getStructureSaleVolume().toPlainString() + "\n";
                    }

                    if (reward.getStructurePaymentVolume() != null && reward.getStructurePaymentVolume().compareTo(BigDecimal.ZERO) > 0) {
                        detail += getString("structure_payment_volume") + ": " + reward.getStructurePaymentVolume().toPlainString() + "\n";
                    }

                    if (reward.getSaleId() != null) {
                        detail += getString("contract") + ": " + domainService.getText(Sale.ENTITY_NAME, reward.getSaleId(), Sale.CONTRACT);
                    }

                    cellItem.add(new MultiLineLabel(componentId, detail));
                }
            };
        } else if (a.getEntityAttributeId().equals(Reward.STATUS)){
            return new AbstractDomainColumn<>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    cellItem.add(new Label(componentId, new StringResourceModel("status." + rowModel.getObject().getRewardStatus(), RewardPanel.this)));
                }
            };
        }


        if (a.getEntityAttributeId().equals(Reward.AMOUNT)){
            return new DomainColumn<>(a, new StringResourceModel("amount", this));
        }else if (a.getEntityAttributeId().equals(Reward.PAYMENT_VOLUME)){
            return new DomainColumn<>(a, new StringResourceModel("paymentVolume", this));
        }else if (a.getEntityAttributeId().equals(Reward.GROUP_PAYMENT_VOLUME)){
            return new DomainColumn<>(a, new StringResourceModel("groupPaymentVolume", this));
        }else if (a.getEntityAttributeId().equals(Reward.STRUCTURE_PAYMENT_VOLUME)){
            return new DomainColumn<>(a, new StringResourceModel("structurePaymentVolume", this));
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected List<Reward> getDomains(FilterWrapper<Reward> filterWrapper) {
        return rewardMapper.getRewards(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<Reward> filterWrapper) {
        return rewardMapper.getRewardsCount(filterWrapper);
    }

    @Override
    protected Component getPagingLeft(String id) {
        return new PeriodPanel(id){
            @Override
            protected void onChange(AjaxRequestTarget target, Period period) {
                getFilterWrapper().put(Reward.FILTER_PERIOD, period != null ? period.getObjectId() : null);

                updateTable(target);
            }
        };
    }
}
