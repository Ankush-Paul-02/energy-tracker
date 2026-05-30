package com.paul.insightservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static Contact getContact() {
        Contact contact = new Contact();
        contact.setName("Ankush Paul");
        contact.setUrl("https://www.paul.com");
        contact.setEmail("ankushpaulclg2002@gmail.com");
        return contact;
    }

    private static License getLicense() {
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");
        return license;
    }

    @Bean
    public OpenAPI insightServiceOpenApiDocs() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Insight Service API")
                                .description("Insight Service API for Energy Tracker.")
                                .contact(getContact())
                                .license(getLicense())
                                .version("1.0.0")
                );
    }
}