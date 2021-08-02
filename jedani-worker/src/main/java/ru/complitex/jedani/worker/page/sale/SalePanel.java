package ru.complitex.jedani.worker.page.sale;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.lang.Objects;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.util.Dates;
import ru.complitex.common.wicket.panel.LinkPanel;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.panel.DomainListModalPanel;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
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

/**
 * @author Anatoly Ivanov
 * 14.12.2020 13:47
 */
public class SalePanel extends DomainListModalPanel<Sale> {
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

    @Inject
    private PeriodMapper periodMapper;

    private final SaleModal saleModal;

    private final SaleRemoveModal saleRemoveModal;

    public SalePanel(String id, Worker worker) {
        super(id, Sale.class);

        FilterWrapper<Sale> filterWrapper = getFilterWrapper();

        if (isCurrentWorkerFilter()) {
            filterWrapper.put(Sale.FILTER_SELLER_WORKER, worker.getJId());
        }

        if (worker.isRegionalLeader()){
            filterWrapper.put(Sale.FILTER_REGION, workerService.getRegionId(worker));
        }

        if (isActualFilter()) {
            filterWrapper.put(Sale.FILTER_ACTUAL, true);
        }

        Form<?> saleForm = new Form<>("saleForm");
        getContainer().add(saleForm);

        saleModal = new SaleModal("sale"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                SalePanel.this.update(target);

            }
        };
        saleForm.add(saleModal);

        Form<?> form = new Form<>("saleRemoveForm");
        getContainer().add(form);

        form.add(saleRemoveModal = new SaleRemoveModal("saleRemove"){
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                SalePanel.this.update(target);
            }
        });
    }

    protected boolean isActualFilter(){
        return true;
    }

    protected void setSellerWorkerJidFilter(String jid){
        getFilterWrapper().put(Sale.FILTER_SELLER_WORKER, jid);
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
    protected List<EntityAttribute> getEntityAttributes(Entity entity) {
        List<EntityAttribute> list = new ArrayList<>();

        list.add(entity.getEntityAttribute(Sale.DATE));
        list.add(entity.getEntityAttribute(Sale.PERIOD));
        list.add(entity.getEntityAttribute(Sale.CONTRACT));

        return list;
    }

    protected boolean isCurrentWorkerFilter(){
        return true;
    }

    @Override
    protected AbstractDomainColumn<Sale> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId() == Sale.PERIOD){
            return new AbstractDomainColumn<>("period", this) {
                @Override
                public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                    Period period = periodMapper.getPeriod(rowModel.getObject().getPeriodId());

                    cellItem.add(new Label(componentId, period != null ? Dates.getMonthText(period.getOperatingMonth()) : ""));
                }
            };
        } else if (a.getEntityAttributeId() == Sale.CONTRACT){
            return new DomainColumn<>(a, new StringResourceModel("contract", this));
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onInitColumns(List<IColumn<Sale, Sort>> columns) {
        if (!isCurrentWorkerFilter()) {
            columns.add(new AbstractDomainColumn<>(SaleItem.FILTER_SELLER_WORKER, this) {
                @Override
                public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                    Sale sale = rowModel.getObject();

                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(sale.getNumber(Sale.SELLER_WORKER))));
                }
            });
        }

        columns.add(new AbstractDomainColumn<>(SaleItem.FILTER_BUYER, this) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Sale sale = rowModel.getObject();

                cellItem.add(new Label(componentId, nameService.getFio(sale.getBuyerLastName(), sale.getBuyerFirstName(),
                        sale.getBuyerMiddleName())));
            }
        });

        columns.add(new AbstractDomainColumn<>("culinary", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Sale sale = rowModel.getObject();

                cellItem.add(new Label(componentId, workerService.getWorkerLabel(sale.getCulinaryWorkerId())));
            }
        });

        columns.add(new AbstractDomainColumn<>("nomenclatures", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                String nomenclatures = saleService.getSaleItems(rowModel.getObject().getObjectId()).stream()
                        .map(si -> Nomenclatures.getNomenclatureLabel(domainService.getDomain(Nomenclature.class,
                                si.getNomenclatureId())) + " - " + si.getQuantity() + "\u00a0" + getString("qty"))
                        .collect(Collectors.joining("\n"));

                cellItem.add(new MultiLineLabel(componentId, nomenclatures));
            }
        });

        columns.add(new AbstractDomainColumn<>("total", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                cellItem.add(new Label(componentId, Objects.defaultIfNull(rowModel.getObject().getTotal(), BigDecimal.ZERO)
                        .toPlainString()));
            }
        });

        columns.add(new AbstractDomainColumn<>("storage", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Sale sale = rowModel.getObject();

                cellItem.add(new Label(componentId, Storages.getSimpleStorageLabel(sale.getStorageId(), domainService)));
            }
        });

        columns.add(new AbstractDomainColumn<>("installmentMonths", this) {
            @Override
            public void populateItem(Item<ICellPopulator<Sale>> cellItem, String componentId, IModel<Sale> rowModel) {
                Long installmentMonths = rowModel.getObject().getInstallmentMonths();

                cellItem.add(new Label(componentId,  Objects.defaultIfNull(installmentMonths, 0L) > 0
                        ? installmentMonths + " " + getString("month") : ""));
            }
        });

        columns.add(new AbstractDomainColumn<>("status", this) {
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
                        case (int) SaleStatus.PAID:
                            status =  getString("closed");
                            break;
                        case (int) SaleStatus.ARCHIVE:
                            status =  getString("archive");
                            break;
                        case (int) SaleStatus.OVERPAID:
                            status =  getString("overpayment");
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
        saleModal.edit(sale.getObjectId(), target, !isViewOnly());
    }

    @Override
    protected boolean isCreateEnabled() {
        return false;
    }

    @Override
    public boolean isEditEnabled() {
        return true;
    }

    public boolean isViewOnly() {
        return true;
    }

    public boolean isRemoveEnabled() {
        return false;
    }

    @Override
    protected void populateAction(RepeatingView repeatingView, IModel<Sale> rowModel) {
        if (isRemoveEnabled()) {
            repeatingView.add(new LinkPanel(repeatingView.newChildId(),
                    new BootstrapAjaxButton(LinkPanel.COMPONENT_ID, Buttons.Type.Link) {
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
    }

    @Override
    protected void onRowItem(Item<Sale> item) {
        super.onRowItem(item);

        Sale sale = item.getModelObject();

        String statusClass = null;

        if (sale.getSaleStatus() != null){
            switch (sale.getSaleStatus().intValue()){
                case (int) SaleStatus.PAYING:
                    statusClass = "success";
                    break;
                case (int) SaleStatus.RISK:
                    statusClass = "warning";
                    break;
                case (int) SaleStatus.NOT_PAYING:
                    statusClass = "danger";
                    break;
                case (int) SaleStatus.PAID:
                    statusClass = "sale_status_closed";
                    break;
                case (int) SaleStatus.OVERPAID:
                    statusClass = "info";
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
