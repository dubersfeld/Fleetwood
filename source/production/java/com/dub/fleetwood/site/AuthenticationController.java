package com.dub.fleetwood.site;

import com.dub.fleetwood.config.annotations.WebController;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * AuthenticationController is used by Spring Security for login
 * */
@WebController
public class AuthenticationController
{  
	@RequestMapping(
	    		value = { "/", "backHome" }, 
	    		method = RequestMethod.GET)   
	public String homePage(ModelMap model) {    
		model.addAttribute("user", SecurityUtils.getPrincipal());
	        
		return "index";    
	}
	  
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView login(Map<String, Object> model)
    {   
       model.put("loginForm", new LoginForm());
       
       return new ModelAndView("login");
    }
    
    @RequestMapping(value="logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) { 
            new SecurityContextLogoutHandler().logout(request, response, auth);
        	auth = SecurityContextHolder.getContext().getAuthentication();           
        }
        return "redirect:/login?logout";
    }

    public static class LoginForm
    {
        private String username;
        private String password;

        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }
    }
}
