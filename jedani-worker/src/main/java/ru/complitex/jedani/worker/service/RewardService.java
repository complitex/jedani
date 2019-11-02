package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

/**
 * @author Anatoly A. Ivanov
 * 20.10.2019 11:02 AM
 */
public class RewardService implements Serializable {
    @Inject
    private DomainService domainService;

    @Inject
    private WorkerNodeService workerNodeService;

    @Inject
    private SaleService saleService;

    @Inject
    private WorkerService workerService;

    @Inject
    private PaymentService paymentService;

    public List<Reward> getRewardsBySaleId(Long saleId){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward().setSaleId(saleId)));
    }

    public BigDecimal getRewardsTotalBySaleId(Long saleId, Long rewardTypeId){
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public WorkerRewardTree calcRewards(){
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        Date month = new Date(0);

        calcSaleVolume(tree, month);
        calcGroupSaleVolume(tree, month);

        calcPaymentVolume(tree, month);

        calcRegistrationCount(tree, Dates.currentDate());
        calcFirstLevelCount(tree);

        return tree;
    }

    private void calcSaleVolume(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> r.setSaleVolume(saleService.getSaleVolume(r.getWorkerNode().getObjectId())));
        });
    }

    private void calcGroupSaleVolume(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setGroupSaleVolume(r.getChildRewards().stream()
                .reduce(ZERO, (v, c) -> v.add(c.getSaleVolume().add(c.getGroupSaleVolume())),
                        BigDecimal::add))));
    }

    private void calcPaymentVolume(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> r.setPaymentVolume(paymentService.getPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getObjectId())));
        });
    }

    private void calcRegistrationCount(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) ->  rl.forEach(r -> r.setRegistrationCount(r.getChildRewards().stream()
                .filter(c -> !c.isManager() && c.getWorkerNode().getRegistrationDate() != null)
                .reduce(0L, (v, c) -> c.getRegistrationCount() + (isNewWorker(c, date) ? 1 : 0), Long::sum))));
    }

    private boolean isNewWorker(WorkerReward workerReward, Date date){
        return Dates.isSameMonth(workerReward.getWorkerNode().getRegistrationDate(), date);
    }

    private void calcFirstLevelCount(WorkerRewardTree tree){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelCount(Long.valueOf(r.getChildRewards().size()))));
    }

    public BigDecimal getPersonalRewardPoint(Sale sale){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_NO_MK);
            }

            boolean mkPremium = saleService.isMkPremiumSaleItem(sale.getObjectId());
            boolean mkTouch = saleService.isMkTouchSaleItem(sale.getObjectId());

            if (sale.isSasRequest()){
                point = new BigDecimal("80");
            }else if (w.getMkStatus() == MkStatus.STATUS_NO_MK){
                if (mkPremium){
                    point = new BigDecimal("80");
                }else if (mkTouch){
                    point = new BigDecimal("90");
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_INSTALMENT_MK){
                if (mkPremium){
                    point = new BigDecimal("120");
                }else if (mkTouch){
                    point = new BigDecimal("130");
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_HAS_MK){
                if (sale.getMkManagerBonusWorkerId() != null){
                    if (mkPremium){
                        point = new BigDecimal("120");
                    }else if (mkTouch){
                        point = new BigDecimal("130");
                    }
                }else if (mkPremium){
                    point = new BigDecimal("170");
                }else if (mkTouch){
                    point = new BigDecimal("195");
                }
            }
        }else if (sale.getType() == SaleType.BASE_ASSORTMENT && sale.getTotal() != null){
            point = sale.getTotal().multiply(new BigDecimal("0.15"));
        }

        return point;
    }

    public BigDecimal getMkManagerBonusRewardPoint(Sale sale){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_NO_MK);
            }

            boolean mkPremium = saleService.isMkPremiumSaleItem(sale.getObjectId());
            boolean mkTouch = saleService.isMkTouchSaleItem(sale.getObjectId());

            if (mkPremium){
                point = new BigDecimal("50");
            }else if (mkTouch){
                point = new BigDecimal("65");
            }
        }

        return point;
    }

    private BigDecimal calcRewardPoint(Sale sale, BigDecimal rewardPoint, Long rewardType){
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0 && sale.getTotal() != null) {
            BigDecimal paidInterest  = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId())
                    .divide(sale.getTotal(), 2, BigDecimal.ROUND_HALF_EVEN);

            if (paidInterest.compareTo(new BigDecimal("0.2")) >= 0){
                point = point.add(rewardPoint).multiply(new BigDecimal("0.25"));
            }

            if (paidInterest.compareTo(new BigDecimal("0.7")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.35")));
            }

            if (paidInterest.compareTo(new BigDecimal("1")) >= 0){
                point = point.add(rewardPoint.multiply(new BigDecimal("0.40")));
            }

            point = point.subtract(getRewardsTotalBySaleId(sale.getObjectId(), rewardType)).stripTrailingZeros();

        }

        return point;
    }

    public Reward calcPersonalReward(Sale sale, BigDecimal rewardPoint){
        Reward reward = new Reward();

        reward.setSaleId(sale.getObjectId());
        reward.setWorkerId(sale.getSellerWorkerId());
        reward.setType(RewardType.TYPE_MYCOOK_SALE);
        reward.setPoint(calcRewardPoint(sale, rewardPoint, RewardType.TYPE_MYCOOK_SALE));

        return reward;
    }

    public Reward calcMkManagerBonusReward(Sale sale, BigDecimal rewardPoint){
        Reward reward = new Reward();

        reward.setSaleId(sale.getObjectId());
        reward.setWorkerId(sale.getMkManagerBonusWorkerId());
        reward.setType(RewardType.TYPE_MYCOOK_SALE);
        reward.setPoint(calcRewardPoint(sale, rewardPoint, RewardType.TYPE_MK_MANAGER_BONUS));

        return reward;
    }

    public Reward calcPersonalVolumeReward(Sale sale, Date date){
        Reward reward = new Reward();

        reward.setSaleId(sale.getObjectId());
        reward.setType(RewardType.TYPE_PERSONAL_VOLUME);
        reward.setWorkerId(sale.getSellerWorkerId());

        BigDecimal paymentVolume = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId(), date);

        if (getRewardsTotalBySaleId(sale.getObjectId(), RewardType.TYPE_PERSONAL_VOLUME).compareTo(ZERO) == 0) {
            if (paymentVolume.compareTo(new BigDecimal("2000")) >= 0 &&
                    paymentVolume.compareTo(new BigDecimal("2999")) <= 0){
                reward.setPoint(new BigDecimal("50"));
            }else if (paymentVolume.compareTo(new BigDecimal("3000")) >= 0){
                reward.setPoint(new BigDecimal("100"));
            }
        }

        return reward;
    }

    public Reward calcCulinaryReward(Sale sale){
        Reward reward = new Reward();

        reward.setSaleId(sale.getObjectId());
        reward.setType(RewardType.TYPE_CULINARY_WORKSHOP);
        reward.setWorkerId(sale.getCulinaryWorkerId() != null ? sale.getCulinaryWorkerId() : sale.getSellerWorkerId());

        BigDecimal paymentVolume = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId());

        if (sale.getTotal() != null && paymentVolume.compareTo(sale.getTotal()) >= 0 &&
                getRewardsTotalBySaleId(sale.getObjectId(), RewardType.TYPE_CULINARY_WORKSHOP).compareTo(ZERO) == 0){

            if (sale.isSasRequest()){
                reward.setPoint(new BigDecimal("15"));
            }else{
                reward.setPoint(new BigDecimal("25"));
            }
        }

        return reward;
    }
}
