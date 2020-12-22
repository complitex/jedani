package ru.complitex.jedani.worker.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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

        DropDownChoice<Period> period = new DropDownChoice<>("period", Model.of(new Period()),
                periodService.getPeriods(), new IChoiceRenderer<Period>() {
            @Override
            public Object getDisplayValue(Period period) {
                return Dates.getMonthText(period.getOperatingMonth());
            }

            @Override
            public String getIdValue(Period period, int index) {
                return period.getObjectId() + "";
            }

            @Override
            public Period getObject(String id, IModel<? extends List<? extends Period>> choices) {
                return choices.getObject().stream()
                        .filter(p -> (p.getObjectId() + "").equals(id))
                        .findFirst()
                        .orElse(null);
            }
        });

        period.setNullValid(true);

        period.add(OnChangeAjaxBehavior.onChange(target -> {
            onChange(target, period.getModelObject());
        }));

        add(period);
    }

    protected void onChange(AjaxRequestTarget target, Period period){

    }
}
