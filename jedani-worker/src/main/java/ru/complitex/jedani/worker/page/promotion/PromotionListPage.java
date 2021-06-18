package ru.complitex.jedani.worker.page.promotion;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import ru.complitex.address.entity.City;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.ValueType;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Promotion;
import ru.complitex.jedani.worker.security.JedaniRoles;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 19:47
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.PROMOTION_ADMINISTRATORS})
public class PromotionListPage extends DomainListModalPage<Promotion> {
    private PromotionModal promotionModal;

    public PromotionListPage() {
        super(Promotion.class);

        Form promotionForm = new Form("promotionForm");
        promotionForm.setMultiPart(true);
        getContainer().add(promotionForm);

        promotionModal = new PromotionModal("promotion"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                PromotionListPage.this.update(target);
            }
        };
        promotionForm.add(promotionModal);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Promotion.DATE_BEGIN));
        list.add(entity.getEntityAttribute(Promotion.DATE_END));
        list.add(entity.getEntityAttribute(Promotion.COUNTRY).withReference(City.class, City.NAME));
        list.add(entity.getEntityAttribute(Promotion.NAME));
        list.add(entity.getEntityAttribute(Promotion.NOMENCLATURES).setValueType(ValueType.ENTITY_LIST)
                .withReference(Nomenclature.class, Nomenclature.NAME));

        return list;
    }

    protected void onRowItem(Item<Promotion> item){
        item.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                promotionModal.edit(item.getModel().getObject().getObjectId(), target);
            }
        });

        item.add(new CssClassNameAppender("pointer"));
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        promotionModal.create(target);
    }
}
