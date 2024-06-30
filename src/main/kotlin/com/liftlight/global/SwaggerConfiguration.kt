package com.liftlight.global

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
class SwaggerConfiguration {

    @Bean
    @Primary
    @Profile("!live")
    fun dev(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("LiftLight API")
                .version("1.0")
                .description("LiftLight API")
        )
}