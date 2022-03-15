package ru.complitex.common.wicket.form;

import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.model.IModel;

/**
 * @author Ivanov Anatoliy
 */
public class IndicatingAjaxButton extends AjaxButton implements IAjaxIndicatorAware {
    private boolean ajax = true;

    private final AjaxIndicatorAppender indicatorAppender = new AjaxIndicatorAppender();

    public IndicatingAjaxButton(String id, IModel<String> model) {
        super(id, model);
        add(indicatorAppender);
    }

    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicatorAppender.getMarkupId() + (ajax ? "" : "_");
    }

    public boolean isAjax() {
        return ajax;
    }

    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    public AjaxIndicatorAppender getIndicatorAppender() {
        return indicatorAppender;
    }

    public void showIndicator(IPartialPageRequestHandler target) {
        target.appendJavaScript("$('#" + indicatorAppender.getMarkupId() + "').removeAttr('hidden')");
    }

    public void hideIndicator(IPartialPageRequestHandler target) {
        target.appendJavaScript("$('#" + indicatorAppender.getMarkupId() + "').attr('hidden', '')");
    }
}
