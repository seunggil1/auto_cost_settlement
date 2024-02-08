package com.seunggil.auto_cost_settlement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.seunggil.auto_cost_settlement.service.EmailService

@SpringBootApplication
class AutoCostSettlementApplication

fun main(args: Array<String>) {
	runApplication<AutoCostSettlementApplication>(*args)

	val emailService = EmailService()

	val host = "imap.naver.com"
	val user = "ksgg1"
	val password = "qpst0902!"


	emailService.readEmails(
			host, user,password
	)

}
