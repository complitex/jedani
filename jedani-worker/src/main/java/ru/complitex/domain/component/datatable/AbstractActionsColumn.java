package ru.complitex.domain.component.datatable;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.entity.Domain;

import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 17.11.2019 17:37
 */
public abstract class AbstractActionsColumn<T extends Domain> extends AbstractDomainColumn<T> {

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
    public Component newFilter(String componentId, Table<T> table) {
        return new LinkPanel(componentId, new BootstrapAjaxButton(LinkPanel.COMPONENT_ID, Buttons.Type.Link){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(table.getBody());
            }
        }.setIconType(GlyphIconType.search));
    }

    protected abstract List<Component> getActions(String componentId, IModel<T> rowModel);

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        RepeatingView repeatingView = new RepeatingView(componentId);
        cellItem.add(repeatingView);

        getActions(LinkPanel.COMPONENT_ID, rowModel)
                .forEach(a -> repeatingView.add(new LinkPanel(repeatingView.newChildId(), a)));
    }

    @Override
    public String getCssClass() {
        return "domain-id-column domain-action";
    }

    public AjaxIndicatorAppender getAjaxIndicatorAppender() {
        return ajaxIndicatorAppender;
    }

}
