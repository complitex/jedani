package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.SaleException;
import ru.complitex.jedani.worker.mapper.PeriodMapper;
import ru.complitex.jedani.worker.mapper.SaleItemMapper;
import ru.complitex.jedani.worker.mapper.SaleMapper;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Anatoly A. Ivanov
 * 19.02.2019 20:26
 */
public class SaleService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private EntityService entityService;

    @Inject
    private StorageService storageService;

    @Inject
    private SaleMapper saleMapper;

    @Inject
    private SaleItemMapper saleItemMapper;

    @Inject
    private PeriodMapper periodMapper;

    @Transactional(rollbackFor = SaleException.class)
    public void save(Sale sale, List<SaleItem> saleItems) throws SaleException {
        if (sale.getObjectId() == null) {
            sale.setPeriodId(periodMapper.getActualPeriod().getObjectId());
        } else {
            domainService.getDomains(SaleItem.class, FilterWrapper.of((SaleItem) new SaleItem().setParentId(sale.getObjectId())))
                    .forEach(si -> {
                        if (saleItems.stream().noneMatch(si0 -> Objects.equals(si.getObjectId(), si0.getObjectId()))){
                            domainService.delete(si);
                        }
                    });
        }

        domainService.save(sale);

        for (SaleItem s : saleItems) {
            Product filter = new Product();

            filter.setParentId(sale.getStorageId());
            filter.setNomenclatureId(s.getNomenclatureId());

            List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of(filter));

            if (products.isEmpty()) {
                accept(sale.getStorageId(), s.getNomenclatureId());

                products = domainService.getDomains(Product.class, FilterWrapper.of(filter));
            }

            Product product = products.get(0);

            product.setReserveQuantity(product.getReserveQuantity() + s.getQuantity());

            domainService.save(product);


            Transfer t = new Transfer();

            t.setNomenclatureId(s.getNomenclatureId());
            t.setQuantity(s.getQuantity());
            t.setType(TransferType.RESERVE);
            t.setStorageIdFrom(sale.getStorageId());
            t.setFirstNameIdTo(sale.getBuyerFirstName());
            t.setMiddleNameIdTo(sale.getBuyerMiddleName());
            t.setLastNameIdTo(sale.getBuyerLastName());

            domainService.save(t);


            Entity saleEntity = entityService.getEntity(Sale.ENTITY_NAME);

            s.setParentEntityId(saleEntity.getId());
            s.setParentId(sale.getObjectId());

            domainService.save(s);
        }
    }

    private void accept(Long storageId, Long nomenclatureId){
        Transfer transfer = new Transfer();

        transfer.setNomenclatureId(nomenclatureId);
        transfer.setQuantity(0L);

        storageService.accept(storageId, transfer);
    }

    public boolean validateQuantity(Sale sale, SaleItem saleItem){
        Product filter = new Product();

        filter.setParentId(sale.getStorageId());
        filter.setNomenclatureId(saleItem.getNomenclatureId());

        List<Product> products = domainService.getDomains(Product.class, FilterWrapper.of(filter));

        return !products.isEmpty() &&
                products.get(0).getQuantity() - products.get(0).getReserveQuantity() > saleItem.getQuantity();
    }

    public List<SaleItem> getSaleItems(Long saleId){
        return saleItemMapper.getSaleItems(FilterWrapper.of((SaleItem) new SaleItem().setParentId(saleId)));
    }

    public BigDecimal getSaleVolume(Long sellerWorkerId, Period period){
        return saleMapper.getSales(FilterWrapper.of(new Sale().setSellerWorkerId(sellerWorkerId))
                .addEntityAttributeId(Sale.TOTAL)
                .put(Sale.FILTER_ACTUAL, true)
                .put(Sale.FILTER_PERIOD, period.getObjectId())).stream()
                .map(s -> s.getTotal() != null ? s.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Nomenclature> getNomenclatures(List<SaleItem> saleItems){
        return saleItems.stream()
                .filter(si -> si.getNomenclatureId() != null)
                .map(si -> domainService.getDomain(Nomenclature.class, si.getNomenclatureId()))
                .collect(Collectors.toList());
    }

    public boolean isMkPremiumSaleItems(List<SaleItem> saleItems){
        return getNomenclatures(saleItems).stream()
                .anyMatch(n -> n.getCode() != null && n.getCode().contains("MK-PREM"));
    }

    public boolean isMkTouchSaleItems(List<SaleItem> saleItems){
        return getNomenclatures(saleItems).stream()
                .anyMatch(n -> n.getCode() != null && n.getCode().contains("MK-TOUCH"));
    }

    public boolean isMkSaleItems(List<SaleItem> saleItems){
        return getNomenclatures(saleItems).stream()
                .anyMatch(n -> n.getCode() != null && (n.getCode().contains("MK-PREM") ||
                        n.getCode().contains("MK-TOUCH")));
    }

    public Sale getSale(Long saleId){
        return domainService.getDomain(Sale.class, saleId);
    }

    public void updateSale(Sale sale, BigDecimal paymentTotalLocal){
        if (sale.getTotalLocal() != null){
            if (sale.getTotalLocal().compareTo(paymentTotalLocal) == 0){
                sale.setSaleStatus(SaleStatus.PAID);
            }else if (sale.getTotalLocal().compareTo(paymentTotalLocal) < 0){
                sale.setSaleStatus(SaleStatus.OVERPAID);
            }else if (paymentTotalLocal.compareTo(sale.getTotal().divide(BigDecimal.TEN, 2, RoundingMode.HALF_EVEN)) >= 0){
                sale.setSaleStatus(SaleStatus.PAYING);
            }else {
                sale.setSaleStatus(SaleStatus.CREATED);
            }

            domainService.save(sale);
        }
    }

    public List<Sale> getActiveSales(){
        return saleMapper.getSales(FilterWrapper.of(new Sale()).put(Sale.FILTER_ACTUAL, true));
    }

    public Set<Long> getActiveSaleWorkerIds(){
        return saleMapper.getSales(FilterWrapper.of(new Sale())
                        .addEntityAttributeId(Sale.SELLER_WORKER)
                        .put(Sale.FILTER_ACTUAL, true)).stream()
                .map(Sale::getSellerWorkerId)
                .collect(Collectors.toSet());
    }

    public BigDecimal getPaymentPercent(Sale sale){
        if (sale.getTotal() != null && sale.getTotal().compareTo(BigDecimal.ZERO) > 0 &&
                sale.getInitialPayment() != null) {
            return sale.getInitialPayment().multiply(new BigDecimal(100))
                    .divide(sale.getTotal(), 2, RoundingMode.HALF_EVEN);
        }

        return BigDecimal.ZERO;
    }

}
