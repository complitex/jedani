package ru.complitex.jedani.worker.entity;

import ru.complitex.domain.entity.Domain;

/**
 * @author Ivanov Anatoliy
 */
public class RewardRank extends Domain {
    public static final String ENTITY_NAME = "reward_rank";

    public static final long WORKER = 1;
    public static final long RANK = 2;
    public static final long PERIOD = 3;

    public RewardRank() {
        super(ENTITY_NAME);
    }

    public Long getWorkerId() {
        return getNumber(WORKER);
    }

    public RewardRank setWorkerId(Long workerId) {
        setNumber(WORKER, workerId);

        return this;
    }

    public Long getRank() {
        return getNumber(RANK);
    }

    public void setRank(Long rank) {
        setNumber(RANK, rank);
    }

    public Long getPeriodId() {
        return getNumber(PERIOD);
    }

    public RewardRank setPeriodId(Long periodId) {
        setNumber(PERIOD, periodId);

        return this;
    }
}
