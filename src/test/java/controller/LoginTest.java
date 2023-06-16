package controller;

import fon.master.OAuthMain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = OAuthMain.class)
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void invalidLogin() throws Exception {
        MvcResult result = mvc.perform(
                post("/login")
                    .with(httpBasic("bad_cred_user", "invalid_password"))
                ).andExpect(status().isFound())
                .andReturn();

        String redirectedUrl =result.getResponse().getHeader("Location");
        assert(redirectedUrl != null);
        assert(redirectedUrl.endsWith("/login?error"));

        Assert.notNull(redirectedUrl, "Redirect on /login?error didn't happened!");

        mvc.perform(get(redirectedUrl))
                .andExpect(status().isOk());
    }

    @Test
    public void validLoginInMemory() throws Exception {
        mvc.perform(
                post("/login")
                        .with(httpBasic("admin_inmemory", "1"))
        ).andExpect(status().isFound());
    }

    @Test
    public void validLoginDB() throws Exception {
        mvc.perform(
                post("/login")
                        .with(httpBasic("admin_pg", "admin_pg"))
        ).andExpect(status().isFound());
    }

    @Test
    public void validLoginLDAP() throws Exception {
        mvc.perform(
                post("/login")
                    .with(httpBasic("aca", "1"))
        ).andExpect(status().isFound());
    }

    @Test
    void testFormLogin() throws Exception {
        mvc.perform(formLogin("/login")
                .user("username","aca")
                .password("password","1")
                )
            .andExpect(status().isFound())
                ;

        mvc.perform(formLogin("/login")
                        .user("username","admin_inmemory")
                        .password("password","1")
                        .loginProcessingUrl("/login")
                )
                .andExpect(status().isFound())
        ;
    }

    @Test
    void testInvalidFormLogin() throws Exception {
        mvc.perform(formLogin("/login")
                        .user("username","aca")
                        .password("password","112121")
                        .loginProcessingUrl("/login?error")
                )
                .andExpect(status().isFound());

        mvc.perform(formLogin("/login")
                        .user("username","aca")
                        .password("password","112121")
                        .loginProcessingUrl("/")
                )
                .andExpect(status().isUnauthorized())
        ;

    }

}
