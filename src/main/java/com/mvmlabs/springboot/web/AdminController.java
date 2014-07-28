package com.mvmlabs.springboot.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Only here to test access restrictions for Spring Security.
 * 
 * @author Mark Meany
 *
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private final Log log = LogFactory.getLog(this.getClass());
    
    @RequestMapping(value = "/greet/{name}", method = RequestMethod.GET)
    public String greet(@AuthenticationPrincipal UserDetails user, @PathVariable(value = "name") final String name, final Model model) {
        String username;
        if (user == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails)principal).getUsername();
            } else {
                username = "Who are you???";
            }
        } else {
            username = user.getUsername();
        }
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        log.info("The authenticated user '" + username + "' is masquarading as '" + name + "'.");
        return "site.admin.greet";
    }
}
