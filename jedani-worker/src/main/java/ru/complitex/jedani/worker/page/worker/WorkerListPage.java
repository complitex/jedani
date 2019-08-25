package ru.complitex.jedani.worker.page.worker;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.TextDataFilter;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerStatus;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
@AuthorizeInstantiation({JedaniRoles.ADMINISTRATORS, JedaniRoles.STRUCTURE_ADMINISTRATORS})
public class WorkerListPage extends DomainListPage<Worker>{
    @Inject
    private EntityService entityService;

    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private UserMapper userMapper;

    private WorkerRemoveModal workerRemoveModal;

    public WorkerListPage() {
        super(Worker.class, WorkerPage.class);

        Form form = new Form("workerRemoveForm");
        getContainer().add(form);

        form.add(workerRemoveModal = new WorkerRemoveModal("workerRemove"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getFeedback(), getTable());
            }
        });
    }

    @Override
    protected FilterWrapper<Worker> newFilterWrapper(Worker worker) {
        FilterWrapper<Worker> filterWrapper =  super.newFilterWrapper(worker);

        filterWrapper.setStatus(FilterWrapper.STATUS_ACTIVE_AND_ARCHIVE);

        if (getCurrentWorker().isRegionalLeader()) {
            filterWrapper.put(Worker.FILTER_REGION_IDS, getCurrentWorker().getRegionIdsString());
        }

        return filterWrapper;
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Worker.LAST_NAME).withReference(LastName.ENTITY_NAME, LastName.NAME));
        list.add(entity.getEntityAttribute(Worker.FIRST_NAME).withReference(FirstName.ENTITY_NAME, FirstName.NAME));
        list.add(entity.getEntityAttribute(Worker.MIDDLE_NAME).withReference(MiddleName.ENTITY_NAME, MiddleName.NAME));

        list.add(entity.getEntityAttribute(Worker.J_ID));

        list.add(entity.getEntityAttribute(Worker.REGIONS).withReference(Region.ENTITY_NAME, Region.NAME));

        list.add(entity.getEntityAttribute(Worker.CITIES).withReference(City.ENTITY_NAME, City.NAME)
                .setPrefixEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE)
                        .withReference(CityType.ENTITY_NAME, CityType.SHORT_NAME)));

        list.add(entity.getEntityAttribute(Worker.PHONE));
        list.add(entity.getEntityAttribute(Worker.EMAIL).setStringType(StringType.LOWER_CASE));
        list.add(entity.getEntityAttribute(Worker.INVOLVED_AT));

//        list.add(entity.getEntityAttribute(Worker.POSITION).withReference(Position.ENTITY_NAME, Position.NAME));
//        list.add(entity.getEntityAttribute(Worker.TYPE));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Worker, SortProperty>> columns) {
        columns.add(4, new AbstractDomainColumn<Worker>(new ResourceModel("login"),
                new SortProperty("login")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                Long userId = rowModel.getObject().getParentId();

                cellItem.add(new Label(componentId, userId != null ? userMapper.getUser(userId).getLogin() : ""));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.login"), form);
            }
        });

        //noinspection Duplicates
        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("subWorkersCount"),
                new SortProperty("subWorkersCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getSubWorkerCount()));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.subWorkersCount"), form);
            }
        });

        //noinspection Duplicates
        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("level"), new SortProperty("level")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getLevel()));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.level"), form);
            }
        });
    }

    @Override
    protected boolean isShowHeader() {
        return false;
    }

    @Override
    protected List<Worker> getDomains(FilterWrapper<Worker> filterWrapper) {
        return workerMapper.getWorkers(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<Worker> filterWrapper) {
        return workerMapper.getWorkersCount(filterWrapper);
    }

    @Override
    protected void onEditPageParameters(PageParameters pageParameters) {
        pageParameters.add("wl", "");
    }

    @Override
    protected void onAddPageParameters(PageParameters pageParameters) {
        pageParameters.add("wl", "");
    }

    @Override
    protected IModel<String> displayModel(EntityAttribute entityAttribute) {
        switch (entityAttribute.getEntityAttributeId().intValue()){
            case (int) Worker.J_ID:
                return new ResourceModel("jId");
            case (int) Worker.INVOLVED_AT:
                return new ResourceModel("involvedAt");
            case (int) Worker.TYPE:
                return new ResourceModel("type");
        }

        return super.displayModel(entityAttribute);
    }

    @Override
    protected IColumn<Worker, SortProperty> newDomainActionColumn() {
        return new DomainActionColumn<Worker>(WorkerPage.class){
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                PageParameters pageParameters = new PageParameters().add("id", rowModel.getObject().getId());

                RepeatingView repeatingView = new RepeatingView(componentId);
                cellItem.add(repeatingView);

                repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapBookmarkablePageLink<>(LinkPanel.LINK_COMPONENT_ID,
                        WorkerPage.class, pageParameters, Buttons.Type.Link).setIconType(GlyphIconType.edit)));

                repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxLink<Worker>(LinkPanel.LINK_COMPONENT_ID,
                        Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        workerRemoveModal.delete(target, rowModel.getObject());
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                    }

                    @Override
                    public boolean isVisible() {
                        return !Objects.equals(rowModel.getObject().getObjectId(), 1L) &&
                                !Objects.equals(rowModel.getObject().getObjectId(), getCurrentWorker().getId());
                    }
                }.setIconType(GlyphIconType.remove)));
            }

            @Override
            public String getCssClass() {
                return "domain-id-column worker-action";
            }
        };
    }

    @Override
    protected void onRowItem(Item<Worker> item) {
        super.onRowItem(item);

        if (item.getModelObject().getStatus().equals(Status.ACTIVE) &&
                item.getModelObject().getWorkerStatus() != null &&
                item.getModelObject().getWorkerStatus() == WorkerStatus.MANAGER_CHANGED){
            item.add(new CssClassNameAppender("info"));
        }
    }
}
