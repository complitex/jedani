package ru.complitex.common.wicket.application;

import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;

/**
 * @author Anatoly A. Ivanov
 * 09.04.2018 17:23
 */
public class ServletAuthorizationStrategy extends RoleAuthorizationStrategy {

    public ServletAuthorizationStrategy() {
        super(new ServletRoleCheckingStrategy());
    }
}
