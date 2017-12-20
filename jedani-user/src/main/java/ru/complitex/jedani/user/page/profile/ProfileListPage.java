package ru.complitex.jedani.user.page.profile;

import ru.complitex.domain.page.DomainListPage;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
public class ProfileListPage extends DomainListPage{
    public ProfileListPage() {
        super("profile", ProfileEditPage.class);
    }
}
