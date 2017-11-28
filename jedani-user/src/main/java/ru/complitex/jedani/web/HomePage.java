package ru.complitex.jedani.web;

import org.apache.wicket.markup.html.basic.Label;
import ru.complitex.jedani.mapper.UserMapper;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends BasePage{
    @Inject
    private UserMapper userMapper;

    public HomePage() {
        add(new Label("test", userMapper.ping()));
    }


}
