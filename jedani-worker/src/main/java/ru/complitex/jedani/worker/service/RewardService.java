package ru.complitex.jedani.worker.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.Dates;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.exception.RewardException;
import ru.complitex.jedani.worker.mapper.RewardMapper;

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

    @Inject
    private PeriodService periodService;

    @Inject
    private RewardMapper rewardMapper;

    public List<Reward> getRewardsBySaleId(Long saleId){
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward().setSaleId(saleId)));
    }

    public BigDecimal getRewardsTotalBySaleId(Long saleId, Long rewardTypeId){
        return getRewardsBySaleId(saleId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public List<Reward> getRewardsByWorkerId(Long workerId){
        //todo opt
        return domainService.getDomains(Reward.class, FilterWrapper.of(new Reward().setWorkerId(workerId)));
    }

    public BigDecimal getRewardsTotalByWorkerId(Long workerId, Long rewardTypeId, Date month){
        return getRewardsByWorkerId(workerId).stream()
                .filter(r -> Objects.equals(r.getType(), rewardTypeId) && Dates.isSameMonth(r.getDate(), month))
                .reduce(ZERO, ((t, p) -> t.add(p.getPoint())), BigDecimal::add);
    }

    public WorkerRewardTree getWorkerRewardTree(Date month){
        WorkerRewardTree tree = new WorkerRewardTree(workerNodeService.getWorkerNodeLevelMap());

        calcSaleVolumes(tree, month);

        calcPaymentVolume(tree, month);

        calcRegistrationCount(tree, month);
        calcFirstLevelCount(tree);

        return tree;
    }

    private void calcSaleVolumes(WorkerRewardTree tree, Date month){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                r.setSaleVolume(saleService.getSaleVolume(r.getWorkerNode().getObjectId(), month));

                r.setGroupSaleVolume(r.getChildRewards().stream()
                        .filter(c -> !c.isManager())
                        .reduce(ZERO, (v, c) -> v.add(c.getSaleVolume().add(c.getGroupSaleVolume())), BigDecimal::add));

                r.setRank(getRank(r.getGroupSaleVolume()));
            });
        });
    }

    private Long getRank(BigDecimal saleVolume){
        if (saleVolume.compareTo(getParameter(20L)) >= 0){
            return RankType.PLATINUM_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(19L)) >= 0){
            return RankType.GOLD_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(18L)) >= 0){
            return RankType.SILVER_DIRECTOR;
        }else if (saleVolume.compareTo(getParameter(17L)) >= 0){
            return RankType.REGIONAL_MANAGER;
        }else if (saleVolume.compareTo(getParameter(16L)) >= 0){
            return RankType.AREA_MANAGER;
        }else if (saleVolume.compareTo(getParameter(15L)) >= 0){
            return RankType.DIVISION_MANAGER;
        }else if (saleVolume.compareTo(getParameter(14L)) >= 0){
            return RankType.SENIOR_MANAGER;
        }else if (saleVolume.compareTo(getParameter(13L)) >= 0){
            return RankType.SENIOR_ASSISTANT;
        }else if (saleVolume.compareTo(getParameter(12L)) >= 0){
            return RankType.TEAM_MANAGER;
        }else if (saleVolume.compareTo(getParameter(11L)) >= 0){
            return RankType.MANAGER_ASSISTANT;
        }

        return 0L;
    }

    private void calcPaymentVolume(WorkerRewardTree tree, Date month){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                r.setPaymentVolume(paymentService.getPaymentsVolumeBySellerWorkerId(r.getWorkerNode().getObjectId(), month));
            });
        });
    }

    private void calcRegistrationCount(WorkerRewardTree tree, Date month){
        tree.forEachLevel((l, rl) -> {
            rl.forEach(r -> {
                r.setRegistrationCount(r.getChildRewards().stream()
                        .filter(c -> !c.isManager() && c.getWorkerNode().getRegistrationDate() != null)
                        .reduce(0L, (v, c) -> c.getRegistrationCount() + (isNewWorker(c, month) ? 1 : 0), Long::sum));
            });
        });
    }

    private boolean isNewWorker(WorkerReward workerReward, Date month){
        return Dates.isSameMonth(workerReward.getWorkerNode().getRegistrationDate(), month);
    }

    private void calcFirstLevelCount(WorkerRewardTree tree){
        tree.forEachLevel((l, rl) -> rl.forEach(r -> r.setFirstLevelCount(Long.valueOf(r.getChildRewards().size()))));
    }

    public BigDecimal getPersonalRewardPoint(Sale sale, List<SaleItem> saleItems){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            boolean mkPremium = saleService.isMkPremiumSaleItems(saleItems);
            boolean mkTouch = saleService.isMkTouchSaleItems(saleItems);

            if (sale.isSasRequest()){
                point = getParameter(1L);
            }else if (w.getMkStatus() == MkStatus.STATUS_PROMO){
                if (mkPremium){
                    point = getParameter(2L);
                }else if (mkTouch){
                    point = getParameter(3L);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_JUST){
                if (mkPremium){
                    point = getParameter(4L);
                }else if (mkTouch){
                    point = getParameter(5L);
                }
            }else if (w.getMkStatus() == MkStatus.STATUS_VIP){
                if (sale.getMkManagerBonusWorkerId() != null){
                    if (mkPremium){
                        point = getParameter(6L).subtract(getParameter(9L));
                    }else if (mkTouch){
                        point = getParameter(7L).subtract(getParameter(10L));
                    }
                }else if (mkPremium){
                    point = getParameter(6L);
                }else if (mkTouch){
                    point = getParameter(7L);
                }
            }
        }else if (sale.getType() == SaleType.BASE_ASSORTMENT && sale.getTotal() != null){
            point = sale.getTotal().multiply(getParameter(8L));
        }

        return point;
    }

    private BigDecimal getManagerPremiumPoint(Sale sale, Long rank){
        switch (rank.intValue()){
            case (int) RankType.MANAGER_ASSISTANT:
                return calcManagerPremiumPoint(sale, 21L, 31L);
            case (int) RankType.TEAM_MANAGER:
                return calcManagerPremiumPoint(sale, 22L, 32L);
            case (int) RankType.SENIOR_ASSISTANT:
                return calcManagerPremiumPoint(sale, 23L, 33L);
            case (int) RankType.SENIOR_MANAGER:
                return calcManagerPremiumPoint(sale, 24L, 34L);
            case (int) RankType.DIVISION_MANAGER:
                return calcManagerPremiumPoint(sale, 25L, 35L);
            case (int) RankType.AREA_MANAGER:
                return calcManagerPremiumPoint(sale, 26L, 36L);
            case (int) RankType.REGIONAL_MANAGER:
                return calcManagerPremiumPoint(sale, 27L, 37L);
            case (int) RankType.SILVER_DIRECTOR:
                return calcManagerPremiumPoint(sale, 28L, 38L);
            case (int) RankType.GOLD_DIRECTOR:
                return calcManagerPremiumPoint(sale, 29L, 39L);
            case (int) RankType.PLATINUM_DIRECTOR:
                return calcManagerPremiumPoint(sale, 30L, 40L);
        }

        return ZERO;
    }

    private BigDecimal calcManagerPremiumPoint(Sale sale, Long mkParameterId, Long baParameterId){
        if (sale.getType() == SaleType.MYCOOK){
            return getParameter(mkParameterId);
        }else if (sale.getType() == SaleType.BASE_ASSORTMENT && sale.getTotal() != null){
            return sale.getTotal().multiply(getParameter(baParameterId));
        }

        return ZERO;
    }




    public BigDecimal getMkManagerBonusRewardPoint(Sale sale, List<SaleItem> saleItems){
        BigDecimal point = ZERO;

        if (sale.getType() == SaleType.MYCOOK){
            Worker w = workerService.getWorker(sale.getSellerWorkerId());

            if (w.getMkStatus() == null){
                w.setMkStatus(MkStatus.STATUS_PROMO);
            }

            if (saleService.isMkPremiumSaleItems(saleItems)){
                point = getParameter(9L);
            }else if (saleService.isMkTouchSaleItems(saleItems)){
                point = getParameter(10L);
            }
        }

        return point;
    }

    private BigDecimal calcRewardPoint(Sale sale, Date month, BigDecimal rewardPoint, Long rewardType){
        BigDecimal point = ZERO;

        if (rewardPoint.compareTo(ZERO) > 0 && sale.getTotal() != null) {
            BigDecimal paidInterest  = paymentService.getPaymentsVolumeBySaleId(sale.getObjectId(), month)
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

    public BigDecimal getCulinaryWorkshopRewardPoint(Sale sale, List<SaleItem> saleItems){
        if (saleService.isMkSaleItems(saleItems)){
            if (!sale.isSasRequest()) {
                return getParameter(41L);
            } else {
                return getParameter(42L);
            }
        }

        return ZERO;
    }

    @Transactional(rollbackFor = RewardException.class)
    public void calculateRewards() throws RewardException {
        try {
            Period period = periodService.getActualPeriod();

            Date month = period.getOperatingMonth();

            rewardMapper.deleteRewards(month);

            WorkerRewardTree tree = getWorkerRewardTree(month);

            saleService.getActiveSales().forEach(s -> {
                if (s.getPersonalRewardPoint() != null){
                    BigDecimal point = calcRewardPoint(s, month, s.getPersonalRewardPoint(),
                            RewardType.TYPE_MYCOOK_SALE);

                    if (point.compareTo(ZERO) > 0){
                        Reward reward = new Reward();

                        reward.setSaleId(s.getObjectId());
                        reward.setWorkerId(s.getSellerWorkerId());
                        reward.setType(RewardType.TYPE_MYCOOK_SALE);
                        reward.setPoint(point);
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);

                        domainService.save(reward);
                    }

                    WorkerReward workerReward = tree.getWorkerReward(s.getSellerWorkerId());

                    if (workerReward.getRank() > 0){
                        BigDecimal premium = calcRewardPoint(s, month,
                                getManagerPremiumPoint(s, workerReward.getRank()), RewardType.TYPE_MANAGER_PREMIUM);

                        if (premium.compareTo(ZERO) > 0){
                            Reward reward = new Reward();

                            reward.setSaleId(s.getObjectId());
                            reward.setWorkerId(s.getSellerWorkerId());
                            reward.setType(RewardType.TYPE_MANAGER_PREMIUM);
                            reward.setPoint(premium);
                            reward.setDate(Dates.currentDate());
                            reward.setMonth(month);
                            reward.setGroupVolume(workerReward.getGroupSaleVolume());
                            reward.setRankId(workerReward.getRank());

                            domainService.save(reward);
                        }
                    }
                }

                if (s.getMkManagerBonusRewardPoint() != null){
                    BigDecimal point = calcRewardPoint(s, month, s.getMkManagerBonusRewardPoint(),
                            RewardType.TYPE_MK_MANAGER_BONUS);

                    if (point.compareTo(ZERO) > 0){
                        Reward reward = new Reward();

                        reward.setSaleId(s.getObjectId());
                        reward.setWorkerId(s.getMkManagerBonusWorkerId());
                        reward.setType(RewardType.TYPE_MK_MANAGER_BONUS);
                        reward.setPoint(point);
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);

                        domainService.save(reward);
                    }
                }

                if (s.getCulinaryRewardPoint() != null && s.getTotal() != null &&
                        paymentService.getPaymentsVolumeBySaleId(s.getObjectId(), month).compareTo(s.getTotal()) >= 0 &&
                        getRewardsTotalBySaleId(s.getObjectId(), RewardType.TYPE_CULINARY_WORKSHOP).compareTo(ZERO) == 0){
                    Reward reward = new Reward();

                    reward.setSaleId(s.getObjectId());
                    reward.setType(RewardType.TYPE_CULINARY_WORKSHOP);
                    reward.setWorkerId(s.getCulinaryWorkerId() != null ? s.getCulinaryWorkerId() : s.getSellerWorkerId());
                    reward.setPoint(s.getCulinaryRewardPoint());
                    reward.setDate(Dates.currentDate());
                    reward.setMonth(month);

                    domainService.save(reward);
                }
            });

            tree.forEachLevel((l, rl) -> {
                rl.forEach(r -> {
                    if (r.getSaleVolume().compareTo(getParameter(43L)) >= 0){
                        Reward reward = new Reward();

                        reward.setType(RewardType.TYPE_PERSONAL_VOLUME);
                        reward.setWorkerId(r.getWorkerNode().getObjectId());
                        reward.setPersonalVolume(r.getSaleVolume());
                        reward.setDate(Dates.currentDate());
                        reward.setMonth(month);

                        if (getRewardsTotalByWorkerId(reward.getWorkerId(), RewardType.TYPE_PERSONAL_VOLUME,
                                month).compareTo(ZERO) == 0) {
                            if (r.getSaleVolume().compareTo(getParameter(44L)) < 0){
                                reward.setPoint(getParameter(45L));
                            }else if (r.getSaleVolume().compareTo(getParameter(44L)) >= 0){
                                reward.setPoint(getParameter(46L));
                            }
                        }

                        domainService.save(reward);
                    }
                });
            });
        } catch (Exception e) {
            throw new RewardException(e.getMessage());
        }
    }

    public BigDecimal getParameter(Long rewardParameterId){
        //todo date
        return domainService.getDomain(RewardParameter.class, rewardParameterId).getDecimal(RewardParameter.VALUE);
    }
}
