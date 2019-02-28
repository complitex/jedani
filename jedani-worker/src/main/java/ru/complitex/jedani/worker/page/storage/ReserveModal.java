package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Sale;
import ru.complitex.jedani.worker.entity.SaleItem;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.jedani.worker.util.Nomenclatures;
import ru.complitex.name.service.NameService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 20.02.2019 17:42
 */
public class ReserveModal extends Modal<Product> {
    @Inject
    private DomainService domainService;

    @Inject
    private WorkerService workerService;

    @Inject
    private NameService nameService;

    private IModel<Product> productModel;

    private WebMarkupContainer container;

    public ReserveModal(String markupId) {
        super(markupId);

        setBackdrop(Backdrop.FALSE);
        size(Size.Large);

        productModel = new Model<>(new Product());

        header(new ResourceModel("header"));

        container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        container.setOutputMarkupId(true);
        add(container);

        container.add(new TextFieldFormGroup<>("nomenclature",
                new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return Nomenclatures.getNomenclatureLabel(productModel.getObject().getNomenclatureId(),
                                domainService);
                    }
                }).setEnabled(false));

        container.add(new TextFieldFormGroup<>("quantity",
                new LoadableDetachableModel<Long>() {
                    @Override
                    protected Long load() {
                        return productModel.getObject().getQuantity();
                    }
                }).setEnabled(false));

        container.add(new ListView<SaleItem>("saleItems", new LoadableDetachableModel<List<SaleItem>>() {
            @Override
            protected List<SaleItem> load() {
                Product product = productModel.getObject();

                if (product.getObjectId() == null){
                    return Collections.emptyList();
                }

                SaleItem filter = new SaleItem();
                filter.setNomenclatureId(product.getNomenclatureId());
                filter.setStorageId(product.getParentId());

                return domainService.getDomains(SaleItem.class, FilterWrapper.of(filter));
            }
        }) {
            @Override
            protected void populateItem(ListItem<SaleItem> item) {
                SaleItem saleItem = item.getModelObject();

                item.add(new Label("id", saleItem.getObjectId()));

                Sale sale = domainService.getDomain(Sale.class, saleItem.getParentId());
                Worker worker = domainService.getDomain(Worker.class, sale.getSellerWorkerId());

                item.add(new Label("seller", worker != null ? workerService.getWorkerLabel(worker) : ""));

                item.add(new Label("buyer", nameService.getFio(sale.getBuyerLastName(), sale.getBuyerFirstName(),
                        sale.getBuyerMiddleName())));

                item.add(new Label("quantity", saleItem.getQuantity()));
            }
        });

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Default) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ReserveModal.this.close(target);
            }
        }.setLabel(new ResourceModel("cancel")));
    }

    void open(Product product, AjaxRequestTarget target){
        productModel.setObject(product);

        container.setVisible(true);
        target.add(container);

        appendShowDialogJavaScript(target);
    }

    private void close(AjaxRequestTarget target){
        appendCloseDialogJavaScript(target);
    }
}
