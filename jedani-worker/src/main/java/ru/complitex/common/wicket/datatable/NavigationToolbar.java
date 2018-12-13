package ru.complitex.common.wicket.datatable;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigatorLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Anatoly A. Ivanov
 * 12.12.2018 21:36
 */
public class NavigationToolbar extends AbstractToolbar {
    public static final MetaDataKey<HashMap<String, Long>> ITEMS_PER_PAGE = new MetaDataKey<HashMap<String, Long>>() {};

    public NavigationToolbar(DataTable<?, ?> table, String tableKey) {
        super(table);

        WebMarkupContainer span = new WebMarkupContainer("span");
        add(span);
        span.add(AttributeModifier.replace("colspan", () -> String.valueOf(table.getColumns().size())));
        span.add(new NavigatorLabel("label", table));
        span.add(new BootstrapAjaxPagingNavigator("navigator", table));

        HashMap<String, Long> map = getSession().getMetaData(ITEMS_PER_PAGE);

        if(map == null){
            map = new HashMap<>();

            getSession().setMetaData(ITEMS_PER_PAGE, map);
        }

        Long itemsPerPages = map.get(tableKey);

        if (itemsPerPages == null){
            itemsPerPages = table.getItemsPerPage();

            map.put(tableKey, itemsPerPages);
        }else{
            table.setItemsPerPage(itemsPerPages);
        }

        IModel<Long> itemsPerPageModel = Model.of(itemsPerPages);

        span.add(new BootstrapSelect<>("size", itemsPerPageModel, Arrays.asList(5L, 10L, 15L, 20L, 25L, 50L, 100L))
                .add(new OnChangeAjaxBehavior() {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        table.setItemsPerPage(itemsPerPageModel.getObject());

                        getSession().getMetaData(ITEMS_PER_PAGE).put(tableKey, itemsPerPageModel.getObject());

                        target.add(table);
                    }
                }));
    }
}
