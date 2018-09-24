package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
import ru.complitex.domain.mapper.EntityAttributeMapper;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;

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
    private EntityAttributeMapper entityAttributeMapper;

    @Inject
    private WorkerMapper workerMapper;

    public WorkerListPage() {
        super(Worker.ENTITY_NAME, WorkerPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Worker.LAST_NAME)
                .setReferenceEntityAttribute(entityAttributeMapper.getEntityAttribute(LastName.ENTITY_NAME, LastName.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.FIRST_NAME)
                .setReferenceEntityAttribute(entityAttributeMapper.getEntityAttribute(FirstName.ENTITY_NAME, FirstName.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.MIDDLE_NAME)
                .setReferenceEntityAttribute(entityAttributeMapper.getEntityAttribute(MiddleName.ENTITY_NAME, MiddleName.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.J_ID));

        list.add(entity.getEntityAttribute(Worker.REGION_IDS)
                .setReferenceEntityAttribute(entityAttributeMapper.getEntityAttribute(Region.ENTITY_NAME, Region.NAME))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.CITY_IDS)
                .setReferenceEntityAttribute(entityAttributeMapper.getEntityAttribute(City.ENTITY_NAME, City.NAME))
                .setPrefixEntityAttribute(entityAttributeMapper.getEntityAttribute(City.ENTITY_NAME, City.CITY_TYPE_ID)
                        .setReferenceEntityAttribute(entityAttributeMapper.getEntityAttribute(CityType.ENTITY_NAME, CityType.SHORT_NAME)))
                .setDisplayCapitalize(true));

        list.add(entity.getEntityAttribute(Worker.PHONE));
        list.add(entity.getEntityAttribute(Worker.EMAIL).setDisplayLowerCase(true));
        list.add(entity.getEntityAttribute(Worker.INVOLVED_AT));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Worker, SortProperty>> columns) {
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
    protected FilterWrapper<Worker> getNewFilterWrapper() {
        return FilterWrapper.of(new Worker());
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
}
