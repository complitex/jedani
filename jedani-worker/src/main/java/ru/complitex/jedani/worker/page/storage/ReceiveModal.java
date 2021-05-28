package ru.complitex.jedani.worker.page.storage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.form.FormGroupDateTextField;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Nomenclature;
import ru.complitex.jedani.worker.entity.Product;
import ru.complitex.jedani.worker.entity.Transfer;
import ru.complitex.jedani.worker.entity.TransferRelocationType;
import ru.complitex.jedani.worker.mapper.TransferMapper;
import ru.complitex.jedani.worker.service.StorageService;
import ru.complitex.jedani.worker.util.Nomenclatures;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 16.11.2018 15:20
 */
class ReceiveModal extends StorageModal {
    @Inject
    private DomainService domainService;

    @Inject
    private TransferMapper transferMapper;

    @Inject
    private StorageService storageService;

    private final IModel<Date> receiveDateModel = Model.of(new Date());

    private final IModel<List<Transfer>> transfersModel = new ListModel<>();

    public ReceiveModal(String markupId, Long storageId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, storageId, onUpdate);

        getContainer().add(new FormGroupDateTextField("receiveDate", receiveDateModel)
                .setRequired(true)
                .onUpdate(t -> {}));

        ListView<Transfer> transfers = new ListView<>("transfers",  transfersModel) {
            @Override
            protected void populateItem(ListItem<Transfer> item) {
                Transfer transfer = item.getModelObject();

                item.add(new CheckBox("check", new PropertyModel<>(transfer, "map.check")).add(OnChangeAjaxBehavior.onChange(t -> {})));
                item.add(new Label("date", transfer.getDate()));
                item.add(new Label("name", Nomenclatures.getNomenclatureLabel(domainService.getDomain(Nomenclature.class, transfer.getNomenclatureId()))));
                item.add(new Label("qty", transfer.getQuantity()));
            }
        };

        transfers.setReuseItems(true);

        getContainer().add(transfers);
    }

    void open(Product product, Long relocationType, AjaxRequestTarget target){
        List<Transfer> transfers = transferMapper.getTransfers(FilterWrapper.of(
                new Transfer()).put(Transfer.FILTER_STORAGE_TO_ID, product.getParentId())
                .put(relocationType == TransferRelocationType.GIFT ? Transfer.FILTER_RECEIVING_GIFT : Transfer.FILTER_RECEIVING, true));

        if (!transfers.isEmpty()){
            transfersModel.setObject(transfers);

            target.add(getContainer());

            open(target);
        }
    }

    void action(AjaxRequestTarget target) {
        transfersModel.getObject().forEach(transfer -> {
            Boolean checked = (Boolean) transfer.getMap().get("check");

            if (checked != null && checked) {
                transfer.setReceiveDate(receiveDateModel.getObject());

                storageService.receive(transfer);

                success(getString("info_received"));
            }
        });

        close(target);

        onUpdate(target);
    }
}
