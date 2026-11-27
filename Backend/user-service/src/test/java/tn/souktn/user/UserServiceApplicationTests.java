package tn.souktn.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.souktn.user.repository.UserProfileRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository repository;

    @BeforeEach
    void clearProfiles() {
        repository.deleteAll();
    }

    @Test
    void managesUserProfilesThroughCrudEndpoints() throws Exception {
        String profile = """
                {
                  "id": "keycloak-user-1",
                  "firstName": "Ahmed",
                  "lastName": "Test",
                  "email": "ahmed@example.com",
                  "phone": "+21612345678"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("keycloak-user-1"));

        mockMvc.perform(get("/api/users/keycloak-user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ahmed@example.com"));

        mockMvc.perform(put("/api/users/keycloak-user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ahmed",
                                  "lastName": "Updated",
                                  "email": "ahmed@example.com",
                                  "phone": "+21698765432"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("keycloak-user-1"));

        mockMvc.perform(delete("/api/users/keycloak-user-1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/keycloak-user-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsDuplicateEmails() throws Exception {
        String firstProfile = """
                {
                  "id": "keycloak-user-2",
                  "firstName": "First",
                  "lastName": "User",
                  "email": "same@example.com"
                }
                """;
        String secondProfile = """
                {
                  "id": "keycloak-user-3",
                  "firstName": "Second",
                  "lastName": "User",
                  "email": "same@example.com"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstProfile)).andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondProfile))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "keycloak-user-2",
                                  "firstName": "Duplicate",
                                  "lastName": "ID",
                                  "email": "different@example.com"
                                }
                                """))
                .andExpect(status().isConflict());
    }
}
