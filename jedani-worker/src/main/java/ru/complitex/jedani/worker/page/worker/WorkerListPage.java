package ru.complitex.jedani.worker.page.worker;

import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.complitex.jedani.worker.entity.Worker.*;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
public class WorkerListPage extends DomainListPage{
    public WorkerListPage() {
        super("worker", WorkerPage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {

        return Stream.of(J_ID, CREATED_AT, FIRST_NAME, MIDDLE_NAME, LAST_NAME, BIRTHDAY, PHONE, EMAIL, CITY_ID) //todo regions
                .map(entity::getEntityAttribute).collect(Collectors.toList());
    }

    @Override
    protected boolean isShowHeader() {
        return false;
    }
}
