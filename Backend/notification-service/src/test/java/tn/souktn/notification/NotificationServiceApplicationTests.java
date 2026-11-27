package tn.souktn.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import tn.souktn.notification.repository.NotificationRepository;
import tn.souktn.notification.user.repository.UserProfileRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void exposesSwaggerApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("SoukTn Notification Service API"))
                .andExpect(jsonPath("$.paths['/api/notifications']").exists())
                .andExpect(jsonPath("$.paths['/api/reviews']").exists())
                .andExpect(jsonPath("$.paths['/api/users']").exists());
    }

    @Test
    void createsNotificationFromSwaggerExampleWithoutAcceptingGeneratedFields() throws Exception {
        notificationRepository.deleteAll();
        mockMvc.perform(post("/api/notifications")
                        .contentType("application/json")
                        .content("""
                                {
                                  "id": 0,
                                  "recipientId": "test-recipient",
                                  "title": "Test notification",
                                  "message": "Test message",
                                  "actionUrl": "/orders/1",
                                  "channel": "IN_APP",
                                  "status": "UNREAD",
                                  "createdAt": "2026-11-27T12:23:15.111Z",
                                  "readAt": "2026-11-27T12:23:15.111Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(not(0)))
                .andExpect(jsonPath("$.status").value("UNREAD"))
                .andExpect(jsonPath("$.readAt").value(nullValue()));
    }

    @Test
    void managesUserProfilesThroughCrudEndpoints() throws Exception {
        userProfileRepository.deleteAll();
        String createRequest = """
                {
                  "id": "keycloak-user-1",
                  "firstName": "Ahmed",
                  "lastName": "Test",
                  "email": "ahmed@example.com",
                  "phone": "+21612345678"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("keycloak-user-1"));

        mockMvc.perform(get("/api/users/keycloak-user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ahmed@example.com"));

        mockMvc.perform(put("/api/users/keycloak-user-1")
                        .contentType("application/json")
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
}
