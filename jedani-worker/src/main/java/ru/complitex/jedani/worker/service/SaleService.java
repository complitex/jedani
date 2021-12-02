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
    private PeriodMapper periodMapper;

    @Inject
    private SaleMapper saleMapper;

    @Inject
    private SaleItemMapper saleItemMapper;

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    @Inject
    private StorageService storageService;

    @Inject
    private SaleDecisionService saleDecisionService;

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

    public SaleItem getSaleItem(Long saleId) {
        return saleItemMapper.getSaleItems(FilterWrapper.of((SaleItem) new SaleItem().setParentId(saleId))).get(0);
    }

    public List<Sale> getSales(Long sellerWorkerId) {
        return saleMapper.getSales(FilterWrapper.of(new Sale().setSellerWorkerId(sellerWorkerId))
                .put(Sale.FILTER_ACTUAL, true));
    }

    public List<Sale> getSales(Long sellerWorkerId, Period period) {
        return saleMapper.getSales(FilterWrapper.of(new Sale().setSellerWorkerId(sellerWorkerId))
                .put(Sale.FILTER_ACTUAL, true)
                .put(Sale.FILTER_PERIOD, period.getObjectId()));
    }

    public List<Nomenclature> getNomenclatures(List<SaleItem> saleItems){
        return saleItems.stream()
                .filter(si -> si.getNomenclatureId() != null)
                .map(si -> domainService.getDomain(Nomenclature.class, si.getNomenclatureId()))
                .collect(Collectors.toList());
    }

    public boolean isMycookPremiumSaleItems(List<SaleItem> saleItems){
        return getNomenclatures(saleItems).stream()
                .anyMatch(n -> n.getCode() != null && n.getCode().contains("MK-PREM"));
    }

    public boolean isMycookTouchSaleItems(List<SaleItem> saleItems){
        return getNomenclatures(saleItems).stream()
                .anyMatch(n -> n.getCode() != null && n.getCode().contains("MK-TOUCH"));
    }

    public boolean isMycookSaleItems(List<SaleItem> saleItems){
        return getNomenclatures(saleItems).stream()
                .anyMatch(n -> n.getCode() != null && (n.getCode().contains("MK-PREM") ||
                        n.getCode().contains("MK-TOUCH")));
    }

    public boolean isMycook(Long saleId){
        return getNomenclatures(getSaleItems(saleId)).stream()
                .anyMatch(n -> n.getCode() != null && (n.getCode().contains("MK-PREM") ||
                        n.getCode().contains("MK-TOUCH")));
    }

    public Sale getSale(Long saleId){
        return domainService.getDomain(Sale.class, saleId);
    }

    public boolean isPaying(Sale sale, BigDecimal paymentTotal) {
        long percent = 10;

        SaleItem saleItem = getSaleItems(sale.getObjectId()).get(0);

        if (saleItem.getSaleDecisionId() != null) {
            SaleDecision saleDecision = saleDecisionService.getSaleDecision(saleItem.getSaleDecisionId());

            saleDecisionService.loadRules(saleDecision);

             percent = saleDecision.getRules().stream()
                    .flatMap(rule -> rule.getConditions().stream())
                    .filter(rc -> Objects.equals(rc.getType(), SaleDecisionConditionType.PAYMENT_PERCENT.getId()))
                    .map(rc -> rc.getNumber(RuleCondition.CONDITION))
                    .filter(Objects::nonNull)
                    .filter(p -> p < 10)
                    .findFirst()
                    .orElse(percent);
        }

        return paymentTotal.compareTo(sale.getTotal().multiply(new BigDecimal(percent))
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN)) >= 0;
    }

    public void updateSaleByTotal(Sale sale, BigDecimal paymentTotal) {
        if (sale.getTotal() != null) {
            if (sale.getTotal().compareTo(paymentTotal) == 0) {
                sale.setSaleStatus(SaleStatus.PAID);
            }else if (sale.getTotal().compareTo(paymentTotal) < 0) {
                sale.setSaleStatus(SaleStatus.OVERPAID);
            }else if (isPaying(sale, paymentTotal)) {
                sale.setSaleStatus(SaleStatus.PAYING);
            }else {
                sale.setSaleStatus(SaleStatus.CREATED);
            }

            domainService.save(sale);
        }
    }

    public List<Sale> getSales(){
        return saleMapper.getSales(FilterWrapper.of(new Sale()).put(Sale.FILTER_ACTUAL, true));
    }

    public Set<Long> getSaleWorkerIds(){
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

    public Long getCountryId(Sale sale) {
        return storageService.getCountryId(sale.getStorageId());
    }

    public Long getCountryId(Long saleId) {
        return getCountryId(getSale(saleId));
    }

    public String getContract(Long saleId) {
        return domainService.getText(Sale.ENTITY_NAME, saleId, Sale.CONTRACT);
    }

    public boolean isFeeWithdraw(Long saleId) {
        return domainService.isBoolean(Sale.ENTITY_NAME, saleId, Sale.FEE_WITHDRAW);
    }

    public Long getManagerBonusWorkerId(Long saleId) {
        return domainService.getNumber(Sale.ENTITY_NAME, saleId, Sale.MANAGER_BONUS_WORKER);
    }
}
