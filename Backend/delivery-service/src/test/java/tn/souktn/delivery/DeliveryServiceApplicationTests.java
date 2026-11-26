package tn.souktn.delivery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tn.souktn.delivery.entity.Delivery;
import tn.souktn.delivery.entity.DeliveryStatus;
import tn.souktn.delivery.entity.Driver;
import tn.souktn.delivery.entity.VehicleType;
import tn.souktn.delivery.service.DeliveryService;
import tn.souktn.delivery.service.DriverService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class DeliveryServiceApplicationTests {

    @Autowired
    private DriverService driverService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void driverAndDeliveryCrudAndAssociationsWork() {
        Driver driver = new Driver();
        driver.setFullName("Sami Ben Ali");
        driver.setPhone("+21620111222");
        driver.setEmail("sami.driver@souktn.tn");
        driver.setVehicleType(VehicleType.MOTORCYCLE);
        driver.setZone("Tunis");
        Driver savedDriver = driverService.createDriver(driver);

        Delivery delivery = new Delivery();
        delivery.setOrderId(101L);
        delivery.setCustomerName("Amira Trabelsi");
        delivery.setCustomerPhone("+21699333444");
        delivery.setShippingAddress("15 Avenue Habib Bourguiba");
        delivery.setCity("Tunis");
        Delivery savedDelivery = deliveryService.createDelivery(delivery);

        assertThat(savedDelivery.getTrackingNumber()).startsWith("TN-DEL-");
        assertThat(savedDelivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);

        Delivery assigned = deliveryService.assignDriver(savedDelivery.getId(), savedDriver.getId());
        assertThat(assigned.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
        assertThat(assigned.getDriver().getId()).isEqualTo(savedDriver.getId());

        Delivery delivered = deliveryService.updateStatus(
                savedDelivery.getId(),
                DeliveryStatus.DELIVERED,
                "Tunis Centre",
                "Delivered to customer"
        );
        assertThat(delivered.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(delivered.getDeliveredAt()).isNotNull();
        assertThat(deliveryService.getTrackingHistory(savedDelivery.getId())).hasSize(3);
    }

    @Test
    void actuatorHealthAndInfoEndpointsAreExposed() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/actuator/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.delivery").exists());

        mockMvc.perform(get("/actuator/info").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("delivery-service"));
    }
}
