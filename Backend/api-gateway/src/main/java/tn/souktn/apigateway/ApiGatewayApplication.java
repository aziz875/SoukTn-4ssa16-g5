package tn.souktn.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import tn.souktn.loadbalancer.NotificationRandomLoadBalancerConfiguration;

@SpringBootApplication
@EnableDiscoveryClient
@LoadBalancerClient(
        name = "NOTIFICATION",
        configuration = NotificationRandomLoadBalancerConfiguration.class
)
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
