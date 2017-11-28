package ru.complitex.jedani.web.user;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.model.Model;
import ru.complitex.jedani.entity.User;
import ru.complitex.jedani.web.BasePage;
import ru.complitex.ui.datatable.DataProvider;
import ru.complitex.ui.datatable.FilterDataTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 16:33
 */
public class UserListPage extends BasePage {
    public UserListPage() {
        DataProvider<User> dataProvider = new DataProvider<User>() {
            @Override
            public Iterator<? extends User> iterator(long first, long count) {
                return Collections.singleton(new User(){{setId(1);}}).iterator();
            }

            @Override
            public long size() {
                return 1;
            }
        };

        List<IColumn<User, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredPropertyColumn<User, String, String>(Model.of("id"), "id"));

        FilterForm<User> filterForm = new FilterForm<>("filterForm", dataProvider);
        filterForm.add(new FilterDataTable<>("dataTable", columns, dataProvider, filterForm,10));
        add(filterForm);
    }
}
