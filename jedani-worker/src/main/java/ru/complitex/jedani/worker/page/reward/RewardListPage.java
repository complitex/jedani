package ru.complitex.jedani.worker.page.reward;

import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Reward;

public class RewardListPage extends DomainListModalPage<Reward> {
    public RewardListPage() {
        super(Reward.class);
    }
}
