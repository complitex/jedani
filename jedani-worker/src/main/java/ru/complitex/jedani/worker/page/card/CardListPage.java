package ru.complitex.jedani.worker.page.card;

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
import ru.complitex.jedani.worker.entity.Card;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.List;

public class CardListPage extends DomainListModalPage<Card> {
    @Inject
    private WorkerService workerService;

    private CardModal cardModal;

    public CardListPage() {
        super(Card.class);

        Form form = new Form("cardForm");
        getContainer().add(form);

        form.add(cardModal = new CardModal("card"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getContainer());
            }
        });
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        return entity.getEntityAttributes(Card.NUMBER, Card.DATE, Card.WORKER);
    }

    @Override
    protected AbstractDomainColumn<Card> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Card.WORKER)){
            return new AbstractDomainColumn<Card>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Card>> cellItem, String componentId, IModel<Card> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        cardModal.create(target);
    }

    @Override
    protected void onEdit(Card object, AjaxRequestTarget target) {
        cardModal.edit(object, target);
    }
}
