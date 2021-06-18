package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.component.datatable.*;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
public class DomainListModalPage<T extends Domain> extends BasePage{
    public static final String CURRENT_PAGE_ATTRIBUTE = "_PAGE";

    public static final String DOMAIN_EDIT_MODAL_ID = "edit";

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private final Class<T> domainClass;

    private final FilterWrapper<T> filterWrapper;

    private final WebMarkupContainer container;

    private final FeedbackPanel feedback;

    private final Table<T> table;

    private final Label titleLabel;

    private AbstractModal<T> domainModal;

    public <P extends Domain> DomainListModalPage(Class<T> domainClass, Class<P> parentClass, Long parentEntityAttributeId) {
        this.domainClass = domainClass;

        T domainObject = Domains.newObject(domainClass);

        Entity entity = entityService.getEntity(domainObject.getEntityName());

        String title = entity.getValue() != null ? entity.getValue().getText() : "[" + domainObject.getEntityName() + "]";

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        add(titleLabel = new Label("title", title));

        container.add(new Label("header", title).setVisible(false));

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        filterWrapper = newFilterWrapper(domainObject);

        Provider<T> provider = new Provider<T>(filterWrapper) {

            @Override
            public List<T> getList() {
                return getDomains(filterWrapper);
            }

            @Override
            public Long getCount() {
                return getDomainsCount(getFilterState());
            }

        };

        FilterForm<FilterWrapper<T>> form = new FilterForm<>("form", provider);
        form.setOutputMarkupId(true);
        container.add(form);

        List<IColumn<T, Sort>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());

        if (parentClass != null){
            Entity parentEntity = entityService.getEntity(parentClass);

            columns.add(new DomainParentColumn<T>(Model.of(parentEntity.getValue().getText()),
                    parentEntity.getEntityAttribute(parentEntityAttributeId)) {
                @Override
                protected P getDomain(Long objectId) {
                    return domainService.getDomain(parentClass, objectId);
                }
            });
        }

        getEntityAttributes(entityService.getEntity(domainObject.getEntityName()))
                .forEach(a -> columns.add(newDomainColumn(a)));

        onInitColumns(columns);

        if (isEditEnabled()) {
            columns.add(new DomainEditActionsColumn<T>() {
                @Override
                protected void onEdit(IModel<T> rowModel, AjaxRequestTarget target) {
                    DomainListModalPage.this.onEdit(rowModel.getObject(), target);
                }

                @Override
                protected void populateAction(RepeatingView repeatingView, IModel<T> rowModel) {
                    DomainListModalPage.this.populateAction(repeatingView, rowModel);
                }
            });
        }

        table = new Table<T>("table", columns, provider, 15, "domainListModalPage" + entity.getName()){
            @Override
            protected Item<T> newRowItem(String id, int index, IModel<T> model) {
                Item<T> item = super.newRowItem(id, index, model);

                onRowItem(item);

                if (item.getModelObject().getStatus().equals(Status.ARCHIVE)){
                    item.add(new CssClassNameAppender("danger"));
                }

                return item;
            }
        };
        table.setCurrentPage((Long) Optional.ofNullable(getSession().getAttribute(getClass().getName() +
                CURRENT_PAGE_ATTRIBUTE)).orElse(0L));
        form.add(table);

        container.add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCreate(target);
            }

            @Override
            public boolean isVisible() {
                return isCreateEnabled();
            }
        });

        Form<T> editForm = new Form<>("editForm");
        container.add(editForm);

        if (isEditEnabled() && isDomainModalEditEnabled()) {
            domainModal = newDomainModal(DOMAIN_EDIT_MODAL_ID);

            if (domainModal == null) {
                domainModal = new DomainModal<T>(DOMAIN_EDIT_MODAL_ID, domainClass, parentClass,
                        parentEntityAttributeId, getEditEntityAttributes(entityService.getEntity(Domains.getEntityName(domainClass))),
                        t -> t.add(feedback, table)){
                    @Override
                    protected boolean validate(Domain domain) {
                        return DomainListModalPage.this.validate(domain);
                    }

                    @Override
                    protected Component newComponent(String componentId, Attribute attribute) {
                        return DomainListModalPage.this.newEditComponent(componentId, attribute);
                    }
                };
            }

            editForm.add(domainModal);
        }else{
            editForm.add(new EmptyPanel("edit"));
        }
    }

    protected FilterWrapper<T> newFilterWrapper(T domainObject) {
        return FilterWrapper.of(domainObject);
    }

    public DomainListModalPage(Class<T> domainInstance) {
        this(domainInstance, null, null);
    }

    protected AbstractModal<T> newDomainModal(String componentId) {
        return null;
    }

    protected Component newEditComponent(String componentId, Attribute attribute) {
        return null;
    }

    protected boolean validate(Domain domain) {
        return true;
    }

    protected void onCreate(AjaxRequestTarget target) {
        domainModal.create(target);
    }

    protected T newDomain(){
        return Domains.newObject(domainClass);
    }

    protected AbstractDomainColumn<T> newDomainColumn(EntityAttribute a) {
        return new DomainColumn<>(a);
    }

    protected void onEdit(T object, AjaxRequestTarget target) {
        domainModal.edit(object.getObjectId(), target);
    }

    protected void onRowItem(Item<T> item){
        item.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onRowClick(item.getModelObject(), target);
            }
        });

        item.add(new CssClassNameAppender("pointer"));
    }

    protected void onRowClick(T object, AjaxRequestTarget target) {
        if (isEditEnabled()) {
            onEdit(object, target);
        }
    }

    protected List<T> getDomains(FilterWrapper<T> filterWrapper) {
        return domainService.getDomains(domainClass, filterWrapper);
    }

    protected Long getDomainsCount(FilterWrapper<T> filterWrapper) {
        return domainService.getDomainsCount(filterWrapper);
    }

    protected List<EntityAttribute> getEntityAttributes(Entity entity){
        return entity.getAttributes();
    }

    protected List<EntityAttribute> getEditEntityAttributes(Entity entity){
        return getEntityAttributes(entity);
    }

    protected void onInitColumns(List<IColumn<T, Sort>> columns){
    }

    public FilterWrapper<T> getFilterWrapper() {
        return filterWrapper;
    }

    public WebMarkupContainer getContainer() {
        return container;
    }

    public void updateFeedback(AjaxRequestTarget target){
        target.add(feedback);
    }

    public void updateTable(AjaxRequestTarget target){
        target.add(table);
    }

    public void update(AjaxRequestTarget target){
        updateFeedback(target);
        updateTable(target);
    }

    protected void title(IModel<String> titleModel){
        titleLabel.setDefaultModel(titleModel);
    }

    protected boolean isEditEnabled(){
        return true;
    }

    protected boolean isDomainModalEditEnabled(){
        return true;
    }

    protected boolean isCreateEnabled(){
        return true;
    }

    protected void populateAction(RepeatingView repeatingView, IModel<T> rowModel){

    }

}
