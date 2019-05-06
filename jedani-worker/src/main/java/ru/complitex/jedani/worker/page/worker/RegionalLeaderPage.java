package ru.complitex.jedani.worker.page.worker;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.jedani.worker.entity.Worker;

/**
 * @author Anatoly A. Ivanov
 * 06.05.2019 14:55
 */
public class RegionalLeaderPage extends WorkerPage{
    public RegionalLeaderPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected FilterWrapper<Worker> newFilterWrapper() {
        return FilterWrapper.of(new Worker()).put(Worker.FILTER_REGION_IDS, getWorker().getNumberValuesString(Worker.REGIONS));
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }

    @Override
    protected boolean isViewOnly() {
        return true;
    }
}
