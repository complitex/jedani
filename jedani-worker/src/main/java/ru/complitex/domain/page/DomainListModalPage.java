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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.datatable.DomainModalActionColumn;
import ru.complitex.domain.component.datatable.DomainParentColumn;
import ru.complitex.domain.entity.Attribute;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author Anatoly A. Ivanov
 * 19.12.2017 3:40
 */
public class DomainListModalPage<T extends Domain<T>> extends BasePage{
    public static final String CURRENT_PAGE_ATTRIBUTE = "_PAGE";

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private Class<T> domainClass;

    private FilterWrapper<T> filterWrapper;

    private WebMarkupContainer container;

    private FeedbackPanel feedback;

    private FilterDataTable<T> table;

    private Label titleLabel;

    private DomainEditModal<T> domainEditModal;

    public DomainListModalPage(Class<T> domainClass, String parentEntityName, Long parentEntityAttributeId) {
        this.domainClass = domainClass;

        T domainInstance = Domains.newObject(domainClass);

        Entity entity = entityService.getEntity(domainInstance.getEntityName());

        String title = entity.getValue() != null ? entity.getValue().getText() : "[" + domainInstance.getEntityName() + "]";

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        add(titleLabel = new Label("title", title));

        container.add(new Label("header", title).setVisible(false));

        feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        filterWrapper = FilterWrapper.of(domainInstance);

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

                onDataLoad(list);

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
                protected Domain getDomain(Long objectId) {
                    return domainService.getDomain(parentEntityName, objectId);
                }
            });
        }

        getEntityAttributes(entityService.getEntity(domainInstance.getEntityName()))
                .forEach(a -> columns.add(newDomainColumn(a)));

        onAddColumns(columns);

        if (isEditEnabled()) {
            columns.add(new DomainModalActionColumn<T>() {
                @Override
                protected void onAction(IModel<T> rowModel, AjaxRequestTarget target) {
                    onActionEdit(rowModel.getObject(), target);
                }
            });
        }

        table = new FilterDataTable<T>("table", columns, dataProvider, form, 15, "domainGridPage" + entity.getName()){
            @Override
            protected Item<T> newRowItem(String id, int index, IModel<T> model) {
                Item<T> item = super.newRowItem(id, index, model);

                onRowItem(item);

                return item;
            }
        };
        table.setCurrentPage((Long) Optional.ofNullable(getSession().getAttribute(getClass().getName() +
                CURRENT_PAGE_ATTRIBUTE)).orElse(0L));
        form.add(table);

        container.add(new AjaxLink<Void>("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAdd(target);
            }

            @Override
            public boolean isVisible() {
                return isAddEnabled();
            }
        });

        Form<T> editForm = new Form<>("editForm");
        container.add(editForm);

        if (isEditEnabled()) {
            domainEditModal = new DomainEditModal<T>("edit", domainClass, parentEntityName,
                    parentEntityAttributeId, getEditEntityAttributes(entityService.getEntity(domainInstance.getEntityName())),
                    t -> t.add(feedback, table)){
                @Override
                protected boolean validate(Domain<T> domain) {
                    return DomainListModalPage.this.validate(domain);
                }

                @Override
                protected Component getComponent(String componentId, Attribute attribute) {
                    return DomainListModalPage.this.getEditComponent(componentId, attribute);
                }
            };

            editForm.add(domainEditModal);
        }else{
            editForm.add(new EmptyPanel("edit"));
        }
    }

    protected Component getEditComponent(String componentId, Attribute attribute) {
        return null;
    }

    protected boolean validate(Domain<T> domain) {
        return true;
    }

    public DomainListModalPage(Class<T> domainInstance) {
        this(domainInstance, null, null);
    }

    protected DomainColumn<T> newDomainColumn(EntityAttribute a) {
        return new DomainColumn<>(a);
    }

    protected void onAdd(AjaxRequestTarget target) {
        domainEditModal.edit(newDomain(), target);
    }

    protected T newDomain(){
        return Domains.newObject(domainClass);
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

    protected void onActionEdit(T object, AjaxRequestTarget target) {
        domainEditModal.edit(object, target);
    }

    protected void onRowClick(T object, AjaxRequestTarget target) {
        onActionEdit(object, target);
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

    protected void onDataLoad(List<T> list){
    }

    protected void onAddColumns(List<IColumn<T, SortProperty>> columns){
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

    protected void title(IModel<String> titleModel){
        titleLabel.setDefaultModel(titleModel);
    }

    protected boolean isEditEnabled(){
        return true;
    }

    protected boolean isAddEnabled(){
        return true;
    }

}
