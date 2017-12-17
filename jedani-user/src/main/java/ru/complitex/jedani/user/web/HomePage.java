package ru.complitex.jedani.user.web;

import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.user.mapper.UserMapper;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends BasePage{
    @Inject
    private UserMapper userMapper;

    public HomePage() {
        add(new Label("test", new Date()));
    }


}
