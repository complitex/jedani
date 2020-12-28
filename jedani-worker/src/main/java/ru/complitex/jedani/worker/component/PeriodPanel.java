package ru.complitex.jedani.worker.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.util.Dates;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.service.PeriodService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 22.12.2020 18:01
 */
public class PeriodPanel extends Panel {
    @Inject
    private PeriodService periodService;

    public PeriodPanel(String id) {
        super(id);

        IModel<List<Period>> periodModel = Model.ofList(periodService.getPeriods());

        IModel<Long> pageModel = Model.of(0L);

        BootstrapAjaxPagingNavigator navigator = new BootstrapAjaxPagingNavigator("navigator",
                new IPageable() {
                    @Override
                    public long getCurrentPage() {
                        return pageModel.getObject();
                    }

                    @Override
                    public void setCurrentPage(long page) {
                        pageModel.setObject(page);
                    }

                    @Override
                    public long getPageCount() {
                        return periodModel.getObject().size();
                    }
                }, (IPagingLabelProvider) page -> Dates.getMonthText(periodModel.getObject().get((int) page).getOperatingMonth())){
            @Override
            protected void onAjaxEvent(AjaxRequestTarget target) {
                onChange(target, periodModel.getObject().get(pageModel.getObject().intValue()));
            }
        };

        add(navigator);
    }

    protected void onChange(AjaxRequestTarget target, Period period){

    }
}
