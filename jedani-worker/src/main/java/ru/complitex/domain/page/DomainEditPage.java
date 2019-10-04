package ru.complitex.domain.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.common.wicket.form.FormGroupBorder;
import ru.complitex.domain.component.form.DomainAutoComplete;
import ru.complitex.domain.entity.*;
import ru.complitex.domain.model.TextAttributeModel;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.service.EntityService;
import ru.complitex.domain.util.Domains;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.page.BasePage;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 17.12.2017 21:51
 */
public abstract class DomainEditPage<T extends Domain> extends BasePage{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private EntityService entityService;

    @Inject
    private DomainService domainService;

    private Class<? extends WebPage> backPage;
    private PageParameters backPageParameters;

    private Form form;

    public DomainEditPage(Class<T> domainClass, PageParameters parameters, Class<? extends WebPage> backPage) {
        this.backPage = backPage;

        Long objectId = parameters.get("id").toOptionalLong();

        T domain = objectId != null ? domainService.getDomain(domainClass, objectId) : Domains.newObject(domainClass);

        if (domain == null){
            throw new WicketRuntimeException("domain not found");
        }

        Entity entity = entityService.getEntity(domain.getEntityName());

        String title = entity.getValue().getText();

        add(new Label("title", title));

        add(new Label("header", title));

        FeedbackPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        form = new BootstrapForm("form");
        add(form);

        //Parent

        Entity parentEntity = getParentEntityName() != null ? entityService.getEntity(getParentEntityName()) : null;

        if (parentEntity != null) {
            FormGroupBorder parentGroup = new FormGroupBorder("parentGroup", Model.of(parentEntity.getValue().getText()));
            form.add(parentGroup);

            parentGroup.add(getParentComponent("parent", parentEntity, domain));
        }else{
            form.add(new EmptyPanel("parentGroup").setVisible(false));
        }

        //Attributes

        List<Long> entityAttributeIds = getEntityAttributeIds();

        List<EntityAttribute> entityAttributes = entityAttributeIds != null
                ? entityAttributeIds.stream().map(entity::getEntityAttribute).collect(Collectors.toList())
                : entity.getAttributes();

        ListView listView = new ListView<EntityAttribute>("attributes", entityAttributes) {
            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute entityAttribute = item.getModelObject();

                Attribute attribute = domain.getOrCreateAttribute(entityAttribute.getEntityAttributeId());
                attribute.setEntityAttribute(entityAttribute);
                onAttribute(attribute);
                entityService.loadReference(attribute.getEntityAttribute());

                FormGroupBorder group = new FormGroupBorder("group", Model.of(entityAttribute.getValue().getText())){
                    @Override
                    protected boolean isRequired() {
                        return entityAttribute.isRequired();
                    }
                };
                FormComponent input1 = null;
                FormComponent input2 = null;
                Component component = getComponent("component", attribute);

                if (component == null) {
                    switch (entityAttribute.getValueType()){
                        case TEXT:
                        case DECIMAL:
                        case ENTITY_LIST:
                            input1 = new TextField<>("input1", new TextAttributeModel(attribute, StringType.UPPER_CASE));
                            break;
                        case DATE:
                            input1 = new TextField<>("input1", new PropertyModel<>(attribute, "date"));
                            break;
                        case ENTITY:
                            EntityAttribute referenceEntityAttribute = attribute.getEntityAttribute()
                                    .getReferenceEntityAttributes().get(0);

                            component = new DomainAutoComplete("component", referenceEntityAttribute.getEntityName(),
                                    referenceEntityAttribute, new PropertyModel<>(attribute, "number"));
                            break;
                        case BOOLEAN:
                        case NUMBER:
                            input1 = new TextField<>("input1", new PropertyModel<>(attribute, "number"));
                            break;
                        case TEXT_LIST:
                            input1 = new TextField<>("input1", new TextAttributeModel(attribute.getOrCreateValue(
                                    Locales.getLocaleId(Locales.RU)), StringType.UPPER_CASE));
                            input1.add(new AttributeModifier("placeholder", getString("RU")));

                            input2 = new TextField<>("input2", new TextAttributeModel(attribute.getOrCreateValue(
                                    Locales.getLocaleId(Locales.UA)), StringType.UPPER_CASE));
                            input2.add(new AttributeModifier("placeholder", getString("UA")));

                            break;
                    }
                }

                group.add(input1 != null ? input1 : new EmptyPanel("input1").setVisible(false));
                group.add(input2 != null ? input2 : new EmptyPanel("input2").setVisible(false));
                group.add(component != null ? component : new EmptyPanel("component").setVisible(false));

                item.add(group);
            }
        };
        listView.setReuseItems(true);
        form.add(listView);

        BootstrapAjaxButton save = new BootstrapAjaxButton("save", Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    if (!DomainEditPage.this.validate(domain)){
                        target.add(feedback);

                        return;
                    }

                    domain.setUserId(getCurrentUser().getId());

                    if (parentEntity != null){
                        domain.setParentEntityId(parentEntity.getId());
                    }

                    domainService.save(domain);

                    getSession().success(entity.getValue().getText() + " " + getString("info_saved"));

                    if (getBackPage() != null) {
                        setResponsePage(getBackPage(), getBackPageParameters());
                    }else {
                        target.add(feedback);
                    }
                } catch (Exception e) {
                    log.error("error save domain", e);

                    getSession().error("Ошибка сохранения " + e.getLocalizedMessage());

                    target.add(feedback);
                }
            }
        };
        save.setLabel(new ResourceModel("save"));
        form.add(save);

        BootstrapAjaxButton cancel = new BootstrapAjaxButton("cancel", Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                setResponsePage(getBackPage(), getBackPageParameters());
            }
        };
        cancel.setLabel(new ResourceModel("cancel"));
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        //todo validate -> entity select
    }

    protected DomainAutoComplete getParentComponent(String componentId, Entity parentEntity, T domain) {
        return new DomainAutoComplete(componentId,
                parentEntity.getName(),
                parentEntity.getEntityAttribute(getParentEntityAttributeId()),
                new PropertyModel<>(domain, "parentId"));
    }

    protected String getParentEntityName(){
        return null;
    }

    protected Long getParentEntityAttributeId(){
        return null;
    }

    protected List<Long> getEntityAttributeIds(){
        return null;
    }

    protected boolean isUpperCaseNames(){
        return true;
    }

    protected void onAttribute(Attribute attribute){
    }

    protected Component getComponent(String componentId, Attribute attribute){
        return null;
    }

    protected String getPrefix(EntityAttribute entityAttribute, Domain domain){
        return "";
    }

    protected boolean validate(T domain){
        return true;
    }

    public Class<? extends WebPage> getBackPage() {
        return backPage;
    }

    public void setBackPage(Class<? extends WebPage> backPage) {
        this.backPage = backPage;
    }

    public PageParameters getBackPageParameters() {
        return backPageParameters;
    }

    public void setBackPageParameters(PageParameters backPageParameters) {
        this.backPageParameters = backPageParameters;
    }

    public Form getForm() {
        return form;
    }
}
