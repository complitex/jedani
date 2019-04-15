package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.mapper.SaleItemMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.jedani.worker.util.Storages;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:22
 */
@AuthorizeInstantiation({JedaniRoles.AUTHORIZED})
public class SaleListPage extends DomainListModalPage<SaleItem> {
    @Inject
    private SaleItemMapper saleItemMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    @Inject
    private NameService nameService;

    private SaleModal saleModal;

    public SaleListPage() {
        super(SaleItem.class);

        title(new ResourceModel("title"));

        Form saleForm = new Form("saleForm");
        getContainer().add(saleForm);

        saleModal = new SaleModal("sale"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getFeedback(), getTable());
            }
        };
        saleForm.add(saleModal);

        if (!isAdmin()){
            getFilterWrapper().add("sellerWorker", getCurrentWorker().getObjectId());
        }
    }


    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(SaleItem.NOMENCLATURE)
                .withReferences(Nomenclature.ENTITY_NAME, Nomenclature.CODE, Nomenclature.NAME));
        list.add(entity.getEntityAttribute(SaleItem.QUANTITY));
        list.add(entity.getEntityAttribute(SaleItem.PRICE));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<SaleItem, SortProperty>> columns) {
        columns.add(1, new AbstractDomainColumn<SaleItem>(SaleItem.FILTER_DATE) {
            @Override
            public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                cellItem.add(new DateTimeLabel(componentId, sale.getDate(Sale.DATE)));
            }
        });

        columns.add(2, new AbstractDomainColumn<SaleItem>(SaleItem.FILTER_BUYER) {
            @Override
            public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                cellItem.add(new Label(componentId, nameService.getFio(sale.getBuyerLastName(), sale.getBuyerFirstName(),
                        sale.getBuyerMiddleName())));
            }
        });

        columns.add(new AbstractDomainColumn<SaleItem>(SaleItem.FILTER_STORAGE) {
            @Override
            public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                cellItem.add(new Label(componentId, Storages.getSimpleStorageLabel(sale.getStorageId(), domainService)));
            }
        });

        columns.add(new AbstractDomainColumn<SaleItem>(SaleItem.FILTER_INSTALLMENT_PERCENTAGE) {
            @Override
            public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                cellItem.add(new Label(componentId, sale.getInstallmentPercentage()));
            }
        });

        columns.add(new AbstractDomainColumn<SaleItem>(SaleItem.FILTER_INSTALLMENT_MONTHS) {
            @Override
            public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                cellItem.add(new Label(componentId, sale.getInstallmentMonths()));
            }
        });

        if (isAdmin()) {
            columns.add(2, new AbstractDomainColumn<SaleItem>(SaleItem.FILTER_SELLER_WORKER) {
                @Override
                public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                    Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(sale.getNumber(Sale.SELLER_WORKER))));
                }
            });
        }
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        saleModal.sale(getCurrentWorker().getObjectId(), target);
    }

    @Override
    protected void onActionEdit(SaleItem object, AjaxRequestTarget target) {
        saleModal.view(object, target);
    }

    @Override
    protected List<SaleItem> getDomains(FilterWrapper<SaleItem> filterWrapper) {
        return saleItemMapper.getSaleItems(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<SaleItem> filterWrapper) {
        return saleItemMapper.getSaleItemsCount(filterWrapper);
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }
}
