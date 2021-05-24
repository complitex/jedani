package ru.complitex.jedani.worker.page.ratio;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.mybatis.cdi.Transactional;
import ru.complitex.address.entity.Country;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.common.wicket.form.FormGroupDecimalField;
import ru.complitex.domain.component.form.AbstractEditModal;
import ru.complitex.domain.component.form.FormGroupDomainAutoComplete;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Ratio;

import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
public class RatioModal extends AbstractEditModal<Ratio> {
    @Inject
    private DomainService domainService;

    private final IModel<Ratio> model;

    public RatioModal(String markupId) {
        super(markupId);

        model = Model.of(new Ratio());

        add(new FormGroupDateTextField("dateBegin", model, Ratio.BEGIN).setRequired(true));

        add(new FormGroupDateTextField("dateEnd", model, Ratio.END));

        add(new FormGroupDomainAutoComplete<>("country", Country.class, Country.NAME, NumberAttributeModel.of(model, Ratio.COUNTRY)).setRequired(true));

        add(new FormGroupDecimalField("value", model, Ratio.VALUE).setRequired(true));
    }

    @Override
    public void create(AjaxRequestTarget target) {
        super.create(target);

        Ratio ratio = new Ratio();

        model.setObject(ratio);
    }

    @Override
    public void edit(Ratio object, AjaxRequestTarget target) {
        super.edit(object, target);

        model.setObject(object);
    }

    @Override
    @Transactional
    protected void save(AjaxRequestTarget target) {
        super.save(target);

        Ratio ratio = model.getObject();

        domainService.getDomains(Ratio.class, FilterWrapper.of(new Ratio().setCountryId(ratio.getCountryId())))
                .forEach(r  -> {
                    if (r.getEnd() == null) {
                        r.setEnd(ratio.getBegin());

                        domainService.save(r);
                    }
                });

        domainService.save(model.getObject());

        success(getString("info_ratio_saved"));
    }
}
