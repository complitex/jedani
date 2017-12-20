package ru.complitex.jedani.user.page.profile;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.domain.page.DomainEditPage;

/**
 * @author Anatoly A. Ivanov
 * 20.12.2017 7:11
 */
public class ProfileEditPage extends DomainEditPage{
    public ProfileEditPage(PageParameters parameters) {
        super("profile", parameters, ProfileListPage.class);
    }
}
