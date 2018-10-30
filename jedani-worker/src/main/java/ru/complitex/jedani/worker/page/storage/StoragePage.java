package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.address.entity.City;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.WorkerAutoCompleteList;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov
 * 30.10.2018 14:58
 */
public class StoragePage extends BasePage {
    @Inject
    private DomainService domainService;

    public StoragePage(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toOptionalLong();

        Storage storage = id != null ? domainService.getDomain(Storage.class, id) : new Storage();

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        DataProvider<Product> dataProvider = new DataProvider<Product>(FilterWrapper.of(new Product())) {
            @Override
            public Iterator<? extends Product> iterator(long first, long count) {
                return null;
            }

            @Override
            public long size() {
                return 0;
            }
        };

        FilterForm<FilterWrapper<Product>> form = new FilterForm<>("form", dataProvider);
        add(form);

        form.add(new DomainAutoCompleteFormGroup("city", City.ENTITY_NAME, City.NAME,
                new NumberAttributeModel(storage, Storage.CITY_ID), true));
        form.add(new WorkerAutoCompleteList("workerIds", Model.of())); //todo form group



    }
}
