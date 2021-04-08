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
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Status;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.entity.WorkerStatus;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
@AuthorizeInstantiation({JedaniRoles.AUTHORIZED})
public class WorkerListPage extends DomainListPage<Worker>{
    @Inject
    private EntityService entityService;

    @Inject
    private WorkerMapper workerMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private DomainService domainService;

    private final WorkerRemoveModal workerRemoveModal;

    public WorkerListPage() {
        super(Worker.class, WorkerPage.class);

        Form<?> form = new Form<>("workerRemoveForm");
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

        Worker currentWorker = getCurrentWorker();

        if (currentWorker.isRegionalLeader()) {
            City city = domainService.getDomain(City.class, getCurrentWorker().getCityId());

            filterWrapper.put(Worker.FILTER_REGION, city.getParentId());
        } else if (currentWorker.isParticipant() && isUser() && !isAdmin() && !isStructureAdmin()){
            worker.setLeft(currentWorker.getLeft());
            worker.setRight(currentWorker.getRight());
            worker.setLevel(currentWorker.getLevel());
        }

        return filterWrapper;
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Worker.LAST_NAME).withReference(LastName.class, LastName.NAME));
        list.add(entity.getEntityAttribute(Worker.FIRST_NAME).withReference(FirstName.class, FirstName.NAME));
        list.add(entity.getEntityAttribute(Worker.MIDDLE_NAME).withReference(MiddleName.class, MiddleName.NAME));
        list.add(new EntityAttribute(Worker.ENTITY_NAME, Worker.REGION));
        list.add(entity.getEntityAttribute(Worker.CITY).withReference(City.class, City.NAME)
                .setPrefixEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE)
                        .withReference(CityType.class, CityType.SHORT_NAME)));
        list.add(entity.getEntityAttribute(Worker.J_ID));
        list.add(entity.getEntityAttribute(Worker.PHONE));
        list.add(entity.getEntityAttribute(Worker.EMAIL).setStringType(StringType.LOWER_CASE));
        list.add(entity.getEntityAttribute(Worker.REGISTRATION_DATE));

//        list.add(entity.getEntityAttribute(Worker.POSITION).withReference(Position.ENTITY_NAME, Position.NAME));
//        list.add(entity.getEntityAttribute(Worker.TYPE));

        return list;
    }

    @Override
    protected IColumn<Worker, Sort> newColumn(EntityAttribute entityAttribute) {
        if (entityAttribute.getEntityAttributeId() == Worker.REGION) {
            return new AbstractDomainColumn<>(new StringResourceModel("region", this), new Sort("region")) {
                @Override
                public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                    Long regionId = domainService.getParentId(City.ENTITY_NAME, rowModel.getObject().getCityId());

                    Region region = domainService.getDomain(Region.class, regionId);

                    cellItem.add(new Label(componentId, region != null ? Attributes.capitalizeWords(region.getTextValue(Region.NAME)) : ""));
                }

                @Override
                public Component newFilter(String componentId, Table<Worker> table) {
                    return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.region"));
                }
            };
        }

        return super.newColumn(entityAttribute);
    }

    @Override
    protected void onAddColumns(List<IColumn<Worker, Sort>> columns) {
        columns.add(6, new AbstractDomainColumn<>(new StringResourceModel("login", this), new Sort("login")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                Long userId = rowModel.getObject().getParentId();

                User user = userMapper.getUser(userId);

                cellItem.add(new Label(componentId, user != null ? user.getLogin() : ""));
            }

            @Override
            public Component newFilter(String componentId, Table<Worker> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.login"));
            }
        });

        //noinspection Duplicates
        columns.add(new AbstractDomainColumn<>(new StringResourceModel("subWorkersCount", this), new Sort("subWorkersCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getSubWorkerCount()));
            }

            @Override
            public Component newFilter(String componentId, Table<Worker> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.subWorkersCount"));
            }
        });

        //noinspection Duplicates
        columns.add(new AbstractDomainColumn<>(new StringResourceModel("level", this), new Sort("level")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getLevel()));
            }

            @Override
            public Component newFilter(String componentId, Table<Worker> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.level"));
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
    protected IModel<String> displayModel(EntityAttribute entityAttribute) {
        switch (entityAttribute.getEntityAttributeId().intValue()){
            case (int) Worker.J_ID:
                return new StringResourceModel("jId", this);
            case (int) Worker.REGISTRATION_DATE:
                return new StringResourceModel("involvedAt", this);
            case (int) Worker.TYPE:
                return new StringResourceModel("type", this);
        }

        return super.displayModel(entityAttribute);
    }

    @Override
    protected IColumn<Worker, Sort> newDomainActionColumn() {
        return new DomainActionColumn<>(WorkerPage.class){
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                Worker worker = rowModel.getObject();

                PageParameters pageParameters = new PageParameters().add("id", worker.getId());

                RepeatingView repeatingView = new RepeatingView(componentId);
                cellItem.add(repeatingView);

                repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapBookmarkablePageLink<>(LinkPanel.LINK_COMPONENT_ID,
                        WorkerPage.class, pageParameters, Buttons.Type.Link).setIconType(GlyphIconType.edit)));

                repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxLink<Worker>(LinkPanel.LINK_COMPONENT_ID,
                        Buttons.Type.Link) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        workerRemoveModal.delete(target, worker);
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                    }

                    @Override
                    public boolean isVisible() {
                        return !worker.getStatus().equals(Status.ARCHIVE) &&
                                !Objects.equals(worker.getObjectId(), 1L) &&
                                !Objects.equals(worker.getObjectId(), getCurrentWorker().getId());
                    }
                }.setIconType(GlyphIconType.remove)));
            }

            @Override
            public String getCssClass() {
                return "domain-id-column domain-action";
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
