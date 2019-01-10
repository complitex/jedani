package ru.complitex.jedani.worker.page.promotion;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Promotion;
import ru.complitex.jedani.worker.security.JedaniRoles;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 24.12.2018 19:47
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.PROMOTION_ADMINISTRATORS})
public class PromotionListPage extends DomainListPage<Promotion> {
    @Inject
    private EntityService entityService;

    private PromotionModal promotionModal;

    public PromotionListPage() {
        super(Promotion.class);

        Form promotionForm = new Form("promotionForm");
        promotionForm.setMultiPart(true);
        getContainer().add(promotionForm);

        promotionModal = new PromotionModal("promotion"){
            @Override
            protected void onAfterAction(AjaxRequestTarget target) {
                target.add(getFeedback(), getTable());
            }
        };
        promotionForm.add(promotionModal);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Promotion.BEGIN));
        list.add(entity.getEntityAttribute(Promotion.END));
        list.add(entity.getEntityAttribute(Promotion.COUNTRY)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME)));
        list.add(entity.getEntityAttribute(Promotion.NAME));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Promotion, SortProperty>> columns) {
        columns.add(new DomainActionColumn<Promotion>(null){
            @Override
            public void populateItem(Item<ICellPopulator<Promotion>> cellItem, String componentId, IModel<Promotion> rowModel) {
                cellItem.add(new LinkPanel(componentId, new BootstrapAjaxLink<Void>(LinkPanel.LINK_COMPONENT_ID,
                        Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        promotionModal.edit(rowModel.getObject(), target);
                    }
                }.setIconType(GlyphIconType.edit)));
            }
        });
    }

    protected void onRowItem(Item<Promotion> item){
        item.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                promotionModal.edit(item.getModel().getObject(), target);
            }
        });

        item.add(new CssClassNameAppender("pointer"));
    }

    @Override
    protected boolean isShowHeader() {
        return false;
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        promotionModal.create(target);
    }
}
