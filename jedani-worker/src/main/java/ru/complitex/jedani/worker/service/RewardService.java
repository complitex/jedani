package ru.complitex.jedani.worker.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        calcPersonalRewardsTree(tree, month);

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

    public List<Reward> calcPersonalRewards(Sale sale, Date date){
        List<Reward> rewards = new ArrayList<>();

        Reward reward = new Reward();

        reward.setSaleId(sale.getObjectId());
        reward.setWorkerId(sale.getSellerWorkerId());

        Reward managerReward = new Reward();

        managerReward.setSaleId(sale.getObjectId());
        managerReward.setWorkerId(sale.getManagerWorkerId());

        managerReward.setType(RewardType.TYPE_MANAGER_BONUS);

        if (sale.getType() == SaleType.MYCOOK){
            reward.setType(RewardType.TYPE_MYCOOK_SALE);

            Worker w = workerService.getWorker(reward.getWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_NO_MK);
            }

            boolean mkPremium = saleService.isMkPremiumSaleItem(sale.getObjectId());
            boolean mkTouch = saleService.isMkTouchSaleItem(sale.getObjectId());

            if (sale.isSasRequest()){
                reward.setPoint(new BigDecimal("80"));
            }else if (w.getMkStatus() == MkStatus.STATUS_NO_MK){
                if (mkPremium){
                    reward.setPoint(new BigDecimal("80"));

                    if (managerReward.getWorkerId() != null){
                        managerReward.setPoint(new BigDecimal("50"));
                    }
                }else if (mkTouch){
                    reward.setPoint(new BigDecimal("90"));

                    if (managerReward.getWorkerId() != null){
                        managerReward.setPoint(new BigDecimal("65"));
                    }
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_INSTALMENT_MK &&
                    sale.getTotal() != null && sale.getInitialPayment() != null &&
                    sale.getInitialPayment().multiply(BigDecimal.TEN).compareTo(sale.getTotal()) > 0){
                if (mkPremium){
                    reward.setPoint(new BigDecimal("120"));

                    if (managerReward.getWorkerId() != null){
                        managerReward.setPoint(new BigDecimal("50"));
                    }
                }else if (mkTouch){
                    reward.setPoint(new BigDecimal("130"));

                    if (managerReward.getWorkerId() != null){
                        managerReward.setPoint(new BigDecimal("65"));
                    }
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_HAS_MK){
                if (managerReward.getWorkerId() != null){
                    if (mkPremium){
                        reward.setPoint(new BigDecimal("120"));
                    }else if (mkTouch){
                        reward.setPoint(new BigDecimal("130"));
                    }
                }else if (mkPremium){
                    reward.setPoint(new BigDecimal("170"));
                }else if (mkTouch){
                    reward.setPoint(new BigDecimal("195"));
                }
            }
        }else if (sale.getType() == SaleType.BASE_ASSORTMENT && sale.getTotal() != null){
            reward.setType(RewardType.TYPE_BASE_ASSORTMENT_SALE);

            reward.setPoint(sale.getTotal().multiply(new BigDecimal("0.15")));
        }

        if (reward.getPoint() != null && sale.getTotal() != null) {
            BigDecimal rewardsTotal = getRewardsTotalBySaleId(sale.getObjectId(), reward.getType());

            BigDecimal paidInterest  = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId())
                    .divide(sale.getTotal(), 2, BigDecimal.ROUND_HALF_EVEN);

            BigDecimal point = ZERO;

            if (paidInterest.compareTo(new BigDecimal("0.2")) >= 0){
                point = point.add(reward.getPoint().multiply(new BigDecimal("0.25")));
            }

            if (paidInterest.compareTo(new BigDecimal("0.7")) >= 0){
                point = point.add(reward.getPoint().multiply(new BigDecimal("0.35")));
            }

            if (paidInterest.compareTo(new BigDecimal("1")) >= 0){
                point = point.add(reward.getPoint().multiply(new BigDecimal("0.40")));
            }

            if (point.compareTo(ZERO) != 0) {
                reward.setPoint(point.subtract(rewardsTotal).stripTrailingZeros());

                rewards.add(reward);
            }
        }

        if (managerReward.getPoint() != null &&
                getRewardsTotalBySaleId(sale.getObjectId(), managerReward.getType()).compareTo(ZERO) == 0){
            rewards.add(managerReward);
        }

        BigDecimal monthPaymentVolume = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId(), date);

        if (getRewardsTotalBySaleId(sale.getObjectId(), RewardType.TYPE_PERSONAL_VOLUME).compareTo(ZERO) == 0) {
            if (monthPaymentVolume.compareTo(new BigDecimal("2000")) >= 0 &&
                    monthPaymentVolume.compareTo(new BigDecimal("2999")) <= 0){
                Reward r = new Reward();

                r.setType(RewardType.TYPE_PERSONAL_VOLUME);
                r.setWorkerId(sale.getSellerWorkerId());
                r.setPoint(new BigDecimal("50"));

                rewards.add(r);
            }else if (monthPaymentVolume.compareTo(new BigDecimal("3000")) >= 0){
                Reward r = new Reward();

                r.setType(RewardType.TYPE_PERSONAL_VOLUME);
                r.setWorkerId(sale.getSellerWorkerId());
                r.setPoint(new BigDecimal("100"));

                rewards.add(r);
            }
        }

        BigDecimal paymentVolume = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId(), date);

        if (sale.getTotal() != null && paymentVolume.compareTo(sale.getTotal()) >= 0 &&
                getRewardsTotalBySaleId(sale.getObjectId(), RewardType.TYPE_CULINARY_WORKSHOP).compareTo(ZERO) == 0){
            Reward r = new Reward();

            r.setType(RewardType.TYPE_CULINARY_WORKSHOP);
            r.setWorkerId(sale.getCulinaryWorkerId() != null ? sale.getCulinaryWorkerId() : reward.getWorkerId());

            if (sale.isSasRequest()){
                r.setPoint(new BigDecimal("15"));
            }else{
                r.setPoint(new BigDecimal("25"));
            }

            rewards.add(r);
        }

        return rewards;
    }

    public List<Reward> calcPersonalRewards(Long saleId, Date date){
        return calcPersonalRewards(saleService.getSale(saleId), date);
    }

    private void calcPersonalRewardsTree(WorkerRewardTree tree, Date date){
        saleService.getSales(date).stream().filter(s -> !s.isForYourself()).forEach(s -> {
            calcPersonalRewards(s, date).forEach(r -> tree.getWorkerReward(r.getWorkerId()).getRewards().add(r));
        });
    }


}
