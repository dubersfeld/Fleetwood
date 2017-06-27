package com.dub.fleetwood.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import com.dub.fleetwood.site.OAuth2SigningRestTemplate;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true, order = 0, mode = AdviceMode.PROXY,
        proxyTargetClass = true
)
@PropertySource("classpath:sharewood.properties")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	@Value("${accessTokenUri}")
	String accessTokenUri;
	
	@Value("${userAuthorizationUri}")
	String userAuthorizationUri;
	
	@Value("${clientSecret}")
	String clientSecret;
	
	
	
    @Bean
   	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
   		return new PropertySourcesPlaceholderConfigurer();
   	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception
	{    
		return super.authenticationManagerBean();
	}
	
    @Bean
    public OAuth2WebSecurityExpressionHandler webSecurityExpressionHandler() {
    	return new OAuth2WebSecurityExpressionHandler();
    }
    
    @Bean
    protected SessionRegistry sessionRegistryImpl()
    {
        return new SessionRegistryImpl();
    }

    
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
			.withUser("Marissa").password("wombat").roles("USER")
			.and()
			.withUser("Steve").password("apple").roles("USER")
			.and()
			.withUser("Bill").password("orange").roles("USER");
	}

   
    
    @Bean
	public OAuth2ProtectedResourceDetails sharewood() {
		AuthorizationCodeResourceDetails details 
								= new AuthorizationCodeResourceDetails();
		details.setId("oAuth2ClientBean");
		details.setClientAuthenticationScheme(AuthenticationScheme.header);
		details.setClientId("Fleetwood");
		details.setClientSecret(clientSecret);
		details.setAuthenticationScheme(AuthenticationScheme.header);
		details.setGrantType("authorization_code");
		details.setAccessTokenUri(accessTokenUri);
		details.setUserAuthorizationUri(userAuthorizationUri);
		details.setScope(Arrays.asList("READ", "WRITE"));
		return details;
	}
  
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public DefaultAccessTokenRequest accessTokenRequestProxy(
    		@Value("#{request.parameterMap}") Map<String, String[]> parameters, 
    		@Value("#{request.getAttribute('currentUri')}") String currentUri) {
    	DefaultAccessTokenRequest requestProxy 
    						= new DefaultAccessTokenRequest(parameters);
    	requestProxy.setCurrentUri(currentUri);
    	return requestProxy;
    }

    @Bean OAuth2ClientContextFilter oAuth2ClientFilter() {
		return new OAuth2ClientContextFilter();
	}

 
    @Override
    public void configure(WebSecurity security)
    {
        security.ignoring().antMatchers("/resource/**");
    }

    @Override
    protected void configure(HttpSecurity security) 
    		throws Exception
    {
        security
                .authorizeRequests().expressionHandler(webSecurityExpressionHandler())                                                        	
                    .antMatchers("/login/**").permitAll()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/logout").permitAll()    
                    .antMatchers("/**").hasAuthority("ROLE_USER")                                                
                    .and().formLogin()
                    .loginPage("/login").failureUrl("/login?loginFailed")
                    .defaultSuccessUrl("/index")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
                .and().logout()
                    .logoutUrl("/logout")
                    .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                    .permitAll()
                .and().sessionManagement()
                    .sessionFixation().changeSessionId()
                    .maximumSessions(1).maxSessionsPreventsLogin(false)
                    .sessionRegistry(this.sessionRegistryImpl())
                .and().and().csrf()
                    .requireCsrfProtectionMatcher((r) -> {
                        String m = r.getMethod();
                        return !r.getServletPath().startsWith("/") &&
                                ("POST".equals(m) || "PUT".equals(m) ||
                                        "DELETE".equals(m) || "PATCH".equals(m));
                    });
        
        security.addFilterAfter(
				oAuth2ClientFilter(), 
				ExceptionTranslationFilter.class);			
    }
    
    @Configuration
    protected static class OAuth2ClientContextConfiguration {
    	
    	@Resource
    	@Qualifier("accessTokenRequest")
    	private AccessTokenRequest accessTokenRequestProxy;
    	
    	@Resource
    	@Qualifier("sharewood")
    	private OAuth2ProtectedResourceDetails sharewood;
    	
    	@Bean
    	@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    	public OAuth2ClientContext clientContextProxy() { 		
    		return new DefaultOAuth2ClientContext(accessTokenRequestProxy);
    	}
    
    	@Bean
    	public OAuth2SigningRestTemplate sharewoodRestTemplate() {
    	
    		OAuth2SigningRestTemplate sharewoodRestTemplate 
    									= new OAuth2SigningRestTemplate(sharewood, clientContextProxy());
    		return sharewoodRestTemplate;
    	}
    
    }
    
}
