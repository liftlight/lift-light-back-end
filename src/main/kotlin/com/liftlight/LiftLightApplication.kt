package com.liftlight

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class LiftLightApplication

fun main(args: Array<String>) {
    runApplication<LiftLightApplication>(*args)
}
