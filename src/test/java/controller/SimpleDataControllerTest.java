package controller;

import fon.master.OAuthMain;
import fon.master.service.SimpleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OAuthMain.class)
public class SimpleDataControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    SimpleDataService simpleDataService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin_test", authorities = {"ROLE_ADMIN", "ROLE_USER", "ROLE_STUFF", "READ", "WRITE", "DELETE"})
    void addSimpleData() throws Exception {
        mockMvc.perform(post("/simpledata/add")
                        .param("value", "testController"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    @WithMockUser(username = "user_test", authorities = {"ROLE_USER", "READ"})
    void addSimpleDataForbidden() throws Exception {
        mockMvc.perform(post("/simpledata/add")
                        .param("value", "testController"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", authorities = {"ROLE_ADMIN", "DELETE"})
    void deleteDataSuccessfully() throws Exception {
    simpleDataService.findByValue("testController")
            .stream()
            .forEach(x ->
                    {
                        try {
                            mockMvc.perform(delete("/simpledata/delete")
                                            .param("id", String.valueOf(x.getId()))
                                    ).andExpect(status().isOk());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
    }

    @Test
    @WithMockUser(username = "user_test", authorities = {"ROLE_USER", "WRITE"})
    void deleteDataForbidden() throws Exception {
        simpleDataService.findByValue("testController")
                .stream()
                .forEach(x ->
                        {
                            try {
                                mockMvc.perform(delete("/simpledata/delete")
                                        .param("id", String.valueOf(x.getId()))
                                ).andExpect(status().isForbidden());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
    }
}
