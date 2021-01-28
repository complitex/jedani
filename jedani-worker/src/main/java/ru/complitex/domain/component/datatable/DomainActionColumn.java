package ru.complitex.domain.component.datatable;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.entity.Domain;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 3:19
 */
public class DomainActionColumn<T extends Domain<T>> extends AbstractDomainColumn<T> {
    private Class<? extends WebPage> editPageClass;

    private PageParameters editPageParameters;

    private AjaxIndicatorAppender ajaxIndicatorAppender = new AjaxIndicatorAppender(){
        @Override
        protected String getSpanClass() {
            return super.getSpanClass() + " btn-sm";
        }
    };

    public DomainActionColumn(Class<? extends WebPage> editPageClass, PageParameters editPageParameters) {
        this.editPageClass = editPageClass;
        this.editPageParameters = editPageParameters;
    }

    public DomainActionColumn(Class<? extends WebPage> editPageClass) {
        this.editPageClass = editPageClass;
    }

    public DomainActionColumn() {

    }

    @Override
    public Component getHeader(String componentId) {
        return super.getHeader(componentId).add(ajaxIndicatorAppender);
    }

    @Override
    public Component newFilter(String componentId, Table<T> table) {
        return new LinkPanel(componentId, new BootstrapAjaxButton(LinkPanel.LINK_COMPONENT_ID, Buttons.Type.Link){
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                onAction(target, table);
            }
        }.setIconType(GlyphIconType.search));
    }

    protected void onAction(AjaxRequestTarget target, Table<T> table) {
        target.add(table.getBody());
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        PageParameters pageParameters = new PageParameters().add("id", rowModel.getObject().getId());
        pageParameters.mergeWith(editPageParameters);

        cellItem.add(new LinkPanel(componentId, new BootstrapBookmarkablePageLink<>(LinkPanel.LINK_COMPONENT_ID,
                editPageClass, pageParameters, Buttons.Type.Link).setIconType(GlyphIconType.edit)));
    }

    @Override
    public String getCssClass() {
        return "domain-id-column";
    }

    public AjaxIndicatorAppender getAjaxIndicatorAppender() {
        return ajaxIndicatorAppender;
    }

    public PageParameters getEditPageParameters() {
        return editPageParameters;
    }
}
