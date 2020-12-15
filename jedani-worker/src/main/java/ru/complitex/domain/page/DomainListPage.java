package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.SortProperty;
import ru.complitex.common.wicket.datatable.DataProvider;
import ru.complitex.common.wicket.datatable.FilterDataForm;
import ru.complitex.common.wicket.datatable.FilterDataTable;
import ru.complitex.domain.component.datatable.DomainActionColumn;
import ru.complitex.domain.component.datatable.DomainColumn;
import ru.complitex.domain.component.datatable.DomainIdColumn;
import ru.complitex.domain.component.datatable.DomainParentColumn;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.entity.Entity;
import ru.complitex.domain.entity.EntityAttribute;
import ru.complitex.domain.entity.Status;
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
public class DomainListPage<T extends Domain<T>> extends BasePage{
    public static final String CURRENT_PAGE_ATTRIBUTE = "_PAGE";

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private Class<T> domainClass;

    private Class<? extends WebPage> editPageClass;

    private FilterWrapper<T> filterWrapper;

    private boolean addVisible = true;

    private WebMarkupContainer container;

    private FeedbackPanel feedback;

    private FilterDataTable<T> table;

    private Label titleLabel;

    public <P extends WebPage> DomainListPage(Class<T> domainClass, String parentEntityName, Long parentEntityAttributeId,
                          Class<P> editPageClass) {
        this.domainClass = domainClass;

        this.editPageClass = editPageClass;

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
                protected Domain<?> getDomain(Long objectId) {
                    return domainService.getDomain(parentEntityName, objectId);
                }
            });
        }

        List<EntityAttribute> entityAttributes = getEntityAttributes(entity);

        List<Long> entityAttributeIds = getEntityAttributeIds();

        if (entityAttributeIds != null){
            entityAttributeIds.forEach(id -> columns.add(newDomainColumn(entity.getEntityAttribute(id))));

        }else {
            entityAttributes.forEach(a -> columns.add(newDomainColumn(a)));
        }

        onAddColumns(columns);

        if (editPageClass != null) {
            columns.add(newDomainActionColumn());
        }

        table = new FilterDataTable<T>("table", columns, dataProvider, form, 15, "domainListPage" + entity.getName()){
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
                return addVisible;
            }
        });
    }

    protected FilterWrapper<T> newFilterWrapper(T domainObject) {
        return FilterWrapper.of(domainObject);
    }

    protected DomainColumn<T> newDomainColumn(EntityAttribute entityAttribute) {
        return new DomainColumn<>(entityAttribute, displayModel(entityAttribute));
    }

    protected IModel<String> displayModel(EntityAttribute entityAttribute){
        return null;
    }

    public DomainListPage(Class<T> domainInstance, Class<? extends WebPage> editPageClass) {
        this(domainInstance, null, null, editPageClass);
    }

    public DomainListPage(Class<T> domainInstance) {
        this(domainInstance, null, null, null);
    }

    public void setAddVisible(boolean addVisible) {
        this.addVisible = addVisible;
    }

    protected void onAdd(AjaxRequestTarget target) {
        PageParameters pageParameters = new PageParameters().add("new", "");

        onAddPageParameters(pageParameters);

        setResponsePage(editPageClass, pageParameters);
    }

    protected void onRowItem(Item<T> item){
        if (editPageClass != null) {
            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    getSession().setAttribute(DomainListPage.this.getClass().getName() + CURRENT_PAGE_ATTRIBUTE, table.getCurrentPage());

                    PageParameters pageParameters = new PageParameters().add("id", item.getModelObject().getObjectId());

                    DomainListPage.this.onEditPageParameters(pageParameters);

                    setResponsePage(editPageClass, pageParameters);
                }
            });

            item.add(new CssClassNameAppender("pointer"));
        }

        if (item.getModelObject().getStatus().equals(Status.ARCHIVE)){
            item.add(new CssClassNameAppender("active"));
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

    protected boolean isShowHeader(){
        return true;
    }

    protected void onDataLoad(List<T> list){
    }

    protected void onAddColumns(List<IColumn<T, SortProperty>> columns){
    }

    protected List<Long> getEntityAttributeIds(){
        return null;
    }

    protected void onEditPageParameters(PageParameters pageParameters){
    }

    protected void onAddPageParameters(PageParameters pageParameters){
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

    protected IColumn<T, SortProperty> newDomainActionColumn(){
        return new DomainActionColumn<T>(editPageClass);
    }


}
