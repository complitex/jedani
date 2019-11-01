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

    public BigDecimal getRewardsTotalBySaleId(Long saleId){
        return getRewardsBySaleId(saleId).stream()
                .reduce(BigDecimal.ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public WorkerRewardTree calcRewards(){
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        Date month = new Date(0);

        calcSaleVolume(tree, month);
        calcGroupSaleVolume(tree, month);

        calcPaymentVolume(tree, month);

        calcRegistrationCount(tree, Dates.currentDate());
        calcFirstLevelCount(tree);
        calcPersonalReward(tree, month);

        return tree;
    }

    private void calcSaleVolume(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> r.setSaleVolume(saleService.getSaleVolume(r.getWorkerNode().getObjectId())));
        });
    }

    private void calcGroupSaleVolume(WorkerRewardTree tree, Date date){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setGroupSaleVolume(r.getChildRewards().stream()
                .reduce(BigDecimal.ZERO, (v, c) -> v.add(c.getSaleVolume().add(c.getGroupSaleVolume())),
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

    private void calcPersonalReward(WorkerRewardTree tree, Date date){
        saleService.getSales(date).stream().filter(s -> !s.isForYourself()).forEach(s -> {
            Reward reward = new Reward();

            reward.setSaleId(s.getObjectId());
            reward.setWorkerId(s.getSellerWorkerId());

            Reward managerReward = new Reward();

            managerReward.setSaleId(s.getObjectId());
            managerReward.setWorkerId(s.getManagerWorkerId());

            managerReward.setType(RewardType.TYPE_MANAGER_BONUS);

            if (s.getType() == SaleType.MYCOOK){
                reward.setType(RewardType.TYPE_MYCOOK_SALE);

                Worker w = workerService.getWorker(reward.getWorkerId());

                if (w.getMkStatus() != null) {
                    boolean mkPremium = saleService.isMkPremiumSaleItem(s.getObjectId());
                    boolean mkTouch = saleService.isMkTouchSaleItem(s.getObjectId());

                    if (s.isSasRequest()){
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
                            s.getTotal() != null && s.getInitialPayment() != null &&
                            s.getInitialPayment().multiply(BigDecimal.TEN).compareTo(s.getTotal()) > 0){
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
                }
            }else if (s.getType() == SaleType.BASE_ASSORTMENT && s.getTotal() != null){
                reward.setType(RewardType.TYPE_BASE_ASSORTMENT_SALE);

                reward.setPoint(s.getTotal().multiply(new BigDecimal("0.15")));
            }

            if (reward.getPoint() != null && s.getTotal() != null) {
                BigDecimal rewardsTotal = getRewardsTotalBySaleId(s.getObjectId());

                BigDecimal paidInterest  = paymentService.getPaymentsVolumeBySaleId(s.getObjectId())
                        .divide(s.getTotal(), 2, BigDecimal.ROUND_HALF_EVEN);

                BigDecimal point = BigDecimal.ZERO;

                if (paidInterest.compareTo(new BigDecimal("0.2")) >= 0){
                    point = point.add(reward.getPoint().multiply(new BigDecimal("0.25")));
                }

                if (paidInterest.compareTo(new BigDecimal("0.7")) >= 0){
                    point = point.add(reward.getPoint().multiply(new BigDecimal("0.35")));
                }

                if (paidInterest.compareTo(new BigDecimal("1")) >= 0){
                    point = point.add(reward.getPoint().multiply(new BigDecimal("0.40")));
                }

                reward.setPoint(point.subtract(rewardsTotal));

                tree.getWorkerReward(reward.getWorkerId()).getRewards().add(reward);
            }

            if (managerReward.getPoint() != null){
                tree.getWorkerReward(managerReward.getWorkerId()).getRewards().add(reward);
            }

            BigDecimal paymentVolume = paymentService.getPaymentsVolumeBySaleId(s.getObjectId());

            if (paymentVolume.compareTo(new BigDecimal("2000")) >= 0 &&
                    paymentVolume.compareTo(new BigDecimal("2999")) <= 0){
                Reward r = new Reward();

                r.setType(RewardType.TYPE_PERSONAL_VOLUME);
                r.setPoint(new BigDecimal("50"));

                tree.getWorkerReward(reward.getWorkerId()).getRewards().add(r);
            }else if (paymentVolume.compareTo(new BigDecimal("3000")) >= 0){
                Reward r = new Reward();

                r.setType(RewardType.TYPE_PERSONAL_VOLUME);
                r.setPoint(new BigDecimal("100"));

                tree.getWorkerReward(reward.getWorkerId()).getRewards().add(r);
            }

            if (s.getTotal() != null && paymentVolume.compareTo(s.getTotal()) >= 0){
                Reward r = new Reward();

                r.setType(RewardType.TYPE_CULINARY_WORKSHOP);

                if (s.isSasRequest()){
                    r.setPoint(new BigDecimal("15"));
                }else{
                    r.setPoint(new BigDecimal("25"));
                }

                tree.getWorkerReward(s.getCulinaryWorkerId() != null ? s.getCulinaryWorkerId() : reward.getWorkerId())
                        .getRewards().add(r);
            }
        });
    }
}
