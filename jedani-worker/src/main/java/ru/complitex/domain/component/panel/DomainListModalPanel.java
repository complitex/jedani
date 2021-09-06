package ru.complitex.domain.component.panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.Sort;
import ru.complitex.common.wicket.table.FilterForm;
import ru.complitex.common.wicket.table.Provider;
import ru.complitex.common.wicket.table.Table;
import ru.complitex.domain.component.datatable.*;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.page.AbstractModal;
import ru.complitex.domain.page.DomainModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 11.12.2020 21:10
 */
public class DomainListModalPanel<T extends Domain> extends Panel {
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

    private AbstractModal<T> domainEditModal;

    private final Long parentEntityAttributeId;

    private Entity entity;

    public <P extends Domain> DomainListModalPanel(String id, Class<T> domainClass, Class<P> parentClass, Long parentEntityAttributeId) {
        super(id);

        this.domainClass = domainClass;

        this.parentEntityAttributeId = parentEntityAttributeId;

        T domainObject = Domains.newObject(domainClass);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);


        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        filterWrapper = newFilterWrapper(domainObject);

        Provider<T> provider = new Provider<>(filterWrapper) {
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

            columns.add(new DomainParentColumn<>(Model.of(parentEntity.getValue().getText()),
                    parentEntity.getEntityAttribute(parentEntityAttributeId)) {
                @Override
                protected Domain getDomain(Long objectId) {
                    return domainService.getDomain(parentClass, objectId);
                }
            });
        }

        entity = entityService.getEntity(domainObject.getEntityName());

        getEntityAttributes(entity).forEach(a -> columns.add(newDomainColumn(a)));

        onInitColumns(columns);

        if (isEditEnabled()) {
            columns.add(new DomainEditActionsColumn<>() {
                @Override
                protected void onEdit(IModel<T> rowModel, AjaxRequestTarget target) {
                    DomainListModalPanel.this.onEdit(rowModel.getObject(), target);
                }

                @Override
                protected void populateAction(RepeatingView repeatingView, IModel<T> rowModel) {
                    DomainListModalPanel.this.populateAction(repeatingView, rowModel);
                }
            });
        }

        table = new Table<>("table", columns, provider, 15,
                "domainListModalPage" + domainClass.getName()){
            @Override
            protected Item<T> newRowItem(String id, int index, IModel<T> model) {
                Item<T> item = super.newRowItem(id, index, model);

                onRowItem(item);

                if (item.getModelObject().getStatus().equals(Status.ARCHIVE)){
                    item.add(new CssClassNameAppender("danger"));
                }

                return item;
            }

            @Override
            protected Component newPagingLeft(String id) {
                return DomainListModalPanel.this.getPagingLeft(id);
            }

            @Override
            protected IModel<String> getCaptionModel() {
                return DomainListModalPanel.this.getCaptionModel();
            }

            @Override
            protected AbstractToolbar newFooter(Table<T> table) {
                return DomainListModalPanel.this.newFooter(table);
            }
        };
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
            domainEditModal = newDomainEditModal(DOMAIN_EDIT_MODAL_ID, parentClass);

            editForm.add(domainEditModal);
        }else{
            editForm.add(new EmptyPanel("edit"));
        }
    }

    public DomainListModalPanel(String id, Class<T> domainClass) {
        this(id, domainClass, null, null);
    }

    public <P extends Domain> AbstractModal<T> newDomainEditModal(String componentId, Class<P> parentClass) {
        return new DomainModal<>(componentId, domainClass, parentClass,
                parentEntityAttributeId, getEditEntityAttributes(entityService.getEntity(Domains.getEntityName(domainClass))),
                t -> t.add(feedback, table)){
            @Override
            protected boolean checkUnique(Domain domain) {
                return DomainListModalPanel.this.validate(domain);
            }

            @Override
            protected Component newComponent(String componentId, Attribute attribute) {
                return DomainListModalPanel.this.getEditComponent(componentId, attribute);
            }
        };
    }

    protected FilterWrapper<T> newFilterWrapper(T domainObject) {
        return FilterWrapper.of(domainObject);
    }

    public Component getEditComponent(String componentId, Attribute attribute) {
        return null;
    }

    public boolean validate(Domain domain) {
        return true;
    }

    protected void onCreate(AjaxRequestTarget target) {
        domainEditModal.create(target);
    }

    protected AbstractDomainColumn<T> newDomainColumn(EntityAttribute a) {
        return new DomainColumn<>(a);
    }

    protected void onEdit(T object, AjaxRequestTarget target) {
        domainEditModal.edit(object.getObjectId(), target);
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

    public void onRowClick(T object, AjaxRequestTarget target) {
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

    public List<EntityAttribute> getEditEntityAttributes(Entity entity){
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

    public boolean isEditEnabled(){
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

    private HttpServletRequest getHttpServletRequest(){
        return ((HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest());
    }

    public String getLogin(){
        return getHttpServletRequest().getUserPrincipal().getName();
    }

    protected boolean isUserInRole(String role){
        return getHttpServletRequest().isUserInRole(role);
    }

    protected Component getPagingLeft(String id) {
        return null;
    }

    protected IModel<String> getCaptionModel() {
        return null;
    }

    protected AbstractToolbar newFooter(Table<T> table) {
        return null;
    }

    public Entity getEntity() {
        return entity;
    }
}
