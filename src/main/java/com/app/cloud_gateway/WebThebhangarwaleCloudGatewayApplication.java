package com.app.cloud_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class WebThebhangarwaleCloudGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebThebhangarwaleCloudGatewayApplication.class, args);
	}

}
