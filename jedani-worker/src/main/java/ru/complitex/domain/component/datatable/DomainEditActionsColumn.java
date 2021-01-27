package ru.complitex.domain.component.datatable;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 26.02.2019 20:22
 */
public abstract class DomainEditActionsColumn<T extends Domain<T>> extends AbstractDomainColumn<T> {

    private AjaxIndicatorAppender ajaxIndicatorAppender = new AjaxIndicatorAppender(){
        @Override
        protected String getSpanClass() {
            return super.getSpanClass() + " btn-sm";
        }
    };

    @Override
    public Component getHeader(String componentId) {
        return super.getHeader(componentId).add(ajaxIndicatorAppender);
    }

    @Override
    public Component getHeader(String componentId, Table<T> table) {
        return new LinkPanel(componentId, new BootstrapAjaxButton(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Link){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(table.getBody());
            }
        }.setIconType(GlyphIconType.search));
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        RepeatingView repeatingView = new RepeatingView(componentId);
        cellItem.add(repeatingView);

        repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxButton(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Link) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                DomainEditActionsColumn.this.onEdit(rowModel, target);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
            }
        }.setIconType(GlyphIconType.edit)));

        populateAction(repeatingView, rowModel);
    }

    protected abstract void onEdit(IModel<T> rowModel, AjaxRequestTarget target);

    @Override
    public String getCssClass() {
        return "domain-id-column domain-action";
    }

    public AjaxIndicatorAppender getAjaxIndicatorAppender() {
        return ajaxIndicatorAppender;
    }

    protected void populateAction(RepeatingView repeatingView, IModel<T> rowModel){

    }
}
