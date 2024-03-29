package com.seunggil.auto_cost_settlement.controller

import com.seunggil.auto_cost_settlement.controller.request.SettlementRequest
import com.seunggil.auto_cost_settlement.controller.request.UserRequest
import com.seunggil.auto_cost_settlement.database.repository.SettlementHistoryRepository
import com.seunggil.auto_cost_settlement.service.settlement.EmailService
import com.seunggil.auto_cost_settlement.service.settlement.PdfService
import com.seunggil.auto_cost_settlement.service.user.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/settlements")
@RestController
class SettlementController(
    private val userService: UserService,
    private val settlementHistoryRepository: SettlementHistoryRepository,
    private val emailService: EmailService,
    private val pdfService: PdfService
) {
    @PostMapping("/")
    fun getSettlementList(@RequestBody userRequest: UserRequest): ResponseEntity<Any> {
        val historyIndexList = userService.getUser(userRequest.id, userRequest.pw)?.let { user ->
            settlementHistoryRepository.findByUserAccount(user)
        }?.let { settlementHistoryList ->
            settlementHistoryList.map {
                it.historyIndex
            }
        } ?: listOf<Long>()

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(historyIndexList)
    }

    @PostMapping("/update")
    fun updateSettlementList(@RequestBody userRequest: UserRequest): ResponseEntity<Any> {
        userService.getUser(userRequest.id, userRequest.pw)?.let { user ->
            val results = emailService.readEmails(user.host, user.id, userRequest.pw)
            for (result in results) {
                pdfService.savePdfFromEmail(user, result)
            }
        }

        return ResponseEntity.accepted().build()
    }

    @PostMapping("/search")
    fun getSettlementByDateAndCost(@RequestBody settlementRequest: SettlementRequest): ResponseEntity<Any> {
        val result = userService.getUser(settlementRequest.id, settlementRequest.pw)?.let {
            user -> settlementHistoryRepository.findByUserAndSettlement(
                userAccount = user, date= settlementRequest.date, cost = settlementRequest.cost)
        }
        return if(result.isNullOrEmpty()){
            ResponseEntity.noContent().build();

        }else{
            ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result[0].pdf)
        }
    }

    @GetMapping("/{historyIndex}")
    fun getPdf(@PathVariable historyIndex : Long) : ResponseEntity<Any>{
        val pdf = settlementHistoryRepository.findByHistoryIndex(historyIndex).pdf

        return if(pdf != null){
            ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf)
        }else{
            ResponseEntity
                .ok(ResponseEntity.EMPTY)
        }
    }
}
