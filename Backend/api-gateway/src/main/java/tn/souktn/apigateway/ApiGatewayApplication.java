package tn.souktn.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("Payment", r -> r.path("/api/payments/**")
                .uri("lb://payment"))
            .route("Delivery", r -> r.path("/api/deliveries/**")
                .uri("lb://delivery"))
            .route("Catalog", r -> r.path("/api/catalog/**")
                .uri("lb://catalog"))
            .route("Order", r -> r.path("/api/orders/**")
                .uri("lb://order"))
            .route("User", r -> r.path("/api/users/**")
                .uri("lb://user"))
            .route("Notification", r -> r.path("/api/notifications/**")
                .uri("lb://notification"))
            .route("Dispute", r -> r.path("/api/disputes/**")
                .uri("lb://dispute"))
            .build();
    }
}
