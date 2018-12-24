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
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.model.FilterMapModel;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.TextDataFilter;
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
                    .add(Storage.FILTER_CURRENT_WORKER, getCurrentWorker().getObjectId())
                    .add(Storage.FILTER_CITIES, getCurrentWorker().getNumberValuesString(Worker.CITIES));

            boolean hasStorage = storageMapper.getStoragesCount(FilterWrapper.of((Storage) new Storage()
                    .setParentId(getCurrentWorker().getObjectId()))) == 0;

            setAddVisible(hasStorage);
        }
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

        columns.add(new AbstractDomainColumn<Storage>(Model.of(label), new SortProperty("workers")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                String workers = rowModel.getObject().getNumberValues(Storage.WORKERS).stream()
                        .map(id -> workerService.getWorkerLabel(id))
                        .collect(Collectors.joining(", "));

                cellItem.add(new Label(componentId, workers));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.workers"), form);
            }
        });

        columns.add(new AbstractDomainColumn<Storage>(new ResourceModel("worker"), new SortProperty("worker")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getParentId())));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.worker"), form);
            }
        });

        columns.add(new AbstractDomainColumn<Storage>(new ResourceModel("nomenclatureCount"),
                new SortProperty("nomenclatureCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getNomenclatureCount()));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId,  FilterMapModel.of(form.getModel(), Storage.FILTER_NOMENCLATURE_COUNT), form);
            }
        });

        columns.add(new AbstractDomainColumn<Storage>(new ResourceModel("transactionCount"),
                new SortProperty("transactionCount")) {
            @Override
            public void populateItem(Item<ICellPopulator<Storage>> cellItem, String componentId, IModel<Storage> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getTransactionCount()));
            }

            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, FilterMapModel.of(form.getModel(), Storage.FILTER_TRANSACTION_COUNT), form);
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
