package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.jedani.worker.component.PeriodPanel;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.RewardMapper;
import ru.complitex.jedani.worker.service.PeriodService;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
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
    private PeriodService periodService;

    public RewardPanel(String id, Worker worker) {
        super(id, Reward.class);

        if (isCurrentWorkerFilter()) {
            getFilterWrapper().getObject().setWorkerId(worker.getObjectId());
        }

        if (isActualMonthFilter()){
            getFilterWrapper().put(Reward.FILTER_ACTUAL_MONTH, periodService.getActualPeriod().getOperatingMonth());
            getFilterWrapper().put(Reward.FILTER_PERIOD, periodService.getActualPeriod().getObjectId());
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

        list.add(entity.getEntityAttribute(Reward.TYPE).withReference(RewardType.ENTITY_NAME, RewardType.NAME));
        list.add(entity.getEntityAttribute(Reward.POINT));
        list.add(entity.getEntityAttribute(Reward.TOTAL));
        list.add(entity.getEntityAttribute(Reward.DISCOUNT));
        list.add(entity.getEntityAttribute(Reward.LOCAL));
        list.add(entity.getEntityAttribute(Reward.SALE_VOLUME));
        list.add(entity.getEntityAttribute(Reward.PAYMENT_VOLUME));
        list.add(entity.getEntityAttribute(Reward.GROUP_SALE_VOLUME));
        list.add(entity.getEntityAttribute(Reward.GROUP_PAYMENT_VOLUME));
        list.add(entity.getEntityAttribute(Reward.RANK).withReference(Rank.ENTITY_NAME, Rank.NAME));
        list.add(entity.getEntityAttribute(Reward.MANAGER));
        list.add(entity.getEntityAttribute(Reward.MANAGER_RANK).withReference(Rank.ENTITY_NAME, Rank.NAME));
        list.add(entity.getEntityAttribute(Reward.STRUCTURE_SALE_VOLUME));
        list.add(entity.getEntityAttribute(Reward.STRUCTURE_PAYMENT_VOLUME));
        list.add(entity.getEntityAttribute(Reward.STATUS));

        return list;
    }

    @Override
    protected AbstractDomainColumn<Reward> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId() == Reward.PERIOD){
            return new AbstractDomainColumn<Reward>("period") {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    Period period = periodService.getPeriod(rowModel.getObject().getPeriodId());

                    cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
                }

                @Override
                public Component getHeader(String componentId, Table<Reward> table) {
                    return new TextFilter<>(componentId, Model.of());
                }
            };
        } else if (a.getEntityAttributeId().equals(Reward.SALE)){
            return new AbstractDomainColumn<Reward>("sale") {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    String sale = "";

                    if (rowModel.getObject().getSaleId() != null) {
                        sale = saleService.getSale(rowModel.getObject().getSaleId()).getContract();
                    }

                    cellItem.add(new Label(componentId, sale));
                }
            };
        }else if (a.getEntityAttributeId().equals(Reward.WORKER)){
            return new AbstractDomainColumn<Reward>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        }else if (a.getEntityAttributeId().equals(Reward.MANAGER)){
            return new AbstractDomainColumn<Reward>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getManagerId())));
                }

            };
        }else if (a.getEntityAttributeId().equals(Reward.TYPE)){
            return new DomainColumn<>(a);
        }else if (a.getEntityAttributeId().equals(Reward.STATUS)){
            return new AbstractDomainColumn<Reward>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    cellItem.add(new Label(componentId, new ResourceModel("status." + rowModel.getObject().getRewardStatus())));
                }
            };
        }

        if (a.getEntityAttributeId().equals(Reward.MONTH)){
            return new DomainColumn<>(a, new ResourceModel("month"));
        }else if (a.getEntityAttributeId().equals(Reward.LOCAL)){
            return new DomainColumn<>(a, new ResourceModel("local"));
        }else if (a.getEntityAttributeId().equals(Reward.PAYMENT_VOLUME)){
            return new DomainColumn<>(a, new ResourceModel("paymentVolume"));
        }else if (a.getEntityAttributeId().equals(Reward.GROUP_PAYMENT_VOLUME)){
            return new DomainColumn<>(a, new ResourceModel("groupPaymentVolume"));
        }else if (a.getEntityAttributeId().equals(Reward.STRUCTURE_PAYMENT_VOLUME)){
            return new DomainColumn<>(a, new ResourceModel("structurePaymentVolume"));
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

                target.add(getTable());
            }
        };
    }
}
