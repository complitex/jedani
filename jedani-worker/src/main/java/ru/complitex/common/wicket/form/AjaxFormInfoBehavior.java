package ru.complitex.common.wicket.form;

import de.agilecoders.wicket.jquery.JQuery;
import de.agilecoders.wicket.jquery.function.Function;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * @author Anatoly A. Ivanov
 * 23.02.2019 19:24
 */
public class AjaxFormInfoBehavior extends AjaxFormComponentUpdatingBehavior {
    private boolean error;
    private boolean errorRendered;

    public AjaxFormInfoBehavior() {
        super("change");
    }

    @Override
    public void onConfigure(Component component) {
        component.setOutputMarkupId(true);

        checkError(component);
    }

    private void checkError(Component component){
        FeedbackMessage feedbackMessage = component.getFeedbackMessages().first(FeedbackMessage.ERROR);

        if (error = feedbackMessage != null){
            feedbackMessage.markRendered();
        }
    }

    @Override
    public void beforeRender(Component component) {
        if (error) {
            component.getResponse().write("<div class=\"has-error\">");

            errorRendered = true;
        }
    }

    @Override
    protected void onComponentRendered() {
        if (error){
            getComponent().getResponse().write("</div>");
        }
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        Component component = getComponent();

        checkError(component);

        if (!error && errorRendered){
            target.appendJavaScript(JQuery.$(component).closest(".has-error")
                    .chain(new Function("removeClass", "has-error"))
                    .build());

            errorRendered = false;
        }
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        if (component.isEnabledInHierarchy()){
            response.render(OnDomReadyHeaderItem.forScript(getCallbackScript(component).toString()));
        }
    }

    public boolean isError() {
        return error;
    }
}
