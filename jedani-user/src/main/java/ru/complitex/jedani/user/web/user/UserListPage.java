package ru.complitex.jedani.user.web.user;

import ru.complitex.jedani.user.web.BasePage;

/**
 * @author Anatoly A. Ivanov
 * 28.11.2017 16:33
 */
public class UserListPage extends BasePage {
//    @Inject
//    private UserMapper userMapper;

    public UserListPage() {
//        DataProvider<User> dataProvider = new DataProvider<User>() {
//            @Override
//            public Iterator<? extends User> iterator(long first, long count) {
//                return userMapper.getUserList(FilterWrapper.of(getFilterState(), first, count)).iterator();
//            }
//
//            @Override
//            public long size() {
//                return userMapper.getUserListCount(FilterWrapper.of(getFilterState()));
//            }
//        };
//        dataProvider.setFilterState(new User());
//
//        List<IColumn<User, String>> columns = new ArrayList<>();
//        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("id"), "id"));
//        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("lastName"), "lastName"));
//        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("firstName"), "firstName"));
//        columns.add(new TextFilteredPropertyColumn<User, String, String>(new ResourceModel("secondName"), "secondName"));
//
//        FilterForm<User> filterForm = new FilterForm<>("filterForm", dataProvider);
//        filterForm.add(new FilterDataTable<>("dataTable", columns, dataProvider, filterForm,10));
//        add(filterForm);
    }
}
