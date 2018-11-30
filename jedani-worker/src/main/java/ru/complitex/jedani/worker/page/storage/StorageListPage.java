package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:47
 */
public class StorageListPage extends DomainListPage<Storage> {
    @Inject
    private EntityService entityService;

    @Inject
    private NameService nameService;

    @Inject
    private DomainService domainService;

    @Inject
    private StorageMapper storageMapper;

    public StorageListPage() {
        super(Storage.class, StoragePage.class);
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Storage.CITY)
                .setReferenceEntityAttribute(entityService.getEntityAttribute(City.ENTITY_NAME, City.NAME)));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Storage, SortProperty>> columns) {
        String label = entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.WORKERS).getValue().getText();

        columns.add(new AbstractDomainColumn<Storage>(Model.of(label), new SortProperty("workerIds")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                String workers = rowModel.getObject().getNumberValues(Storage.WORKERS).stream()
                        .map(id -> domainService.getDomain(Worker.ENTITY_NAME, id))
                        .map(w -> w.getText(Worker.J_ID) + " " +
                                nameService.getLastName(w.getNumber(Worker.LAST_NAME)))
                        .collect(Collectors.joining(", "));

                cellItem.add(new Label(componentId, workers));
            }

            @Override
            public Component getFilter(String componentId, FilterForm<?> form) {
                return new TextFilter<>(componentId, Model.of(""), form);
            }
        });
    }

    @Override
    protected List<Storage> getDomains(FilterWrapper<Storage> filterWrapper) {
        return storageMapper.getStorages(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<Storage> filterWrapper) {
        return storageMapper.getStoragesCount(filterWrapper);
    }
}
