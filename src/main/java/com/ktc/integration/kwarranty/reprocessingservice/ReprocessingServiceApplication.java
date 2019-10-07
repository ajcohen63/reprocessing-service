package com.ktc.integration.kwarranty.reprocessingservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class ReprocessingServiceApplication {
    private static final Logger log = LogManager.getLogger(ReprocessingServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReprocessingServiceApplication.class, args);
	}
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
			//	.antMatchers("/api/**", "/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**").anonymous()
				.apis(RequestHandlerSelectors.basePackage("com.ktc.integration.kwarranty.reprocessingservice")).build();
	}
}
