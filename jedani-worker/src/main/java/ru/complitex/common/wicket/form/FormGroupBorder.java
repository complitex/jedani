package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.function.Function;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.Objects;

/**
 * @author Anatoly A. Ivanov
 * 23.02.2019 22:34
 */
public class FormGroupBorder extends Border {
    private Label info;

    public FormGroupBorder(String id, IModel<String> labelModel) {
        super(id);

        setOutputMarkupId(true);

        addToBorder(new Label("label", labelModel){
            @Override
            protected void onComponentTag(ComponentTag tag) {
                if (isRequired()){
                    super.onComponentTag(tag);

                    tag.put("required", "required");
                }
            }
        });

        info = new Label("info", new Model<>());
        info.setOutputMarkupId(true);

        addToBorder(info);
    }

    public FormGroupBorder(String id) {
        this(id, new ResourceModel(id));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        info.setDefaultModelObject(null);

        getBodyContainer().streamChildren()
                .filter(c -> c instanceof FormComponent)
                .map(c -> c.getFeedbackMessages().first(FeedbackMessage.ERROR))
                .filter(Objects::nonNull)
                .findAny()
                .ifPresent(m -> {
                    info.setDefaultModelObject(m.getMessage());
                    m.markRendered();
                });
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (info.getDefaultModelObject() != null) {
            tag.put("class", "has-error");
        }
    }

    public String getRemoveErrorJs(){
        return JQuery.$(this)
                .closest(".has-error")
                .chain(new Function("removeClass", "has-error"))
                .build() + "; " +

                JQuery.$(info)
                        .chain(new Function("hide"))
                        .build();

    }

    protected boolean isRequired(){
        return false;
    }
}
