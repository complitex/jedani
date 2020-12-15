package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;
import ru.complitex.jedani.worker.entity.Worker;
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
    private WorkerService workerService;

    @Inject
    private SaleService saleService;

    public RewardPanel(String id, Worker worker) {
        super(id, Reward.class);

        if (isCurrentWorkerFilter()) {
            getFilterWrapper().getObject().setWorkerId(worker.getObjectId());
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
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Reward.DATE));
        list.add(entity.getEntityAttribute(Reward.MONTH));
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

        return list;
    }

    @Override
    protected AbstractDomainColumn<Reward> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Reward.SALE)){
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
        }

        return super.newDomainColumn(a);
    }
}
