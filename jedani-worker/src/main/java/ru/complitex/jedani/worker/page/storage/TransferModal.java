package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelectConfig;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import ru.complitex.common.wicket.form.FormGroupPanel;
import ru.complitex.common.wicket.form.FormGroupSelectPanel;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.component.form.DomainAutoCompleteFormGroup;
import ru.complitex.domain.entity.StringType;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.component.StorageAutoComplete;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.*;
import ru.complitex.jedani.worker.service.StorageService;
import ru.complitex.jedani.worker.util.Nomenclatures;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;

import javax.inject.Inject;
import java.util.*;

/**
 * @author Anatoly A. Ivanov
 * 07.11.2018 17:18
 */
class TransferModal extends StorageModal {
    @Inject
    private DomainService domainService;

    @Inject
    private StorageService storageService;

    private Product product;

    private IModel<Integer> tabIndexModel = Model.of(0);

    TransferModal(String markupId, Long storageId, SerializableConsumer<AjaxRequestTarget> onUpdate) {
        super(markupId, storageId, onUpdate);

        List<ITab> tabs = new ArrayList<>();

        tabs.add(new AbstractTab(new ResourceModel("sell")){

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment fragment = new Fragment(panelId, "sellFragment", TransferModal.this);

                fragment.add(getNomenclature());

                FormGroupPanel worker = new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.WORKER_TO)).setRequired(true)
                        .setLabel(Model.of(getString("worker")))){
                    @Override
                    public boolean isVisible() {
                        return Objects.equals(getModelObject().getNumber(Transaction.RECIPIENT_TYPE), RecipientType.WORKER);
                    }
                };
                fragment.add(worker);

                WebMarkupContainer client = new WebMarkupContainer("client"){
                    @Override
                    public boolean isVisible() {
                        return Objects.equals(getModelObject().getNumber(Transaction.RECIPIENT_TYPE), RecipientType.CLIENT);
                    }
                };
                client.setOutputMarkupId(true);
                client.setOutputMarkupPlaceholderTag(true);
                fragment.add(client);

                client.add(new DomainAutoCompleteFormGroup("lastName", LastName.ENTITY_NAME, LastName.NAME,
                        new NumberAttributeModel(getModel(), Transaction.LAST_NAME_TO)).setRequired(true));
                client.add(new DomainAutoCompleteFormGroup("firstName", FirstName.ENTITY_NAME, FirstName.NAME,
                        new NumberAttributeModel(getModel(), Transaction.FIRST_NAME_TO)).setRequired(true));
                client.add(new DomainAutoCompleteFormGroup("middleName", MiddleName.ENTITY_NAME, MiddleName.NAME,
                        new NumberAttributeModel(getModel(), Transaction.MIDDLE_NAME_TO)));

                IModel<Long> recipientModel = new NumberAttributeModel(getModel(), Transaction.RECIPIENT_TYPE);

