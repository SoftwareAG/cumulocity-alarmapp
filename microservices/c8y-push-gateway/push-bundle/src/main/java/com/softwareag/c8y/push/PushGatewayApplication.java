package com.softwareag.c8y.push;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cumulocity.microservice.autoconfigure.MicroserviceApplication;
import com.cumulocity.microservice.context.annotation.EnableContextSupport;

@MicroserviceApplication
@EnableContextSupport
@EnableScheduling
@ComponentScan(basePackages = { 
		"com.softwareag.c8y.push.registration", 
		"com.softwareag.c8y.push.sender",
		"com.softwareag.c8y.push.template", 
		"com.softwareag.c8y.push.commons"
	}
)
public class PushGatewayApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		System.setProperty("spring.main.allow-circular-references", "true");
		SpringApplication.run(PushGatewayApplication.class, args);
	}
}
