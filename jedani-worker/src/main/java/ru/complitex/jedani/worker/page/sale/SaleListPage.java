package ru.complitex.jedani.worker.page.sale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.TextDataFilter;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.mapper.SaleItemMapper;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:22
 */
@AuthorizeInstantiation({JedaniRoles.AUTHORIZED})
public class SaleListPage extends DomainListPage<SaleItem> {
    @Inject
    private SaleItemMapper saleItemMapper;

    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    private SaleModal saleModal;

    public SaleListPage() {
        super(SaleItem.class);

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

        list.add(entity.getEntityAttribute(SaleItem.NOMENCLATURE).withReference(Nomenclature.ENTITY_NAME, Nomenclature.NAME));
        list.add(entity.getEntityAttribute(SaleItem.QUANTITY));
        list.add(entity.getEntityAttribute(SaleItem.PRICE));
        list.add(entity.getEntityAttribute(SaleItem.STORAGE));
        list.add(entity.getEntityAttribute(SaleItem.INSTALLMENT_PERCENTAGE));
        list.add(entity.getEntityAttribute(SaleItem.INSTALLMENT_MONTHS));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<SaleItem, SortProperty>> columns) {
        columns.add(1, new AbstractDomainColumn<SaleItem>(new ResourceModel("date"), new SortProperty("date")) {
            @Override
            public Component getFilter(String componentId, FilterDataForm<?> form) {
                return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.date"), form);
            }

            @Override
            public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                cellItem.add(new Label(componentId, sale.getDate(Sale.DATE)));
            }
        });

        if (isAdmin()) {
            columns.add(2, new AbstractDomainColumn<SaleItem>(new ResourceModel("sellerWorker"), new SortProperty("sellerWorker")) {
                @Override
                public Component getFilter(String componentId, FilterDataForm<?> form) {
                    return new TextDataFilter<>(componentId, new PropertyModel<>(form.getModel(), "map.sellerWorker"), form);
                }

                @Override
                public void populateItem(Item<ICellPopulator<SaleItem>> cellItem, String componentId, IModel<SaleItem> rowModel) {
                    Sale sale = domainService.getDomain(Sale.class, rowModel.getObject().getParentId());

                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(sale.getNumber(Sale.SELLER_WORKER))));
                }
            });
        }
    }

    @Override
    protected boolean isShowHeader() {
        return false;
    }

    @Override
    protected void onAdd(AjaxRequestTarget target) {
        saleModal.sale(getCurrentWorker().getObjectId(), target);
    }

    @Override
    protected List<SaleItem> getDomains(FilterWrapper<SaleItem> filterWrapper) {
        return saleItemMapper.getSaleItems(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<SaleItem> filterWrapper) {
        return saleItemMapper.getSaleItemsCount(filterWrapper);
    }
}
