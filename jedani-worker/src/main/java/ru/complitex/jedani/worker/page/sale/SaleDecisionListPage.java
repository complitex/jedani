package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import ru.complitex.address.entity.Country;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.SaleDecision;
import ru.complitex.jedani.worker.service.SaleDecisionService;

import javax.inject.Inject;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 25.05.2019 18:33
 */
@AuthorizeInstantiation(ADMINISTRATORS)
public class SaleDecisionListPage extends DomainListModalPage<SaleDecision> {
    @Inject
    private SaleDecisionService saleDecisionService;

    private final SaleDecisionModal saleDecisionModal;

    private final SaleDecisionRemoveModal saleDecisionRemoveModal;

    public SaleDecisionListPage() {
        super(SaleDecision.class);

        Form<?> saleDecisionForm = new Form<>("saleDecisionForm");
        getContainer().add(saleDecisionForm);

        saleDecisionForm.add(saleDecisionModal = new SaleDecisionModal("saleDecision"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getContainer());
            }
        });

        Form<?> form = new Form<>("saleDecisionRemoveForm");
        getContainer().add(form);

        form.add(saleDecisionRemoveModal = new SaleDecisionRemoveModal("saleDecisionRemove"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                SaleDecisionListPage.this.update(target);
            }
        });
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {

        entity.getEntityAttribute(SaleDecision.COUNTRY).withReference(Country.class, Country.NAME);
        entity.getEntityAttribute(SaleDecision.NOMENCLATURES).withReference(Nomenclature.class, Nomenclature.NAME);

        return entity.getEntityAttributes(SaleDecision.NOMENCLATURE_TYPE,SaleDecision.DATE_BEGIN, SaleDecision.DATE_END,
                SaleDecision.COUNTRY, SaleDecision.NAME, SaleDecision.NOMENCLATURES);
    }

    @Override
    protected AbstractDomainColumn<SaleDecision> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(SaleDecision.NOMENCLATURE_TYPE)){
            return new AbstractDomainColumn<SaleDecision>(a){

                @Override
                public void populateItem(Item<ICellPopulator<SaleDecision>> cellItem, String componentId, IModel<SaleDecision> rowModel) {
                    Long type = rowModel.getObject().getNomenclatureType();

                    cellItem.add(new Label(componentId, type != null ? getString("type." + type) : ""));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        saleDecisionModal.add(target);
    }

    @Override
    protected void onEdit(SaleDecision saleDecision, AjaxRequestTarget target) {
        saleDecisionModal.edit(saleDecision.getObjectId(), target);
    }

    @Override
    protected void populateAction(RepeatingView repeatingView, IModel<SaleDecision> rowModel) {
        repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxButton(LinkPanel.COMPONENT_ID,
                Buttons.Type.Link) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                SaleDecision saleDecision = saleDecisionService.getSaleDecision(rowModel.getObject().getObjectId());

                saleDecision.clearObjectId();

                saleDecision.setName(saleDecision.getName() + " " + getString("copy"));

                saleDecisionService.save(saleDecision);

                getSession().success(getString("info_copied"));

                SaleDecisionListPage.this.update(target);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
            }
        }.setIconType(GlyphIconType.export)));

        repeatingView.add(new LinkPanel(repeatingView.newChildId(),
                new BootstrapAjaxButton(LinkPanel.COMPONENT_ID, Buttons.Type.Link) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        saleDecisionRemoveModal.open(rowModel.getObject().getObjectId(), target);
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                    }
                }.setIconType(GlyphIconType.remove)));
    }
}
