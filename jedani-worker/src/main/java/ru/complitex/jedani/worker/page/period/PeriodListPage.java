package ru.complitex.jedani.worker.page.period;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.GlyphIconType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.component.DateTimeLabel;
import ru.complitex.domain.component.datatable.AbstractActionsColumn;
import ru.complitex.domain.component.datatable.AbstractDomainColumn;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.page.DomainListModalPage;
import ru.complitex.jedani.worker.entity.Period;
import ru.complitex.jedani.worker.service.WorkerService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static ru.complitex.jedani.worker.security.JedaniRoles.ADMINISTRATORS;

/**
 * @author Anatoly A. Ivanov
 * 05.11.2019 9:49 PM
 */
@AuthorizeInstantiation({ADMINISTRATORS})
public class PeriodListPage extends DomainListModalPage<Period> {
    @Inject
    private WorkerService workerService;

    private PeriodModal periodModal;
    private PeriodCalculateModal periodCalculateModal;
    private PeriodCloseModal periodCloseModal;

    public PeriodListPage() {
        super(Period.class);

        boolean init = isCreateEnabled();

        Form periodForm = new Form("periodForm");
        periodForm.setVisible(init);
        getContainer().add(periodForm);
        periodForm.add(periodModal = new PeriodModal("periodModal").onUpdate(t -> t.add(getContainer())));


        Form periodCalculateForm = new Form("periodCalculateForm");
        periodCalculateForm.setVisible(!init);
        getContainer().add(periodCalculateForm);
        periodCalculateForm.add(periodCalculateModal = new PeriodCalculateModal("periodCalculateModal")
                .onUpdate(this::update));


        Form periodCloseForm = new Form("periodCloseForm");
        periodCloseForm.setVisible(!init);
        getContainer().add(periodCloseForm);
        periodCloseForm.add(periodCloseModal = new PeriodCloseModal("periodCloseModal")
                .onUpdate(this::update));


        getContainer().add(new AjaxLink<Void>("calculateRewards") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                periodCalculateModal.create(target);
            }

            @Override
            public boolean isVisible() {
                return !isCreateEnabled();
            }
        });
    }

    @Override
    protected void onInitColumns(List<IColumn<Period, Sort>> iColumns) {
        super.onInitColumns(iColumns);

        iColumns.add(new AbstractActionsColumn<Period>() {
            @Override
            protected List<Component> getActions(String componentId, IModel<Period> rowModel) {
                return Collections.singletonList(new BootstrapAjaxButton(componentId, Buttons.Type.Link) {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target) {
                        periodCloseModal.create(target);

                        updateFeedback(target);
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
                    }

                    @Override
                    public boolean isVisible() {
                        return rowModel.getObject().getCloseTimestamp() == null;
                    }
                }.setIconType(GlyphIconType.ok));
            }
        });
    }

    @Override
    protected AbstractDomainColumn<Period> newDomainColumn(EntityAttribute a) {
        if (a.getEntityAttributeId().equals(Period.CLOSE_TIMESTAMP)){
            return new AbstractDomainColumn<Period>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Period>> cellItem, String componentId, IModel<Period> rowModel) {
                    cellItem.add(new DateTimeLabel(componentId, rowModel.getObject().getCloseTimestamp())
                            .add(AttributeAppender.append("style", "white-space: nowrap")));
                }
            };
        }else if (a.getEntityAttributeId().equals(Period.WORKER)){
            return new AbstractDomainColumn<Period>(a) {
                @Override
                public void populateItem(Item<ICellPopulator<Period>> cellItem, String componentId, IModel<Period> rowModel) {
                    cellItem.add(new Label(componentId, workerService.getWorkerLabel(rowModel.getObject().getWorkerId())));
                }
            };
        }

        return super.newDomainColumn(a);
    }

    @Override
    protected void onCreate(AjaxRequestTarget target) {
        periodModal.create(target);
    }

    @Override
    protected boolean isEditEnabled() {
        return false;
    }
}
