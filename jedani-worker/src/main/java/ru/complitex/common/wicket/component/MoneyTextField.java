package ru.complitex.common.wicket.component;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import ru.complitex.jedani.worker.page.resource.MaskMoneyJsResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 25.02.2019 11:59
 */
public class MoneyTextField<T> extends TextField<T> {
    public MoneyTextField(String id, IModel<T> model) {
        super(id, model);

        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forReference(MaskMoneyJsResourceReference.INSTANCE));

        response.render(OnDomReadyHeaderItem.forScript("$('#" + getMarkupId() + "').maskMoney()"));
    }
}
