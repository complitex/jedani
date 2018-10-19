package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.CityType;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Position;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    public WorkerListPage() {
        super(Worker.class, WorkerPage.class);

        add(new AjaxLink<Void>("addEmployee") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(WorkerPage.class, new PageParameters().add("new", "employee").add("a", ""));
            }
        });
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Worker.LAST_NAME)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(LastName.ENTITY_NAME, LastName.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.FIRST_NAME)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(FirstName.ENTITY_NAME, FirstName.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.MIDDLE_NAME)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(MiddleName.ENTITY_NAME, MiddleName.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.J_ID));

        list.add(entity.getEntityAttribute(Worker.REGION_IDS)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Region.ENTITY_NAME, Region.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.CITY_IDS)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME))
                .setPrefixEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE_ID)
                        .setReferenceEntityAttribute(entityService.getEntityAttribute(CityType.ENTITY_NAME, CityType.SHORT_NAME)))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.PHONE));
        list.add(entity.getEntityAttribute(Worker.EMAIL).setDisplayLowerCase(true));
        list.add(entity.getEntityAttribute(Worker.INVOLVED_AT));

        list.add(entity.getEntityAttribute(Worker.POSITION_ID)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(Position.ENTITY_NAME, Position.NAME)));
        list.add(entity.getEntityAttribute(Worker.EMPLOYEE));

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
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.login"), form);
            }
        });

        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("subWorkersCount"),
                new SortProperty("subWorkersCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getSubWorkerCount()));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });

        columns.add(new AbstractDomainColumn<Worker>(new ResourceModel("level"), new SortProperty("level")) {
            @Override
            public void populateItem(Item<ICellPopulator<Worker>> cellItem, String componentId, IModel<Worker> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getLevel()));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
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
        pageParameters.add("a", "");
    }

    @Override
    protected void onAddPageParameters(PageParameters pageParameters) {
        pageParameters.add("a", "");
    }
}
