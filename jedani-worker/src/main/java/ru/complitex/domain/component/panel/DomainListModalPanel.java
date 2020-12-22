package ru.complitex.domain.component.panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.domain.component.datatable.*;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.page.AbstractDomainEditModal;
import ru.complitex.domain.page.DomainEditModal;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 11.12.2020 21:10
 */
public class DomainListModalPanel<T extends Domain<T>> extends Panel {
    public static final String DOMAIN_EDIT_MODAL_ID = "edit";

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private final Class<T> domainClass;

    private final FilterWrapper<T> filterWrapper;

    private final WebMarkupContainer container;

    private final FeedbackPanel feedback;

    private final FilterDataTable<T> table;

    private AbstractDomainEditModal<T> domainEditModal;

    private final String parentEntityName;
    private final Long parentEntityAttributeId;

    public DomainListModalPanel(String id, Class<T> domainClass, String parentEntityName, Long parentEntityAttributeId) {
        super(id);

        this.domainClass = domainClass;
        this.parentEntityName = parentEntityName;
        this.parentEntityAttributeId = parentEntityAttributeId;

        T domainObject = Domains.newObject(domainClass);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);


        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        filterWrapper = newFilterWrapper(domainObject);

        DataProvider<T> dataProvider = new DataProvider<T>(filterWrapper) {
            @Override
            public Iterator<? extends T> iterator(long first, long count) {
                FilterWrapper<T> filterWrapper = getFilterState().limit(first, count);

                if (getSort() != null){
                    filterWrapper.setSortProperty(getSort().getProperty());
                    filterWrapper.setAscending(getSort().isAscending());
                }else{
                    filterWrapper.setSortProperty(new SortProperty("id"));
                    filterWrapper.setAscending(false);
                }

                List<T> list = getDomains(filterWrapper);

                return list.iterator();
            }

            @Override
            public long size() {
                return getDomainsCount(getFilterState());
            }

        };

        FilterDataForm<FilterWrapper<T>> form = new FilterDataForm<>("form", dataProvider);
        form.setOutputMarkupId(true);
        container.add(form);

        List<IColumn<T, SortProperty>> columns = new ArrayList<>();

        columns.add(new DomainIdColumn<>());

        if (parentEntityName != null){
            Entity parentEntity = entityService.getEntity(parentEntityName);

            columns.add(new DomainParentColumn<T>(Model.of(parentEntity.getValue().getText()),
                    parentEntity.getEntityAttribute(parentEntityAttributeId)) {
                @Override
                protected Domain<?> getDomain(Long objectId) {
                    return domainService.getDomain(parentEntityName, objectId);
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
                    DomainListModalPanel.this.onEdit(rowModel.getObject(), target);
                }

                @Override
                protected void populateAction(RepeatingView repeatingView, IModel<T> rowModel) {
                    DomainListModalPanel.this.populateAction(repeatingView, rowModel);
                }
            });
        }

        table = new FilterDataTable<T>("table", columns, dataProvider, form, 10,
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
            domainEditModal = newDomainEditModal(DOMAIN_EDIT_MODAL_ID);

            editForm.add(domainEditModal);
        }else{
            editForm.add(new EmptyPanel("edit"));
        }
    }

    public DomainListModalPanel(String id, Class<T> domainClass) {
        this(id, domainClass, null, null);
    }

    public AbstractDomainEditModal<T> newDomainEditModal(String componentId) {
        return new DomainEditModal<T>(componentId, domainClass, parentEntityName,
                parentEntityAttributeId, getEditEntityAttributes(entityService.getEntity(Domains.getEntityName(domainClass))),
                t -> t.add(feedback, table)){
            @Override
            protected boolean validate(Domain<T> domain) {
                return DomainListModalPanel.this.validate(domain);
            }

            @Override
            protected Component getComponent(String componentId, Attribute attribute) {
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

    public boolean validate(Domain<T> domain) {
        return true;
    }

    protected void onCreate(AjaxRequestTarget target) {
        domainEditModal.edit(newDomain(), target);
    }

    public T newDomain(){
        return Domains.newObject(domainClass);
    }

    protected AbstractDomainColumn<T> newDomainColumn(EntityAttribute a) {
        return new DomainColumn<>(a);
    }

    protected void onEdit(T object, AjaxRequestTarget target) {
        domainEditModal.edit(object, target);
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

    protected void onInitColumns(List<IColumn<T, SortProperty>> columns){
    }

    public FilterWrapper<T> getFilterWrapper() {
        return filterWrapper;
    }

    public WebMarkupContainer getContainer() {
        return container;
    }

    public FeedbackPanel getFeedback() {
        return feedback;
    }

    public FilterDataTable<T> getTable() {
        return table;
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
}
