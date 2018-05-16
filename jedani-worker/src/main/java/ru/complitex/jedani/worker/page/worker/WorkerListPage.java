package ru.complitex.jedani.worker.page.worker;

import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
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

        list.add(entity.getEntityAttribute(BIRTHDAY));
        list.add(entity.getEntityAttribute(PHONE));
        list.add(entity.getEntityAttribute(EMAIL));
        list.add(entity.getEntityAttribute(INVOLVED_AT));

        //todo кол-во сотрудников и уровней

        return list;
    }

    @Override
    protected boolean isShowHeader() {
        return false;
    }
}
