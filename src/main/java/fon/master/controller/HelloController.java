package fon.master.controller;

import fon.master.config.securityAnnotations.IsAdmin;
import fon.master.config.securityAnnotations.IsStuff;
import fon.master.config.securityAnnotations.IsUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/hello")
public class HelloController {

//    @PreAuthorize("hasAuthority('UPDATE')")
    @GetMapping("/")
    public String hello(Authentication authentication, Principal principal) {
        return "hello: "+principal.getName()+", "+ SecurityContextHolder.getContext().getAuthentication()
                +"\n================================================================="
                +"\n"+authentication.getCredentials()+"\n"+authentication.getDetails()
                +"\n"+authentication.getPrincipal();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin")
    public String helloAdmin(Principal principal) {
        return "hello admin account: "+principal.getName()+", "+ SecurityContextHolder.getContext().getAuthentication();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_STUFF"})
    @IsUser
    @GetMapping("/user")
    public String helloUser(Principal principal) {
        return "hello user account: "+principal.getName()+", "+ SecurityContextHolder.getContext().getAuthentication();
    }

}
