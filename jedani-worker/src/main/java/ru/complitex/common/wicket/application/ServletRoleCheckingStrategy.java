package ru.complitex.common.wicket.application;

import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov
 * 09.04.2018 17:20
 */
public class ServletRoleCheckingStrategy implements IRoleCheckingStrategy {

    @Override
    public boolean hasAnyRole(Roles roles) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();

        if (roles != null){
            for (String role : roles){
                if (httpServletRequest.isUserInRole(role)){
                    return true;
                }
            }
        }

        return false;
    }
}
