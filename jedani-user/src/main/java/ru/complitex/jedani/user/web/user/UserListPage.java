package ru.complitex.jedani.user.web.user;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.ui.datatable.DataProvider;
import ru.complitex.common.ui.datatable.FilterDataTable;
import ru.complitex.jedani.user.entity.User;
import ru.complitex.jedani.user.mapper.UserMapper;
import ru.complitex.jedani.user.web.BasePage;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 16:33
 */
public class UserListPage extends BasePage {
    @Inject
    private UserMapper userMapper;

    public UserListPage() {
        DataProvider<User> dataProvider = new DataProvider<User>() {
            @Override
            public Iterator<? extends User> iterator(long first, long count) {
                return userMapper.getUserList(FilterWrapper.of(getFilterState(), first, count)).iterator();
            }

            @Override
            public long size() {
                return userMapper.getUserListCount(FilterWrapper.of(getFilterState()));
            }
        };
        dataProvider.setFilterState(new User());

        List<IColumn<User, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("id"), "id"));
        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("lastName"), "lastName"));
        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("firstName"), "firstName"));
        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("secondName"), "secondName"));

        FilterForm<User> filterForm = new FilterForm<>("filterForm", dataProvider);
        filterForm.add(new FilterDataTable<>("dataTable", columns, dataProvider, filterForm,10));
        add(filterForm);
    }
}
