package com.seunggil.auto_cost_settlement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.seunggil.auto_cost_settlement.service.EmailService
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AutoCostSettlementApplication

fun main(args: Array<String>) {
	runApplication<AutoCostSettlementApplication>(*args)
}
