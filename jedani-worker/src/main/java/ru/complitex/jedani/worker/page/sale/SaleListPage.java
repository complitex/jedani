package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Objects;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.entity.SaleStatus;
import ru.complitex.jedani.worker.mapper.SaleMapper;
import ru.complitex.jedani.worker.service.SaleService;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.jedani.worker.util.Nomenclatures;
import ru.complitex.jedani.worker.util.Storages;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.complitex.jedani.worker.security.JedaniRoles.*;

/**
 * @author Anatoly A. Ivanov
 * 18.02.2019 15:22
 */
@AuthorizeInstantiation({ADMINISTRATORS, STRUCTURE_ADMINISTRATORS, SALE_ADMINISTRATORS})
public class SaleListPage extends DomainListModalPage<Sale> {
    @Inject
    private SaleMapper saleMapper;

    @Inject
    private SaleService saleService;

    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    @Inject
    private NameService nameService;

    private SaleModal saleModal;

    private SaleRemoveModal saleRemoveModal;

    public SaleListPage() {
        super(Sale.class);

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

        Form form = new Form("saleRemoveForm");
        getContainer().add(form);

        form.add(saleRemoveModal = new SaleRemoveModal("saleRemove"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(getFeedback(), getTable());
            }
        });
    }

    @Override
    protected FilterWrapper<Sale> newFilterWrapper(Sale domainObject) {
        FilterWrapper<Sale> filterWrapper = super.newFilterWrapper(domainObject);

        if (getCurrentWorker().isRegionalLeader()){
            filterWrapper.put(Sale.FILTER_REGION_IDS, getCurrentWorker().getRegionIdsString());

        }else if (!isAdmin() && !isStructureAdmin()){
            filterWrapper.put(Sale.FILTER_SELLER_WORKER, getCurrentWorker().getObjectId());
        }

        return filterWrapper;
    }

    @Override
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Sale.DATE));
        list.add(entity.getEntityAttribute(Sale.CONTRACT));

        return list;
    }

    @Override
    protected void onAddColumns(List<IColumn<Sale, SortProperty>> columns) {


        if (isAdmin() || isStructureAdmin()) {
            columns.add(new AbstractDomainColumn<Sale>(SaleItem.FILTER_SELLER_WORKER) {
                @Override
                public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                    Sale sale = rowModel.getObject();

                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(sale.getNumber(Sale.SELLER_WORKER))));
                }
            });
        }

        columns.add(new AbstractDomainColumn<Sale>(SaleItem.FILTER_BUYER) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Sale sale = rowModel.getObject();

                cellItem.add(new Label(componentId, nameService.getFio(sale.getBuyerLastName(), sale.getBuyerFirstName(),
                        sale.getBuyerMiddleName())));
            }
        });

        columns.add(new AbstractDomainColumn<Sale>("nomenclatures") {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                String nomenclatures = saleService.getSaleItems(rowModel.getObject().getObjectId()).stream()
                        .map(si -> Nomenclatures.getNomenclatureLabel(domainService.getDomain(Nomenclature.class,
                                si.getNomenclatureId())) + " - " + si.getQuantity() + " " + getString("qty"))
                        .collect(Collectors.joining("\n"));

                cellItem.add(new MultiLineLabel(componentId, nomenclatures));
            }
        });

        columns.add(new AbstractDomainColumn<Sale>("total") {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                cellItem.add(new Label(componentId, Objects.defaultIfNull(rowModel.getObject().getTotal(), BigDecimal.ZERO)
                        .toPlainString()));
            }
        });

        columns.add(new AbstractDomainColumn<Sale>("storage") {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Sale sale = rowModel.getObject();

                cellItem.add(new Label(componentId, Storages.getSimpleStorageLabel(sale.getStorageId(), domainService)));
            }
        });

        columns.add(new AbstractDomainColumn<Sale>("installmentMonths") {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Long installmentMonths = rowModel.getObject().getInstallmentMonths();

                cellItem.add(new Label(componentId,  Objects.defaultIfNull(installmentMonths, 0L) > 0
                        ? installmentMonths + " " + getString("month") : ""));
            }
        });

        columns.add(new AbstractDomainColumn<Sale>("status") {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Sale sale = rowModel.getObject();

                String status = "";

                if (sale.getSaleStatus() != null){
                    switch (sale.getSaleStatus().intValue()){
                        case (int) SaleStatus.CREATED:
                            status = getString("created");
                            break;
                        case (int) SaleStatus.PAYING:
                            status =  getString("paying");
                            break;
                        case (int) SaleStatus.RISK:
                            status =  getString("risk");
                            break;
                        case (int) SaleStatus.NOT_PAYING:
                            status =  getString("not_paying");
                            break;
                        case (int) SaleStatus.CLOSED:
                            status =  getString("closed");
                            break;
                        case (int) SaleStatus.ARCHIVE:
                            status =  getString("archive");
                            break;
                    }
                }

                cellItem.add(new Label(componentId, status));
            }
        });
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        saleModal.create(target);
    }

    @Override
    protected void onEdit(Sale sale, AjaxRequestTarget target) {
        saleModal.edit(sale, target, isAdmin() || isStructureAdmin());
    }

    @Override
    protected List<Sale> getDomains(FilterWrapper<Sale> filterWrapper) {
        return saleMapper.getSales(filterWrapper);
    }

    @Override
    protected Long getDomainsCount(FilterWrapper<Sale> filterWrapper) {
        return saleMapper.getSalesCount(filterWrapper);
    }

    @Override
    protected boolean isEditEnabled() {
        return isAdmin() || isStructureAdmin();
    }

    @Override
    protected void onAddAction(RepeatingView repeatingView, IModel<Sale> rowModel) {
        repeatingView.add(new LinkPanel(repeatingView.newChildId(), new BootstrapAjaxButton(LinkPanel.LINK_COMPONENT_ID,
                Buttons.Type.Link) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                saleRemoveModal.delete(target, rowModel.getObject());
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
            }
        }.setIconType(GlyphIconType.remove)));
    }

    @Override
    protected void onRowItem(Item<Sale> item) {
        super.onRowItem(item);

        Sale sale = item.getModelObject();

        String statusClass = null;

        if (sale.getSaleStatus() != null){
            switch (sale.getSaleStatus().intValue()){
                case (int) SaleStatus.CREATED:
                    statusClass = "info";
                    break;
                case (int) SaleStatus.PAYING:
                    statusClass = "success";
                    break;
                case (int) SaleStatus.RISK:
                    statusClass = "warning";
                    break;
                case (int) SaleStatus.NOT_PAYING:
                    statusClass = "danger";
                    break;
                case (int) SaleStatus.CLOSED:
                    statusClass = "sale_status_closed";
                    break;
                case (int) SaleStatus.ARCHIVE:
                    statusClass = "active";
                    break;
            }
        }

        if (statusClass != null){
            item.add(new CssClassNameAppender(statusClass));
        }
    }
}
