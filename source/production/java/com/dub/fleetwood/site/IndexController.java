package com.dub.fleetwood.site;

import com.dub.fleetwood.config.annotations.WebController;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/** 
 * IndexController handle the home page 
 * */
@WebController
public class IndexController
{
    @RequestMapping({ "/", "/backHome", "index" })
    public String index(Model model)
    {    	
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		model.addAttribute("username", username);
    	
        return "index";
    }
}
