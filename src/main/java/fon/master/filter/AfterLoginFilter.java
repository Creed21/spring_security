package fon.master.filter;

import fon.master.model.Role;
import fon.master.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AfterLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("==================AfterLoginFilter====================");

        log.info("request.getAuthType: {}", request.getAuthType());
        log.info("request.getUserPrincipal: {}", request.getUserPrincipal());
        log.info("request.getHeaderNames: {}", request.getHeaderNames());
        log.info("request.getRemoteUser: {}", request.getRemoteUser());

        if(request.getUserPrincipal() != null)
            log.info("request.prinicpal", request.getUserPrincipal().getName());

        log.info("================================================");

        var contextHolder = SecurityContextHolder.getContext();
        log.info("contextHolder: {}", request);
        log.info("contextHolder.getSession: {}", request.getSession());
        log.info("contextHolder.getAuthentication: {}", contextHolder.getAuthentication());
        log.info("contextHolder.getAuthentication.getAuthorities(): {}", contextHolder.getAuthentication().getAuthorities());

//        logKeycloakPrincipal(request, contextHolder);

        log.info("==================AfterLoginFilter END====================");

        filterChain.doFilter(request, response);
    }
}
