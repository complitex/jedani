package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.common.wicket.table.TextFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 18.10.2018 20:47
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class StorageListPage extends DomainListPage<Storage> {
    @Inject
    private EntityService entityService;

    @Inject
    private StorageMapper storageMapper;

    @Inject
    private WorkerService workerService;

    public StorageListPage() {
        super(Storage.class, StoragePage.class);

        if (!isAdmin()) {
            getFilterWrapper()
                    .put(Storage.FILTER_CURRENT_WORKER, getCurrentWorker().getObjectId())
                    .put(Storage.FILTER_CITIES, getCurrentWorker().getNumberValuesString(Worker.CITIES));

            boolean hasStorage = storageMapper.getStoragesCount(FilterWrapper.of((Storage) new Storage()
                    .setParentId(getCurrentWorker().getObjectId()))) == 0;

            setAddVisible(hasStorage);
        }
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Storage.CITY).withReference(City.ENTITY_NAME, City.NAME));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Storage, Sort>> columns) {
        String label = entityService.getEntityAttribute(Storage.ENTITY_NAME, Storage.WORKERS).getValue().getText();

        columns.add(new AbstractDomainColumn<Storage>(Model.of(label), new Sort("workers")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                String workers = rowModel.getObject().getNumberValues(Storage.WORKERS).stream()
                        .map(id -> workerService.getWorkerLabel(id))
                        .collect(Collectors.joining("; "));

                cellItem.add(new Label(componentId, workers));
            }

            @Override
            public Component getHeader(String componentId, Table<Storage> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.workers"));
            }
        });

        columns.add(new AbstractDomainColumn<>(new ResourceModel("worker"), new Sort("worker")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getParentId())));
            }

            @Override
            public Component getHeader(String componentId, Table<Storage> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map.worker"));
            }
        });

        columns.add(new AbstractDomainColumn<>(new ResourceModel("nomenclatureCount"),
                new Sort("nomenclatureCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getNomenclatureCount()));
            }

            @Override
            public Component getHeader(String componentId, Table<Storage> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map." + Storage.FILTER_NOMENCLATURE_COUNT));
            }
        });

        columns.add(new AbstractDomainColumn<>(new ResourceModel("transactionCount"),
                new Sort("transactionCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getTransactionCount()));
            }

            @Override
            public Component getHeader(String componentId, Table<Storage> table) {
                return new TextFilter<>(componentId, PropertyModel.of(table.getFilterWrapper(), "map." + Storage.FILTER_TRANSACTION_COUNT));
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

    @Override
    protected boolean isShowHeader() {
        return false;
    }
}
