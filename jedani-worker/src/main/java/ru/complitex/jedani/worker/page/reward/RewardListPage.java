package ru.complitex.jedani.worker.page.reward;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Rank;
import ru.complitex.jedani.worker.entity.Reward;
import ru.complitex.jedani.worker.entity.RewardType;
import ru.complitex.jedani.worker.page.period.PeriodCalculateModal;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;
import static ru.complitex.jedani.worker.security.JedaniRoles.STRUCTURE_ADMINISTRATORS;

@AuthorizeInstantiation({ADMINISTRATORS, STRUCTURE_ADMINISTRATORS})
public class RewardListPage extends DomainListModalPage<Reward> {
    @Inject
    private WorkerService workerService;

    @Inject
    private SaleService saleService;

    private RewardModal rewardModal;

    private PeriodCalculateModal periodCalculateModal;

    public RewardListPage() {
        super(Reward.class);

        Form rewardForm = new Form("rewardForm");
        getContainer().add(rewardForm);

        rewardForm.add(rewardModal = new RewardModal("rewardModal").onUpdate(t -> t.add(getFeedback(), getTable())));

        Form periodCalculateForm = new Form("periodCalculateForm");
        getContainer().add(periodCalculateForm);
        periodCalculateForm.add(periodCalculateModal = new PeriodCalculateModal("periodCalculateModal")
                .onUpdate(t -> t.add(getFeedback(), getTable())));

        getContainer().add(new AjaxLink<Void>("calculateRewards") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                periodCalculateModal.create(target);
            }
        });
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Reward.DATE));
        list.add(entity.getEntityAttribute(Reward.MONTH));
        list.add(entity.getEntityAttribute(Reward.SALE));
        list.add(entity.getEntityAttribute(Reward.WORKER));
        list.add(entity.getEntityAttribute(Reward.TYPE).withReference(RewardType.ENTITY_NAME, RewardType.NAME));
        list.add(entity.getEntityAttribute(Reward.POINT));
        list.add(entity.getEntityAttribute(Reward.RATE));
        list.add(entity.getEntityAttribute(Reward.DISCOUNT));
        list.add(entity.getEntityAttribute(Reward.LOCAL));
        list.add(entity.getEntityAttribute(Reward.SALE_VOLUME));
        list.add(entity.getEntityAttribute(Reward.PAYMENT_VOLUME));
        list.add(entity.getEntityAttribute(Reward.GROUP_SALE_VOLUME));
        list.add(entity.getEntityAttribute(Reward.GROUP_PAYMENT_VOLUME));
        list.add(entity.getEntityAttribute(Reward.RANK).withReference(Rank.ENTITY_NAME, Rank.NAME));
        list.add(entity.getEntityAttribute(Reward.MANAGER));
        list.add(entity.getEntityAttribute(Reward.MANAGER_RANK));
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

                @Override
                public String getCssClass() {
                    return "domain-column-nowrap";
                }
            };
        }else if (a.getEntityAttributeId().equals(Reward.TYPE)){
            return new DomainColumn<Reward>(a){
                @Override
                public String getCssClass() {
                    return "domain-column-nowrap";
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
    protected void onEdit(Reward reward, AjaxRequestTarget target) {
        rewardModal.edit(reward, target);
    }
}