                fragment.add(new FormGroupSelectPanel("recipient", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                        recipientModel, Arrays.asList(RecipientType.WORKER, RecipientType.CLIENT),
                        new IChoiceRenderer<Long>() {
                            @Override
                            public Object getDisplayValue(Long object) {
                                switch (object.intValue()){
                                    case (int) RecipientType.WORKER:
                                        return getString("worker");

                                    case (int) RecipientType.CLIENT:
                                        return getString("client");
                                }

                                return null;
                            }

                            @Override
                            public String getIdValue(Long object, int index) {
                                return object + "";
                            }

                            @Override
                            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                return Long.valueOf(id);
                            }
                        }).setNullValid(false).add(OnChangeAjaxBehavior.onChange(target -> target.add(worker, client)))));

                fragment.add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                        Transaction.QUANTITY)).setRequired(true).setType(Long.class));

                fragment.add(new TextFieldFormGroup<>("serialNumber", new TextAttributeModel(getModel(), Transaction.SERIAL_NUMBER,
                        StringType.DEFAULT)));

                return fragment;
            }
        });


        tabs.add(new AbstractTab(new ResourceModel("transfer")){

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment fragment = new Fragment(panelId, "transferFragment", TransferModal.this);

                fragment.add(getNomenclature());

                FormGroupPanel storage;

                fragment.add(storage =  new FormGroupPanel("storage", new StorageAutoComplete(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.STORAGE_TO))
                        .setRequired(true)
                        .setLabel(new ResourceModel("storage"))
                        ){
                    @Override
                    public boolean isVisible() {
                        return Objects.equals(getModelObject().getNumber(Transaction.RECIPIENT_TYPE), RecipientType.STORAGE);
                    }
                });

                FormGroupPanel worker = new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.WORKER_TO))
                        .setRequired(true)
                        .setLabel(Model.of(getString("worker")))){
                    @Override
                    public boolean isVisible() {
                        return Objects.equals(getModelObject().getNumber(Transaction.RECIPIENT_TYPE), RecipientType.WORKER);
                    }
                };
                fragment.add(worker);

                IModel<Long> recipientModel = new NumberAttributeModel(getModel(), Transaction.RECIPIENT_TYPE);

                fragment.add(new FormGroupSelectPanel("recipient", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                        recipientModel, Arrays.asList(RecipientType.STORAGE, RecipientType.WORKER),
                        new IChoiceRenderer<Long>() {
                            @Override
                            public Object getDisplayValue(Long object) {
                                switch (object.intValue()){
                                    case (int) RecipientType.STORAGE:
                                        return getString("storage");

                                    case (int) RecipientType.WORKER:
                                        return getString("worker");
                                }

                                return null;
                            }

                            @Override
                            public String getIdValue(Long object, int index) {
                                return object + "";
                            }

                            @Override
                            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                return Long.valueOf(id);
                            }
                        }).setNullValid(false).add(OnChangeAjaxBehavior.onChange(target -> target.add(worker, storage)))));

                fragment.add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                        Transaction.QUANTITY)).setRequired(true).setType(Long.class));

                fragment.add(new FormGroupSelectPanel("type", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.TRANSFER_TYPE),
                        Arrays.asList(TransferType.TRANSFER, TransferType.GIFT),
                        new IChoiceRenderer<Long>() {
                            @Override
                            public Object getDisplayValue(Long object) {
                                switch (object.intValue()){
                                    case (int) TransferType.TRANSFER:
                                        return getString("transfer");

                                    case (int) TransferType.GIFT:
                                        return getString("gift");
                                }

                                return null;
                            }

                            @Override
                            public String getIdValue(Long object, int index) {
                                return object + "";
                            }

                            @Override
                            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                            }
                        }).with(new BootstrapSelectConfig().withNoneSelectedText(""))));


                return fragment;
            }
        });


        tabs.add(new AbstractTab(new ResourceModel("withdraw")){

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment fragment = new Fragment(panelId, "withdrawFragment", TransferModal.this);

                fragment.add(getNomenclature());

                fragment.add(new FormGroupSelectPanel("withdrawType", new BootstrapSelect<>(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.TRANSFER_TYPE),
                        Arrays.asList(TransferType.TRANSFER, TransferType.GIFT),
                        new IChoiceRenderer<Long>() {
                            @Override
                            public Object getDisplayValue(Long object) {
                                switch (object.intValue()){
                                    case (int) TransferType.TRANSFER:
                                        return getString("good");

                                    case (int) TransferType.GIFT:
                                        return getString("gift");
                                }

                                return null;
                            }

                            @Override
                            public String getIdValue(Long object, int index) {
                                return object + "";
                            }

                            @Override
                            public Long getObject(String id, IModel<? extends List<? extends Long>> choices) {
                                return id != null && !id.isEmpty() ? Long.valueOf(id) : null;
                            }
                        }).with(new BootstrapSelectConfig().withNoneSelectedText(""))));

                fragment.add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                        Transaction.QUANTITY)).setRequired(true).setType(Long.class).setRequired(true));

                fragment.add(new TextFieldFormGroup<>("comments", new TextAttributeModel(getModel(), Transaction.COMMENTS,
                        StringType.DEFAULT)));

                return fragment;
            }
        });

        getContainer().add(new AjaxBootstrapTabbedPanel<ITab>("tabs", tabs, tabIndexModel){
            @Override
            protected void onAjaxUpdate(Optional<AjaxRequestTarget> targetOptional) {
                targetOptional.ifPresent(t -> t.add(getFeedback()));
                targetOptional.ifPresent(t -> updateTabs(t));
            }
        });
    }

    private Component getNomenclature(){
        return new TextFieldFormGroup<>("nomenclature", new ResourceModel("nomenclature"),
                new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return Nomenclatures.getNomenclatureLabel(product.getNumber(Product.NOMENCLATURE), domainService);
                    }
                }).setEnabled(false);
    }

    void open(Product product, Long transferType, AjaxRequestTarget target){
        this.product = product;

        tabIndexModel.setObject(0);

        open(target);

        getModelObject().setNumber(Transaction.TRANSFER_TYPE, transferType);
    }

    private void updateTabs(AjaxRequestTarget target){
        String label;

        switch (tabIndexModel.getObject()){
            case 0:
                getModelObject().setNumber(Transaction.RECIPIENT_TYPE, RecipientType.WORKER);
                label = getString("sellAction");

                break;
            case 1:
                getModelObject().setNumber(Transaction.RECIPIENT_TYPE, RecipientType.STORAGE);
                label = getString("transferAction");

                break;
            case 2:
                label = getString("withdrawAction");

                break;
            default:
                label = getString("action");
        }

        BootstrapAjaxButton actionButton = getActionButton();

        actionButton.setLabel(Model.of(label));
        target.add(actionButton);
    }

    public Product getProduct() {
        return product;
    }

    public IModel<Integer> getTabIndexModel() {
        return tabIndexModel;
    }

    @Override
    void action(AjaxRequestTarget target) {
        Transaction transaction = getModelObject();

        Product product = domainService.getDomain(Product.class, getProduct().getObjectId());

        boolean gift = Objects.equals(transaction.getNumber(Transaction.TRANSFER_TYPE), TransferType.GIFT);

        Long tQty = transaction.getNumber(gift ? TransferType.GIFT : Transaction.QUANTITY);
        Long pQty = product.getNumber(gift ? TransferType.GIFT : Product.QUANTITY);

        if (tQty > pQty){
            error(getString("error_quantity") + ": " + tQty + " > " + pQty);
            target.add(getFeedback());

            return;
        }

        if (tQty < 1){
            error(getString("error_quantity") + ": " + tQty + " < 1 ");
            target.add(getFeedback());

            return;
        }

        if (Objects.equals(transaction.getNumber(Transaction.RECIPIENT_TYPE), RecipientType.WORKER)){
            Worker w = domainService.getDomain(Worker.class, transaction.getNumber(Transaction.WORKER_TO));

            if (Objects.equals(w.getNumber(Worker.TYPE), 1L)){
                error(getString("error_participant"));
                target.add(getFeedback());

                return;
            }
        }

        switch (getTabIndexModel().getObject()){
            case 0:
                storageService.sell(product, transaction);
                success(getString("info_sold"));

                break;
            case 1:
                if (Objects.equals(transaction.getNumber(Transaction.RECIPIENT_TYPE), RecipientType.STORAGE)
                        && Objects.equals(transaction.getNumber(Transaction.STORAGE_TO), getStorageId())){
                    error(getString("error_same_storage"));
                    target.add(getFeedback());

                    return;
                }

                storageService.transfer(product, transaction);
                success(getString("info_transferred"));

                break;
            case 2:
                storageService.withdraw(product, transaction);
                success(getString("info_withdrew"));

                break;
        }

        close(target);

        onUpdate(target);
    }
}
