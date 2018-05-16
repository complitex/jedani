package ru.complitex.domain.component.datatable;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 3:19
 */
public class DomainActionColumn extends AbstractDomainColumn{
    private Class<? extends WebPage> editPageClass;

    public DomainActionColumn(Class<? extends WebPage> editPageClass) {
        this.editPageClass = editPageClass;
    }

    @Override
    public Component getHeader(String componentId) {
        return super.getHeader(componentId);
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new LinkPanel(componentId, new BootstrapAjaxButton(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Link){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(form);
            }
        }.setSize(Buttons.Size.Small).setIconType(GlyphIconType.search));
    }

    @Override
    public void populateItem(Item<ICellPopulator<Domain>> cellItem, String componentId, IModel<Domain> rowModel) {
        cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink(LinkPanel.LINK_COMPONENT_ID,
                editPageClass, new PageParameters().add("id", rowModel.getObject().getId()),
                Buttons.Type.Link).setIconType(GlyphIconType.edit).setSize(Buttons.Size.Small)));
    }

    @Override
    public String getCssClass() {
        return "domain-id-column";
    }
}
