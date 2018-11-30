package ru.complitex.jedani.worker.page.storage;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.wicket.form.TextFieldFormGroup;
import ru.complitex.domain.component.form.FormGroupPanel;
import ru.complitex.domain.model.NumberAttributeModel;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.jedani.worker.component.StorageAutoCompete;
import ru.complitex.jedani.worker.component.WorkerAutoComplete;
import ru.complitex.jedani.worker.entity.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Anatoly A. Ivanov
 * 07.11.2018 17:18
 */
public abstract class TransferModal extends StorageModal {
    private Long productId;

    private IModel<Integer> tabIndexModel = Model.of(0);

    TransferModal(String markupId) {
        super(markupId);

        List<ITab> tabs = new ArrayList<>();

        tabs.add(new AbstractTab(LoadableDetachableModel.of(() -> getString("sell"))){

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment fragment = new Fragment(panelId, "sellFragment", TransferModal.this);

                fragment.add(new FormGroupPanel("worker", new WorkerAutoComplete(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.WORKER_TO))));

                fragment.add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                        Transaction.QUANTITY)).setType(Long.class));

                fragment.add(new TextFieldFormGroup<>("serialNumber", new TextAttributeModel(getModel(), Transaction.SERIAL_NUMBER,
                        TextAttributeModel.TYPE.DEFAULT)));

                return fragment;
            }
        });


        tabs.add(new AbstractTab(LoadableDetachableModel.of(() -> getString("transfer"))){

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment fragment = new Fragment(panelId, "transferFragment", TransferModal.this);

                fragment.add(new FormGroupPanel("storage", new StorageAutoCompete(FormGroupPanel.COMPONENT_ID,
                        new NumberAttributeModel(getModel(), Transaction.STORAGE_TO))));

                fragment.add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                        Transaction.QUANTITY)).setType(Long.class));

                fragment.add(new TextFieldFormGroup<>("type", new NumberAttributeModel(getModel(),
                        Transaction.TRANSFER_TYPE)).setType(Long.class));

                return fragment;
            }
        });


        tabs.add(new AbstractTab(LoadableDetachableModel.of(() -> getString("withdraw"))){

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                Fragment fragment = new Fragment(panelId, "withdrawFragment", TransferModal.this);

                fragment.add(new TextFieldFormGroup<>("quantity", new NumberAttributeModel(getModel(),
                        Transaction.QUANTITY)).setType(Long.class));

                fragment.add(new TextFieldFormGroup<>("comments", new TextAttributeModel(getModel(), Transaction.COMMENTS,
                        TextAttributeModel.TYPE.DEFAULT)));

                return fragment;
            }
        });

        add(new AjaxBootstrapTabbedPanel<ITab>("tabs", tabs, tabIndexModel){
            @Override
            protected void onAjaxUpdate(Optional<AjaxRequestTarget> targetOptional) {
                targetOptional.ifPresent(t -> t.add(getFeedback()));
                targetOptional.ifPresent(t -> updateActionLabel(t));
            }
        });
    }

    void open(Long productId, AjaxRequestTarget target){
        this.productId = productId;

        open(target);
    }

    private void updateActionLabel(AjaxRequestTarget target){
        String label;

        switch (tabIndexModel.getObject()){
            case 0:
                label = getString("sellAction");
                break;
            case 1:
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

    public Long getProductId() {
        return productId;
    }

    public IModel<Integer> getTabIndexModel() {
        return tabIndexModel;
    }
}
