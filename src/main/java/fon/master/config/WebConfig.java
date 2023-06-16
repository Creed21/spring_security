package fon.master.config;

import fon.master.filter.AfterLoginFilter;
import fon.master.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/**
 * prePostEnabled -  property enables Spring Security pre/post annotations.
 * securedEnabled - property determines if the @Secured annotation should be enabled.
 * jsr250Enabled - property allows us to use the @RoleAllowed annotation.
 * */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true, // -  property enables Spring Security pre/post annotations.
        securedEnabled = true, // - property determines if the @Secured annotation should be enabled.
        jsr250Enabled = true) // - property allows us to use the @RoleAllowed annotation.
public class WebConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService userDetailsService;
    private final ContextSource contextSource;

    @Autowired
    public WebConfig(MyUserDetailsService userDetailsService, ContextSource contextSource, PasswordEncoder passwordEncoder){
        this.userDetailsService = userDetailsService;
        this.contextSource = contextSource;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.addFilterAfter(new AfterLoginFilter(), AuthorizationFilter.class);

        http.authorizeRequests()
                .antMatchers("/hello/admin/*").hasRole("ADMIN")
//                .antMatchers("/hello/user/*").hasAnyRole("ADMIN", "STUFF", "USER")
                .antMatchers("/simpledata/*").hasAnyRole("ADMIN", "STUFF", "USER")
                .antMatchers("/login").permitAll();

        http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin()
                .and().httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        configureInMemoryAuth(auth);
        configureUserDetailsService(auth);
        configureLDAP(auth);
    }

    /**
     * kada se koristi inMemoryAuthentication() i dodaju se i roles(String ...) i authorities(String ...)
     * ona metoda koja je poslednja u nizu zgazi sve prethodne
     * na kraju se i roles i authorities svode samo na authorities !!!!
     * @param auth
     * @throws Exception
     */
    private void configureInMemoryAuth(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("user_inmemory")
                    .password(passwordEncoder.encode("1"))
                    .roles("USER", "STUFF") // ovo ce biti pregazno nakon poziva naredne metode
                    .authorities("ROLE_USER", "ROLE_STUFF", "READ")
                .and()
                .withUser("admin_inmemory")
                    .password(passwordEncoder.encode("1"))
//                .roles("ADMIN", "USER", "STUFF")
                    .authorities("ROLE_ADMIN", "ROLE_USER", "ROLE_STUFF", "READ", "WRITE", "DELETE", "UPDATE")
                ;
    }

    private void configureUserDetailsService(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    private void configureLDAP(AuthenticationManagerBuilder auth) throws Exception {
        CustomLdapAuthoritiesPopular ldapAuthoritiesPopulator = new CustomLdapAuthoritiesPopular(contextSource, "ou=groups");

        ldapAuthoritiesPopulator.setDefaultRole("ROLE_LDAP");

        auth.ldapAuthentication()
                .userDnPatterns("uid={0},ou=users")
                .groupSearchBase("ou=groups")
                .userSearchBase("ou=users")
                .userSearchFilter("(uid={0})")
                .groupSearchFilter("member={0}")
                .groupRoleAttribute("cn")
                .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator)
                .contextSource()
                .url("ldap://localhost:10390/dc=master,dc=fon");
    }

}
