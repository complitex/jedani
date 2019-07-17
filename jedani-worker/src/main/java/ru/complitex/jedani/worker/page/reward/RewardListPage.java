package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RewardListPage extends DomainListModalPage<Reward> {
    @Inject
    private WorkerService workerService;

    private RewardModal rewardModal;

    public RewardListPage() {
        super(Reward.class);

        Form rewardForm = new Form("rewardForm");
        getContainer().add(rewardForm);

        rewardForm.add(rewardModal = new RewardModal("rewardModal", t -> t.add(getContainer())));
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Reward.DATE));
        list.add(entity.getEntityAttribute(Reward.WORKER));
        list.add(entity.getEntityAttribute(Reward.TYPE).withReference(RewardType.ENTITY_NAME, RewardType.NAME));
        list.add(entity.getEntityAttribute(Reward.RANK).withReference(Rank.ENTITY_NAME, Rank.NAME));
        list.add(entity.getEntityAttribute(Reward.POINT));
        list.add(entity.getEntityAttribute(Reward.DETAIL));

        return list;
    }

    @Override
    protected AbstractDomainColumn<Reward> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Reward.WORKER)){
            return new AbstractDomainColumn<Reward>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Reward>> cellItem, String componentId, IModel<Reward> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        rewardModal.create(target);
    }

    @Override
    protected void onEdit(Reward object, AjaxRequestTarget target) {
        rewardModal.edit(object, target);
    }
}
