package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.WorkerMapper;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static ru.complitex.jedani.worker.entity.Worker.*;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
public class WorkerListPage extends DomainListPage{
    @Inject
    private EntityService entityService;

    @Inject
    private WorkerMapper workerMapper;

    public WorkerListPage() {
        super("worker", WorkerPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        entityService.setRefEntityAttribute(entity, LAST_NAME, LastName.ENTITY_NAME, LastName.NAME);
        list.add(entity.getEntityAttribute(LAST_NAME));

        entityService.setRefEntityAttribute(entity, FIRST_NAME, FirstName.ENTITY_NAME, FirstName.NAME);
        list.add(entity.getEntityAttribute(FIRST_NAME));

        entityService.setRefEntityAttribute(entity, MIDDLE_NAME, MiddleName.ENTITY_NAME, MiddleName.NAME);
        list.add(entity.getEntityAttribute(MIDDLE_NAME));

        list.add(entity.getEntityAttribute(J_ID));

        entityService.setRefEntityAttribute(entity, REGION_IDS, Region.ENTITY_NAME, Region.NAME);
        list.add(entity.getEntityAttribute(REGION_IDS));

        entityService.setRefEntityAttribute(entity, CITY_IDS, City.ENTITY_NAME, City.NAME);
        list.add(entity.getEntityAttribute(CITY_IDS));

        list.add(entity.getEntityAttribute(PHONE));
        list.add(entity.getEntityAttribute(EMAIL));
        list.add(entity.getEntityAttribute(INVOLVED_AT));

        return list;
    }

    @Override
    protected void onDataLoad(List<Domain> list) {
        list.forEach(d -> d.getMap().put("subWorkersCount", workerMapper.getSubWorkersCount(d.getObjectId())));
    }

    @Override
    protected void onAddColumns(List<IColumn<Domain, SortProperty>> columns) {
        columns.add(new AbstractDomainColumn(new ResourceModel("subWorkersCount"),
                new SortProperty("subWorkersCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Domain>> cellItem, String componentId, IModel<Domain> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getMap().get("subWorkersCount") + ""));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });

        columns.add(new AbstractDomainColumn(new ResourceModel("level"), new SortProperty("level")) {
            @Override
            public void populateItem(Item<ICellPopulator<Domain>> cellItem, String componentId, IModel<Domain> rowModel) {
                cellItem.add(new Label(componentId,  rowModel.getObject().getNumber(Worker.INDEX_LEVEL)));
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
}
