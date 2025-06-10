package com.example.expense_tracker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI mySwaggerConfig() {
		return new OpenAPI().info(
				new Info().title("Expense tracker expalanation").description("By Chayan")
			)
				//.servers(List.of())
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.in(SecurityScheme.In.HEADER)
						.name("Authorization")))
				;
	}
	
}
