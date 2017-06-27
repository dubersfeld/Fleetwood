package com.dub.fleetwood.config;

import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Order(2)
public class SecurityBootstrap extends AbstractSecurityWebApplicationInitializer
{
    @Override
    protected boolean enableHttpSessionEventPublisher()
    {
    	System.out.println("SecurityBootstrap completed");
        return true;
    }
}
