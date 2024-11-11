package com.ark.retailpulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI retailpulseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RetailPulse API")
                        .description("Documentation RetailPulse api")
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Retail Pulse admin")
                                .url("http://retailpulse.com")
                                .email("contact.retailpulse@gmail.com")))
                .addServersItem(new Server().url("/"));
    }
}